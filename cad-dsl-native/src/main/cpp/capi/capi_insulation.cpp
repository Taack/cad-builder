//
// C API: clearances and creepage distances between the conductors of an assembly, as IEC 60664-1 measures them, on
// meshes of its parts.
//
// - A clearance is the shortest path through the air: straight segments bending on the convex edges of the meshes.
//   Every point of a path of length L is closer than L to both conductors added: the points spaced along those edges
//   within such a bound are searched by A*, the bound larger until a path shorter than it is found. A segment is
//   tested against the triangles only when the search takes it. The bends then slide along their edges to the
//   shortest path.
// - A creepage distance is the shortest path along the surfaces of the insulating parts, their triangles split to a
//   few steps wide: across the triangles, between points spaced along their edges, or through the air, across the
//   grooves narrower than X between faces at less than 80° and the joints between parts. Its bends then slide along
//   the edges, its ends and those of the jumps inside the faces too.
//
// The searches share the meshes and run in parallel. They run unoptimized otherwise: the library is built without
// optimization.
//
#if defined(__GNUC__) && !defined(__clang__)
#pragma GCC optimize("O2")
#endif

#include "capi_internal.hxx"

#include <BRepBuilderAPI_Copy.hxx>
#include <BRepExtrema_DistShapeShape.hxx>
#include <BRepMesh_IncrementalMesh.hxx>
#include <BRep_Builder.hxx>
#include <BRep_Tool.hxx>
#include <Poly_Triangulation.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Compound.hxx>
#include <TopoDS_Face.hxx>

#include <array>
#include <chrono>
#include <cmath>
#include <cstdint>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <queue>
#include <stdexcept>
#include <unordered_map>
#include <vector>

namespace {

const double INF = std::numeric_limits<double>::infinity();
// The points of the graph stand this far out of the surfaces, for the segments between them not to graze the faces
const double LIFT = 1.0e-4;
// No group: the triangles of every group are obstacles
const int NO_GROUP = -100;

struct V3 {
    double x = 0, y = 0, z = 0;
};

inline V3 operator+(V3 a, V3 b) { return {a.x + b.x, a.y + b.y, a.z + b.z}; }
inline V3 operator-(V3 a, V3 b) { return {a.x - b.x, a.y - b.y, a.z - b.z}; }
inline V3 operator*(V3 a, double k) { return {a.x * k, a.y * k, a.z * k}; }
inline double dot(V3 a, V3 b) { return a.x * b.x + a.y * b.y + a.z * b.z; }
inline V3 cross(V3 a, V3 b) { return {a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x}; }
inline double norm(V3 a) { return std::sqrt(dot(a, a)); }
inline double dist(V3 a, V3 b) { return norm(a - b); }
inline V3 unit(V3 a) {
    double n = norm(a);
    return n > 0 ? a * (1.0 / n) : V3{};
}
inline V3 lerp(V3 a, V3 b, double t) { return a + (b - a) * t; }

struct Box {
    V3 lo{INF, INF, INF}, hi{-INF, -INF, -INF};

    void add(V3 p) {
        lo = {std::min(lo.x, p.x), std::min(lo.y, p.y), std::min(lo.z, p.z)};
        hi = {std::max(hi.x, p.x), std::max(hi.y, p.y), std::max(hi.z, p.z)};
    }

    void add(const Box &b) {
        add(b.lo);
        add(b.hi);
    }

    double distance2(V3 p) const {
        double dx = std::max({lo.x - p.x, 0.0, p.x - hi.x});
        double dy = std::max({lo.y - p.y, 0.0, p.y - hi.y});
        double dz = std::max({lo.z - p.z, 0.0, p.z - hi.z});
        return dx * dx + dy * dy + dz * dz;
    }

    // Whether the segment p + t d, t in [0, 1], crosses the box
    bool crossed(V3 p, V3 inv) const {
        double t0 = 0, t1 = 1;
        const double o[3] = {p.x, p.y, p.z}, iv[3] = {inv.x, inv.y, inv.z};
        const double l[3] = {lo.x, lo.y, lo.z}, h[3] = {hi.x, hi.y, hi.z};
        for (int k = 0; k < 3; k++) {
            double a = (l[k] - o[k]) * iv[k], b = (h[k] - o[k]) * iv[k];
            if (std::isnan(a) || std::isnan(b)) {
                // parallel to the slab, on its boundary: inside if the origin is
                if (o[k] < l[k] || o[k] > h[k]) return false;
                continue;
            }
            if (a > b) std::swap(a, b);
            t0 = std::max(t0, a);
            t1 = std::min(t1, b);
            if (t0 > t1) return false;
        }
        return true;
    }
};

// The closest point of the triangle abc to p (Ericson, Real-Time Collision Detection, 5.1.5)
V3 closestOnTriangle(V3 p, V3 a, V3 b, V3 c) {
    V3 ab = b - a, ac = c - a, ap = p - a;
    double d1 = dot(ab, ap), d2 = dot(ac, ap);
    if (d1 <= 0 && d2 <= 0) return a;
    V3 bp = p - b;
    double d3 = dot(ab, bp), d4 = dot(ac, bp);
    if (d3 >= 0 && d4 <= d3) return b;
    double vc = d1 * d4 - d3 * d2;
    if (vc <= 0 && d1 >= 0 && d3 <= 0) return a + ab * (d1 / (d1 - d3));
    V3 cp = p - c;
    double d5 = dot(ab, cp), d6 = dot(ac, cp);
    if (d6 >= 0 && d5 <= d6) return c;
    double vb = d5 * d2 - d1 * d6;
    if (vb <= 0 && d2 >= 0 && d6 <= 0) return a + ac * (d2 / (d2 - d6));
    double va = d3 * d6 - d5 * d4;
    if (va <= 0 && d4 - d3 >= 0 && d5 - d6 >= 0) return b + (c - b) * ((d4 - d3) / ((d4 - d3) + (d5 - d6)));
    double denom = 1.0 / (va + vb + vc);
    return a + ab * (vb * denom) + ac * (vc * denom);
}

// The triangles of the parts, merged where the faces of a part share their edges
struct Mesh {
    std::vector<V3> points, pointNormals;
    std::vector<int> pointGroups;
    std::vector<std::array<int, 3>> triangles;
    std::vector<V3> triangleNormals;
    std::vector<int> triangleGroups, triangleShapes;
    int shapes = 0;

    struct Edge {
        int a, b;
        std::vector<int> triangles;
        bool convex;    // or bounding a single triangle, or more than two
        V3 normal;      // the bisector of the normals of its triangles, outward
    };
    std::vector<Edge> edges;
    std::vector<std::array<int, 3>> triangleEdges;
    std::vector<std::vector<int>> pointTriangles, pointEdges;

    // Adds the faces of shape, triangulated with deflection, its points welded where closer than weld
    void add(const TopoDS_Shape &original, int group, double deflection) {
        // a copy, not to leave triangulations on the shapes of the model
        TopoDS_Shape shape = BRepBuilderAPI_Copy(original, true, false).Shape();
        BRepMesh_IncrementalMesh mesher(shape, deflection, false, 0.2, true);
        const double weld = 1.0e-6, cell = 1.0e-4;
        std::unordered_map<std::int64_t, std::vector<int>> grid;
        auto key = [&](std::int64_t i, std::int64_t j, std::int64_t k) {
            return (i * 73856093) ^ (j * 19349663) ^ (k * 83492791);
        };
        auto weldPoint = [&](V3 p) -> int {
            std::int64_t i = (std::int64_t) std::floor(p.x / cell), j = (std::int64_t) std::floor(p.y / cell),
                    k = (std::int64_t) std::floor(p.z / cell);
            for (int di = -1; di <= 1; di++)
                for (int dj = -1; dj <= 1; dj++)
                    for (int dk = -1; dk <= 1; dk++) {
                        auto found = grid.find(key(i + di, j + dj, k + dk));
                        if (found == grid.end()) continue;
                        for (int q : found->second) if (dist(points[q], p) < weld) return q;
                    }
            points.push_back(p);
            pointGroups.push_back(group);
            grid[key(i, j, k)].push_back((int) points.size() - 1);
            return (int) points.size() - 1;
        };
        for (TopExp_Explorer faces(shape, TopAbs_FACE); faces.More(); faces.Next()) {
            const TopoDS_Face &face = TopoDS::Face(faces.Current());
            TopLoc_Location location;
            occ::handle<Poly_Triangulation> triangulation = BRep_Tool::Triangulation(face, location);
            if (triangulation.IsNull()) continue;
            const gp_Trsf &trsf = location.Transformation();
            std::vector<int> welded(triangulation->NbNodes() + 1);
            for (int i = 1; i <= triangulation->NbNodes(); i++) {
                gp_Pnt p = triangulation->Node(i).Transformed(trsf);
                welded[i] = weldPoint({p.X(), p.Y(), p.Z()});
            }
            bool reversed = face.Orientation() == TopAbs_REVERSED;
            for (int i = 1; i <= triangulation->NbTriangles(); i++) {
                int n1, n2, n3;
                triangulation->Triangle(i).Get(n1, n2, n3);
                if (reversed) std::swap(n2, n3);
                std::array<int, 3> t = {welded[n1], welded[n2], welded[n3]};
                V3 n = cross(points[t[1]] - points[t[0]], points[t[2]] - points[t[0]]);
                if (t[0] == t[1] || t[1] == t[2] || t[0] == t[2] || norm(n) == 0) continue;
                triangles.push_back(t);
                triangleNormals.push_back(unit(n));
                triangleGroups.push_back(group);
                triangleShapes.push_back(shapes);
            }
        }
        shapes++;
    }

    // The edges, their convexity, and the normals of the points, once every part is added
    void link() {
        pointTriangles.assign(points.size(), {});
        pointEdges.assign(points.size(), {});
        std::vector<V3> normals(points.size());
        std::map<std::pair<int, int>, int> byPoints;
        triangleEdges.resize(triangles.size());
        for (int t = 0; t < (int) triangles.size(); t++) {
            for (int k = 0; k < 3; k++) {
                int a = triangles[t][k], b = triangles[t][(k + 1) % 3], c = triangles[t][(k + 2) % 3];
                pointTriangles[a].push_back(t);
                // weighted by the angle at the point, not to tilt the normal toward the finely meshed faces
                V3 u = unit(points[b] - points[a]), v = unit(points[c] - points[a]);
                normals[a] = normals[a] + triangleNormals[t] * std::acos(std::max(-1.0, std::min(1.0, dot(u, v))));
                auto edgeKey = std::make_pair(std::min(a, b), std::max(a, b));
                auto found = byPoints.find(edgeKey);
                int e;
                if (found == byPoints.end()) {
                    e = (int) edges.size();
                    edges.push_back({edgeKey.first, edgeKey.second, {}, true, {}});
                    byPoints[edgeKey] = e;
                    pointEdges[edgeKey.first].push_back(e);
                    pointEdges[edgeKey.second].push_back(e);
                } else {
                    e = found->second;
                }
                edges[e].triangles.push_back(t);
                triangleEdges[t][k] = e;
            }
        }
        pointNormals.resize(points.size());
        for (size_t p = 0; p < points.size(); p++) pointNormals[p] = unit(normals[p]);
        for (Edge &e : edges) {
            if (e.triangles.size() != 2) {
                e.normal = unit(pointNormals[e.a] + pointNormals[e.b]);
                continue;
            }
            int t0 = e.triangles[0], t1 = e.triangles[1];
            V3 n0 = triangleNormals[t0], n1 = triangleNormals[t1];
            int opposite = triangles[t1][0] + triangles[t1][1] + triangles[t1][2] - e.a - e.b;
            double below = dot(n0, points[opposite] - points[e.a]);
            e.convex = below < -1.0e-9 && dot(n0, n1) < 1.0 - 1.0e-7;
            e.normal = unit(n0 + n1);
            if (norm(e.normal) == 0) e.normal = n0;
        }
    }
};

// A bounding volume hierarchy of triangles, for the segments crossing them and the closest points
class TriangleTree {
public:
    void build(const Mesh &mesh, std::vector<int> selected) {
        this->mesh = &mesh;
        order = std::move(selected);
        nodes.clear();
        if (order.empty()) return;
        centers.resize(mesh.triangles.size());
        for (int t : order) {
            const auto &tri = mesh.triangles[t];
            centers[t] = (mesh.points[tri[0]] + mesh.points[tri[1]] + mesh.points[tri[2]]) * (1.0 / 3);
        }
        split(0, (int) order.size());
        std::vector<V3>().swap(centers);
    }

    // Whether the segment pq goes through a part, the parts of the groups ignore1 and ignore2 left out: whether it
    // crosses a triangle farther than skip from its ends, or, between them, is inside a part. The ends are on the
    // surfaces, out of them by LIFT, or inside the meshes of the parts touching theirs.
    bool crossed(V3 p, V3 q, double skip, int ignore1 = NO_GROUP, int ignore2 = NO_GROUP) const {
        if (nodes.empty()) return false;
        V3 d = q - p;
        double length = norm(d);
        if (length <= 2 * skip) return false;
        if (any(p, d, skip / length, 1 - skip / length, ignore1, ignore2)) return true;
        // crossing no triangle between the ends, it is all in a part or out of them, as its middle
        return inside(p + d * 0.5, ignore1, ignore2);
    }

    // Whether p is inside a part: the parts a ray from p crosses an odd number of times
    bool inside(V3 p, int ignore1, int ignore2) const {
        // a direction along no edge of the meshes, they are mostly along the axes
        const V3 ray = V3{0.5383, 0.6317, 0.5577} * 1.0e4;
        std::vector<char> parity(mesh->shapes, 0);
        visit(p, ray, 0, 1, [&](int t) {
            int g = mesh->triangleGroups[t];
            if (g != ignore1 && g != ignore2) parity[mesh->triangleShapes[t]] ^= 1;
            return false;
        });
        for (char c : parity) if (c) return true;
        return false;
    }

    bool any(V3 p, V3 d, double tMin, double tMax, int ignore1, int ignore2) const {
        return visit(p, d, tMin, tMax, [&](int t) {
            int g = mesh->triangleGroups[t];
            return g != ignore1 && g != ignore2;
        });
    }

    // Calls hit(t) for the triangles t the segment p + s d crosses, s in ]tMin, tMax[, until it returns true
    template <typename F>
    bool visit(V3 p, V3 d, double tMin, double tMax, F &&hit) const {
        V3 inv{1.0 / d.x, 1.0 / d.y, 1.0 / d.z};
        int stack[128];
        int top = 0;
        stack[top++] = 0;
        while (top > 0) {
            const Node &node = nodes[stack[--top]];
            if (!node.box.crossed(p, inv)) continue;
            if (node.count == 0) {
                stack[top++] = node.left;
                stack[top++] = node.left + 1;
                continue;
            }
            for (int i = node.start; i < node.start + node.count; i++) {
                int t = order[i];
                if (hits(t, p, d, tMin, tMax) && hit(t)) return true;
            }
        }
        return false;
    }

    // The distance from p to the closest point of the triangles, at, INF when there are none
    double closest(V3 p, V3 &at) const {
        double best = INF;
        if (nodes.empty()) return best;
        int stack[128];
        int top = 0;
        stack[top++] = 0;
        while (top > 0) {
            const Node &node = nodes[stack[--top]];
            if (node.box.distance2(p) >= best) continue;
            if (node.count == 0) {
                // the nearer child last, to be searched first
                double d0 = nodes[node.left].box.distance2(p), d1 = nodes[node.left + 1].box.distance2(p);
                stack[top++] = d0 < d1 ? node.left + 1 : node.left;
                stack[top++] = d0 < d1 ? node.left : node.left + 1;
                continue;
            }
            for (int i = node.start; i < node.start + node.count; i++) {
                const auto &tri = mesh->triangles[order[i]];
                V3 c = closestOnTriangle(p, mesh->points[tri[0]], mesh->points[tri[1]], mesh->points[tri[2]]);
                double d2 = dot(c - p, c - p);
                if (d2 < best) {
                    best = d2;
                    at = c;
                }
            }
        }
        return std::sqrt(best);
    }

private:
    struct Node {
        Box box;
        int left = 0;    // children at left and left + 1 when count is 0
        int start = 0, count = 0;
    };
    const Mesh *mesh = nullptr;
    std::vector<int> order;
    std::vector<V3> centers;
    std::vector<Node> nodes;

    int split(int start, int end) {
        int index = (int) nodes.size();
        nodes.emplace_back();
        Box box, middles;
        for (int i = start; i < end; i++) {
            const auto &tri = mesh->triangles[order[i]];
            for (int k = 0; k < 3; k++) box.add(mesh->points[tri[k]]);
            middles.add(centers[order[i]]);
        }
        nodes[index].box = box;
        if (end - start <= 4) {
            nodes[index].start = start;
            nodes[index].count = end - start;
            return index;
        }
        V3 size = middles.hi - middles.lo;
        int axis = size.x >= size.y && size.x >= size.z ? 0 : size.y >= size.z ? 1 : 2;
        int middle = (start + end) / 2;
        std::nth_element(order.begin() + start, order.begin() + middle, order.begin() + end, [&](int a, int b) {
            const V3 &ca = centers[a], &cb = centers[b];
            return axis == 0 ? ca.x < cb.x : axis == 1 ? ca.y < cb.y : ca.z < cb.z;
        });
        // the children next to each other
        int left = (int) nodes.size();
        nodes.emplace_back();
        nodes.emplace_back();
        nodes[index].left = left;
        int l = split(start, middle), r = split(middle, end);
        nodes[left] = nodes[l];
        nodes[left + 1] = nodes[r];
        return index;
    }

    // Möller-Trumbore, the segment p + t d for t in [tMin, tMax], through the triangle or its edges: a segment
    // through an edge between two faces must not get through both of them
    bool hits(int t, V3 p, V3 d, double tMin, double tMax) const {
        const auto &tri = mesh->triangles[t];
        V3 a = mesh->points[tri[0]];
        V3 e1 = mesh->points[tri[1]] - a, e2 = mesh->points[tri[2]] - a;
        V3 pv = cross(d, e2);
        double det = dot(e1, pv);
        if (std::abs(det) <= 1.0e-12 * norm(e1) * norm(e2) * norm(d)) return false;
        double inv = 1.0 / det;
        V3 tv = p - a;
        double u = dot(tv, pv) * inv;
        const double e = 1.0e-9;
        if (u < -e || u > 1 + e) return false;
        V3 qv = cross(tv, e1);
        double v = dot(d, qv) * inv;
        if (v < -e || u + v > 1 + e) return false;
        double s = dot(e2, qv) * inv;
        return s > tMin && s < tMax;
    }
};

// The triangles of the insulating parts, split until their edges are shorter than step, the edges shared by two
// triangles split in both: the paths along the surfaces then bend anywhere on the faces, and start anywhere
Mesh refined(const Mesh &mesh, double step) {
    Mesh m;
    std::vector<int> index(mesh.points.size(), -1);
    for (size_t t = 0; t < mesh.triangles.size(); t++) {
        if (mesh.triangleGroups[t] != -1) continue;
        std::array<int, 3> tri;
        for (int k = 0; k < 3; k++) {
            int p = mesh.triangles[t][k];
            if (index[p] < 0) {
                index[p] = (int) m.points.size();
                m.points.push_back(mesh.points[p]);
                m.pointGroups.push_back(-1);
            }
            tri[k] = index[p];
        }
        m.triangles.push_back(tri);
        m.triangleNormals.push_back(mesh.triangleNormals[t]);
        m.triangleGroups.push_back(-1);
        m.triangleShapes.push_back(mesh.triangleShapes[t]);
    }
    m.shapes = mesh.shapes;
    for (;;) {
        std::unordered_map<std::int64_t, int> middles;
        auto middle = [&](int a, int b) -> int {
            if (dist(m.points[a], m.points[b]) <= step) return -1;
            std::int64_t key = ((std::int64_t) std::min(a, b) << 32) | std::max(a, b);
            auto found = middles.find(key);
            if (found != middles.end()) return found->second;
            int p = (int) m.points.size();
            middles[key] = p;
            m.points.push_back((m.points[a] + m.points[b]) * 0.5);
            m.pointGroups.push_back(-1);
            return p;
        };
        size_t count = m.triangles.size();
        std::vector<std::array<int, 3>> triangles;
        std::vector<V3> normals;
        std::vector<int> shapes;
        for (size_t t = 0; t < count; t++) {
            std::array<int, 3> tri = m.triangles[t];
            std::array<int, 3> mid = {middle(tri[0], tri[1]), middle(tri[1], tri[2]), middle(tri[2], tri[0])};
            int split = (mid[0] >= 0) + (mid[1] >= 0) + (mid[2] >= 0);
            // turned for the split edges to come first, the orientation kept
            for (int turn = 0; turn < 3 && split > 0 && split < 3 && !(mid[0] >= 0 && (split == 1 || mid[1] >= 0));
                 turn++) {
                tri = {tri[1], tri[2], tri[0]};
                mid = {mid[1], mid[2], mid[0]};
            }
            int a = tri[0], b = tri[1], c = tri[2];
            std::vector<std::array<int, 3>> parts;
            if (split == 0) {
                parts = {tri};
            } else if (split == 1) {
                parts = {{a, mid[0], c}, {mid[0], b, c}};
            } else if (split == 2) {
                int m1 = mid[0], m2 = mid[1];
                parts = {{m1, b, m2}};
                if (dist(m.points[a], m.points[m2]) < dist(m.points[m1], m.points[c])) {
                    parts.push_back({a, m1, m2});
                    parts.push_back({a, m2, c});
                } else {
                    parts.push_back({a, m1, c});
                    parts.push_back({m1, m2, c});
                }
            } else {
                parts = {{a, mid[0], mid[2]}, {mid[0], b, mid[1]}, {mid[2], mid[1], c}, {mid[0], mid[1], mid[2]}};
            }
            for (const auto &p : parts) {
                triangles.push_back(p);
                normals.push_back(m.triangleNormals[t]);
                shapes.push_back(m.triangleShapes[t]);
            }
        }
        if (middles.empty()) break;
        m.triangles = triangles;
        m.triangleNormals = normals;
        m.triangleShapes = shapes;
        m.triangleGroups.assign(triangles.size(), -1);
    }
    m.link();
    return m;
}

// A point of the graph: a point of the mesh, or one spaced along an edge
struct Node {
    V3 at;          // on the surface
    V3 lifted;      // out of it by LIFT, the end of the segments tested
    int group;
    int point;      // the point of the mesh, or -1
    int edge;       // the edge, or -1
};

// No point when there is no path: of length -1 when there is none at all, else of the length it is longer than
struct Path {
    double length = -1;
    std::vector<V3> points;
};

Path longerThan(double limit) {
    Path path;
    path.length = limit;
    return path;
}

// A binary heap of the items 0 .. n - 1 by key, the key of an item changed in place
class IndexedHeap {
public:
    explicit IndexedHeap(int n) : keys(n, INF), positions(n, -1) {}

    bool empty() const { return items.empty(); }
    int top() const { return items.front(); }
    double key(int item) const { return keys[item]; }

    // Adds the item, or moves it to its new key
    void set(int item, double key) {
        double old = keys[item];
        keys[item] = key;
        if (positions[item] < 0) {
            positions[item] = (int) items.size();
            items.push_back(item);
            up(positions[item]);
        } else if (key < old) {
            up(positions[item]);
        } else {
            down(positions[item]);
        }
    }

    void pop() {
        int item = items.front();
        swap(0, (int) items.size() - 1);
        items.pop_back();
        positions[item] = -1;
        if (!items.empty()) down(0);
    }

private:
    std::vector<double> keys;
    std::vector<int> positions, items;

    void swap(int i, int j) {
        std::swap(items[i], items[j]);
        positions[items[i]] = i;
        positions[items[j]] = j;
    }

    void up(int i) {
        while (i > 0 && keys[items[(i - 1) / 2]] > keys[items[i]]) {
            swap(i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    void down(int i) {
        for (;;) {
            int l = 2 * i + 1, r = l + 1, m = i;
            if (l < (int) items.size() && keys[items[l]] < keys[items[m]]) m = l;
            if (r < (int) items.size() && keys[items[r]] < keys[items[m]]) m = r;
            if (m == i) return;
            swap(i, m);
            i = m;
        }
    }
};

// The nodes on the edges of mesh that edge(e) wants, at their ends and spaced by step at most, at least one inside
// each when inside: the index of the node of each point, and the first and count of those inside each edge
struct Graph {
    std::vector<Node> nodes;
    std::vector<int> pointNode;
    std::vector<std::pair<int, int>> edgeNodes;

    template <typename F>
    Graph(const Mesh &mesh, double step, bool inside, F &&wanted) {
        std::vector<char> used(mesh.points.size(), 0);
        for (const Mesh::Edge &e : mesh.edges) if (wanted(e)) used[e.a] = used[e.b] = 1;
        pointNode.assign(mesh.points.size(), -1);
        for (size_t p = 0; p < mesh.points.size(); p++) {
            if (!used[p]) continue;
            pointNode[p] = (int) nodes.size();
            nodes.push_back({mesh.points[p], mesh.points[p] + mesh.pointNormals[p] * LIFT, mesh.pointGroups[p],
                             (int) p, -1});
        }
        edgeNodes.assign(mesh.edges.size(), {0, 0});
        for (int i = 0; i < (int) mesh.edges.size(); i++) {
            const Mesh::Edge &e = mesh.edges[i];
            if (!wanted(e)) continue;
            V3 a = mesh.points[e.a], b = mesh.points[e.b];
            int count = std::max(inside ? 1 : 0, (int) std::floor(dist(a, b) / step));
            edgeNodes[i] = {(int) nodes.size(), count};
            for (int k = 1; k <= count; k++) {
                V3 at = lerp(a, b, (double) k / (count + 1));
                nodes.push_back({at, at + e.normal * LIFT, mesh.pointGroups[e.a], -1, i});
            }
        }
    }

    // Keeps one node per cube of side cell, part, and direction out of the surface, the others left out: the meshes
    // of the curved faces have more points than the paths need, their bends sliding along the edges. The nodes of
    // the two sides of a thin wall stay, out of it in other directions. Only the nodes are kept, not the indices of
    // those of the points and edges.
    void thin(const Mesh &mesh, double cell) {
        std::unordered_map<std::int64_t, int> kept;
        std::vector<Node> thinned;
        for (const Node &node : nodes) {
            int triangle = node.point >= 0 ? mesh.pointTriangles[node.point].front()
                                           : mesh.edges[node.edge].triangles.front();
            V3 out = unit(node.lifted - node.at);
            auto bin = [](double c) { return (std::int64_t) std::max(-1L, std::min(1L, std::lround(1.5 * c))) + 1; };
            std::int64_t key = (std::int64_t) std::floor(node.at.x / cell) * 73856093;
            key ^= (std::int64_t) std::floor(node.at.y / cell) * 19349663;
            key ^= (std::int64_t) std::floor(node.at.z / cell) * 83492791;
            key ^= (std::int64_t) mesh.triangleShapes[triangle] * 2654435761LL;
            key ^= (bin(out.x) * 9 + bin(out.y) * 3 + bin(out.z)) * 40503;
            if (kept.emplace(key, (int) thinned.size()).second) thinned.push_back(node);
        }
        nodes = thinned;
        pointNode.clear();
        edgeNodes.clear();
    }
};

// Faces at less than this angle across a groove are bridged by the creepage distance, at 80° and more it follows them
const double GROOVE_COS = std::cos(100.0 * M_PI / 180.0);

}  // namespace

class InsulationPaths {
public:
    InsulationPaths(const TopTools_ListOfShape &shapes, const int *groups, double deflection, double step)
            : step(step), skip(2 * deflection + 4 * LIFT), contact(deflection + 2 * LIFT) {
        int i = 0;
        for (const TopoDS_Shape &shape : shapes) {
            mesh.add(shape, groups[i], deflection);
            groupShapes[groups[i]].push_back(shape);
            i++;
        }
        mesh.link();
        std::vector<int> all(mesh.triangles.size());
        std::map<int, std::vector<int>> byGroup;
        for (int t = 0; t < (int) all.size(); t++) {
            all[t] = t;
            byGroup[mesh.triangleGroups[t]].push_back(t);
        }
        obstacles.build(mesh, all);
        for (auto &[group, triangles] : byGroup) groupTrees[group].build(mesh, triangles);
        TRACE2(std::ostringstream{} << mesh.points.size() << " points, " << mesh.triangles.size() << " triangles, "
                                    << mesh.edges.size() << " edges");
    }

    Path clearance(int from, int to, double limit) {
        std::call_once(airMade, [&] {
            // the bends slide along the edges: a few steps between the points spaced along them are enough
            air = std::make_unique<Graph>(mesh, 4 * step, false, [](const Mesh::Edge &e) { return e.convex; });
            size_t all = air->nodes.size();
            air->thin(mesh, step);
            TRACE2(std::ostringstream{} << air->nodes.size() << " nodes in the air, of " << all);
        });
        double straight;
        Path best = direct(from, to, limit, &straight);
        if (best.length >= 0) return best;
        if (straight >= limit) return longerThan(limit);
        // every point of a path of length L is closer than L to both groups added: the nodes searched are those of
        // a bound, larger until the path found is shorter than it
        Box a = groupBox(from), b = groupBox(to);
        const std::vector<float> &toFrom = distancesTo(from), &toTo = distancesTo(to);
        // the bound up to the limit at once when close to it, for a search not to be done twice nearly
        auto larger = [&](double bound) { return 1.5 * bound > 0.75 * limit ? limit : 1.5 * bound; };
        for (double bound = std::min(1.25 * straight + 1.0, limit);; bound = larger(bound)) {
            std::vector<int> nodes;
            bool all = true;
            for (int v = 0; v < (int) air->nodes.size(); v++) {
                V3 p = air->nodes[v].at;
                if (std::sqrt(a.distance2(p)) + std::sqrt(b.distance2(p)) <= bound &&
                    toFrom[v] + toTo[v] <= bound) nodes.push_back(v);
                else all = false;
            }
            Path path = search(nodes, from, to, bound);
            TRACE2(std::ostringstream{} << nodes.size() << " nodes closer than " << bound << ": " << path.length);
            if (path.length >= 0 || all) return path;
            if (bound >= limit) return longerThan(limit);
        }
    }

    Path creepage(int from, int to, double groove, double limit) {
        std::call_once(surfaceMade, [&] {
            // triangles a few steps wide: the points spaced along their edges cover their insides enough
            surfaceMesh = refined(mesh, 4 * step);
            surface = std::make_unique<Graph>(surfaceMesh, step, true, [](const Mesh::Edge &) { return true; });
            TRACE2(std::ostringstream{} << surfaceMesh.triangles.size() << " triangles, " << surface->nodes.size()
                                        << " nodes on the surfaces");
        });
        const std::vector<Node> &nodes = surface->nodes;
        const Grid &grid = gridOf(groove);
        const TriangleTree &source = tree(from), &target = tree(to);
        int n = (int) nodes.size();
        std::vector<V3> toSource(n), toTarget(n);
        std::vector<double> g(n, INF), h(n, -1);
        std::vector<int> parent(n, -1);
        std::vector<char> settled(n, 0);
        auto heuristic = [&](int v) {
            if (h[v] < 0) h[v] = target.closest(nodes[v].at, toTarget[v]);
            return h[v];
        };
        using Entry = std::pair<double, int>;
        std::priority_queue<Entry, std::vector<Entry>, std::greater<>> open;
        // from where the source touches an insulating part, or across a groove narrower than X
        Box near = groupBox(from);
        near.lo = near.lo - V3{groove, groove, groove};
        near.hi = near.hi + V3{groove, groove, groove};
        for (int v = 0; v < n; v++) {
            if (near.distance2(nodes[v].at) > 0) continue;
            double d = source.closest(nodes[v].at, toSource[v]);
            if (bridged(toSource[v], nodes[v].lifted, faces(nodes[v]), groove, from, NO_GROUP)) {
                g[v] = d;
                open.push({d + heuristic(v), v});
            }
        }
        Path straight = direct(from, to, std::min(groove, limit)), best = longerThan(limit);
        if (straight.length >= 0) best = straight;
        int last = -1;
        while (!open.empty()) {
            double f = open.top().first;
            int u = open.top().second;
            open.pop();
            if (settled[u] || f != g[u] + h[u]) continue;
            if (best.length >= 0 && f >= best.length) break;
            settled[u] = 1;
            const std::vector<int> &uFaces = faces(nodes[u]);
            if (h[u] <= groove && (best.length < 0 || g[u] + h[u] < best.length) &&
                bridged(toTarget[u], nodes[u].lifted, uFaces, groove, to, NO_GROUP)) {
                best.length = g[u] + h[u];
                last = u;
            }
            auto relax = [&](int v, bool jump) {
                if (settled[v] || v == u) return;
                double d = g[u] + dist(nodes[u].at, nodes[v].at);
                if (d >= g[v]) return;
                if (jump && !across(nodes[u].lifted, uFaces, nodes[v].lifted, faces(nodes[v]), groove)) return;
                g[v] = d;
                parent[v] = u;
                open.push({d + heuristic(v), v});
            };
            for (int t : uFaces) {
                for (int k = 0; k < 3; k++) {
                    relax(surface->pointNode[surfaceMesh.triangles[t][k]], false);
                    auto range = surface->edgeNodes[surfaceMesh.triangleEdges[t][k]];
                    for (int i = range.first; i < range.first + range.second; i++) relax(i, false);
                }
            }
            forNear(grid, nodes[u].at, groove, [&](int v) { relax(v, true); });
        }
        if (last < 0) return best;
        std::vector<int> chain;
        for (int u = last; u >= 0; u = parent[u]) chain.insert(chain.begin(), u);
        return straighten(chain, *surface, surfaceMesh, {toSource[chain.front()], toTarget[last]}, from, to, true, true,
                          groove);
    }

    // A* over the nodes of the air given, for a path shorter than bound. The segment from a node to its parent is
    // tested only when the search takes the node, the parent replaced then by the closest settled node it sees.
    Path search(const std::vector<int> &subset, int from, int to, double bound) {
        const TriangleTree &source = tree(from), &target = tree(to);
        int n = (int) subset.size();
        std::vector<Node> nodes;
        for (int v : subset) nodes.push_back(air->nodes[v]);
        std::vector<V3> toSource(n), toTarget(n);
        std::vector<double> g(n, INF), h(n);
        // the parent of a node: another one, or -1 from the closest point of the source, -2 from the node itself
        std::vector<int> parent(n, -1);
        std::vector<char> settled(n, 0), checked(n, 1);
        // the closest to the source of the settled nodes seen from each node, among those already scanned
        std::vector<double> seenG(n, INF);
        std::vector<int> seenParent(n, -1), scanned(n, 0);
        long tests = 0, failures = 0, taken = 0;
        auto started = std::chrono::steady_clock::now();
        for (int i = 0; i < n; i++) {
            h[i] = nodes[i].group == to ? 0 : target.closest(nodes[i].at, toTarget[i]);
            if (nodes[i].group == from) {
                g[i] = seenG[i] = 0;
                parent[i] = seenParent[i] = -2;
                continue;
            }
            double d = source.closest(nodes[i].at, toSource[i]);
            tests++;
            if (d + h[i] < bound && !obstacles.crossed(toSource[i], nodes[i].lifted, skip, from)) g[i] = seenG[i] = d;
        }
        IndexedHeap open(n);
        for (int i = 0; i < n; i++) if (g[i] < INF) open.set(i, g[i] + h[i]);
        double best = bound;
        int last = -1;
        bool lastToTarget = false;
        std::vector<int> done;
        while (!open.empty()) {
            int u = open.top();
            if (open.key(u) >= best) break;
            if (!checked[u]) {
                checked[u] = 1;
                tests++;
                if (obstacles.crossed(nodes[parent[u]].lifted, nodes[u].lifted, skip)) {
                    // the settled node seen from it closest to the source then: among those settled since the last
                    // scan, or the one it found
                    failures++;
                    std::vector<std::pair<double, int>> candidates;
                    for (size_t k = scanned[u]; k < done.size(); k++) {
                        int s = done[k];
                        double d = g[s] + dist(nodes[s].at, nodes[u].at);
                        if (d < seenG[u] && d + h[u] < best) candidates.push_back({d, s});
                    }
                    scanned[u] = (int) done.size();
                    std::make_heap(candidates.begin(), candidates.end(), std::greater<>());
                    while (!candidates.empty()) {
                        std::pop_heap(candidates.begin(), candidates.end(), std::greater<>());
                        auto candidate = candidates.back();
                        candidates.pop_back();
                        tests++;
                        if (!obstacles.crossed(nodes[candidate.second].lifted, nodes[u].lifted, skip)) {
                            seenG[u] = candidate.first;
                            seenParent[u] = candidate.second;
                            break;
                        }
                    }
                    g[u] = seenG[u];
                    parent[u] = seenParent[u];
                    if (g[u] < INF) open.set(u, g[u] + h[u]);
                    else open.pop();
                    continue;
                }
            }
            open.pop();
            settled[u] = 1;
            taken++;
            done.push_back(u);
            if (nodes[u].group == to) {
                if (g[u] < best) {
                    best = g[u];
                    last = u;
                    lastToTarget = false;
                }
            } else if (g[u] + h[u] < best) {
                tests++;
                if (!obstacles.crossed(nodes[u].lifted, toTarget[u], skip, to)) {
                    best = g[u] + h[u];
                    last = u;
                    lastToTarget = true;
                }
            }
            for (int v = 0; v < n; v++) {
                if (settled[v]) continue;
                double d = g[u] + dist(nodes[u].at, nodes[v].at);
                if (d < g[v] && d + h[v] < best) {
                    g[v] = d;
                    parent[v] = u;
                    checked[v] = 0;
                    open.set(v, d + h[v]);
                }
            }
        }
        TRACE2(std::ostringstream{} << taken << " nodes taken, " << failures << " blocked, " << tests << " segments tested, "
                                    << std::chrono::duration<double>(std::chrono::steady_clock::now() - started).count() << " s");
        if (last < 0) return Path();
        std::vector<int> chain;
        int first = last;
        for (int u = last; u >= 0; u = parent[u]) {
            chain.insert(chain.begin(), subset[u]);
            first = u;
        }
        bool atSource = parent[first] == -1;
        std::vector<V3> ends = {atSource ? toSource[first] : nodes[first].at,
                                lastToTarget ? toTarget[last] : nodes[last].at};
        return straighten(chain, *air, mesh, ends, from, to, atSource, lastToTarget, -1);
    }

    // The distances from the air nodes to the group, rounded down not to leave out a node at a bound, computed once
    const std::vector<float> &distancesTo(int group) {
        std::lock_guard<std::mutex> lock(cached);
        std::vector<float> &known = airDistances[group];
        if (known.empty()) {
            known.resize(air->nodes.size());
            const TriangleTree &t = tree(group);
            for (size_t v = 0; v < known.size(); v++) {
                V3 c;
                known[v] = (float) (t.closest(air->nodes[v].at, c) * (1 - 1e-6));
            }
        }
        return known;
    }

private:
    // the searches run in parallel: what they share is made once, under this lock or call_once
    std::mutex cached, measuring;
    std::once_flag airMade, surfaceMade;
    std::map<int, std::vector<float>> airDistances;
    // the ends of the segments tested may be inside the meshes, deviating from the faces, up to skip; parts touch
    // when their meshes are closer than contact
    double step, skip, contact;
    Mesh mesh, surfaceMesh;
    TriangleTree obstacles;
    std::map<int, TriangleTree> groupTrees;
    std::map<int, std::vector<TopoDS_Shape>> groupShapes;
    std::unique_ptr<Graph> air, surface;
    // the surface nodes by cell of a grid as large as the groove width, by groove width
    struct Grid {
        double cell;
        std::unordered_map<std::int64_t, std::vector<int>> cells;
    };
    std::map<double, Grid> grids;

    const TriangleTree &tree(int group) const {
        auto found = groupTrees.find(group);
        if (found == groupTrees.end()) throw std::invalid_argument("no shape in group " + std::to_string(group));
        return found->second;
    }

    Box groupBox(int group) const {
        Box box;
        for (size_t p = 0; p < mesh.points.size(); p++) if (mesh.pointGroups[p] == group) box.add(mesh.points[p]);
        return box;
    }

    // The triangles of the surfaces a node is on
    const std::vector<int> &faces(const Node &node) const {
        return node.point >= 0 ? surfaceMesh.pointTriangles[node.point] : surfaceMesh.edges[node.edge].triangles;
    }

    // Whether the creepage distance crosses the air from p, on the triangles pFaces, to q, on qFaces: across a
    // groove narrower than X between faces at less than 80°, or a joint between two parts
    bool across(V3 p, const std::vector<int> &pFaces, V3 q, const std::vector<int> &qFaces, double groove) const {
        V3 d = q - p;
        double length = norm(d);
        if (length > groove) return false;
        bool joint = length <= contact &&
                     surfaceMesh.triangleShapes[pFaces.front()] != surfaceMesh.triangleShapes[qFaces.front()];
        if (!joint) {
            bool facing = false;
            for (int a : pFaces) {
                V3 na = surfaceMesh.triangleNormals[a];
                if (dot(na, d) <= 0) continue;
                for (int b : qFaces) {
                    V3 nb = surfaceMesh.triangleNormals[b];
                    if (dot(na, nb) < GROOVE_COS && dot(nb, d) < 0) facing = true;
                }
            }
            if (!facing) return false;
        }
        return !obstacles.crossed(p, q, skip);
    }

    // Whether the creepage distance crosses the air from c, on the conductor of group, to p, on the triangles pFaces:
    // where the conductor touches the insulating part, or across a groove narrower than X, the conductor facing the
    // face of p at less than 80° as its closest point does
    bool bridged(V3 c, V3 p, const std::vector<int> &pFaces, double groove, int group1, int group2) const {
        V3 d = p - c;
        double length = norm(d);
        if (length > groove) return false;
        if (length > contact) {
            bool facing = false;
            for (int a : pFaces) facing = facing || dot(surfaceMesh.triangleNormals[a], d) < GROOVE_COS * length;
            if (!facing) return false;
        }
        return !obstacles.crossed(c, p, skip, group1, group2);
    }

    static std::int64_t cellKey(std::int64_t i, std::int64_t j, std::int64_t k) {
        return (i * 73856093) ^ (j * 19349663) ^ (k * 83492791);
    }

    const Grid &gridOf(double cell) {
        std::lock_guard<std::mutex> lock(cached);
        auto found = grids.find(cell);
        if (found != grids.end()) return found->second;
        Grid &grid = grids[cell];
        grid.cell = cell;
        for (int v = 0; v < (int) surface->nodes.size(); v++) {
            V3 p = surface->nodes[v].at;
            grid.cells[cellKey((std::int64_t) std::floor(p.x / cell), (std::int64_t) std::floor(p.y / cell),
                               (std::int64_t) std::floor(p.z / cell))].push_back(v);
        }
        return grid;
    }

    template <typename F>
    void forNear(const Grid &grid, V3 p, double radius, F &&f) const {
        std::int64_t i = (std::int64_t) std::floor(p.x / grid.cell), j = (std::int64_t) std::floor(p.y / grid.cell),
                k = (std::int64_t) std::floor(p.z / grid.cell);
        for (int di = -1; di <= 1; di++)
            for (int dj = -1; dj <= 1; dj++)
                for (int dk = -1; dk <= 1; dk++) {
                    auto found = grid.cells.find(cellKey(i + di, j + dj, k + dk));
                    if (found == grid.cells.end()) continue;
                    for (int v : found->second) if (dist(surface->nodes[v].at, p) <= radius) f(v);
                }
    }

    // The shortest straight segment between the groups, through the air, if shorter than limit; straight, if not
    // null, its length, whatever stands between the groups
    Path direct(int from, int to, double limit, double *straight = nullptr) {
        Path path;
        auto compound = [&](int group) {
            TopoDS_Compound c;
            BRep_Builder builder;
            builder.MakeCompound(c);
            for (const TopoDS_Shape &s : groupShapes.at(group)) builder.Add(c, s);
            return c;
        };
        // one at a time, OCCT reading the shapes
        std::lock_guard<std::mutex> lock(measuring);
        BRepExtrema_DistShapeShape extrema;
        extrema.SetDeflection(0.01);
        extrema.SetMultiThread(true);
        extrema.LoadS1(compound(from));
        extrema.LoadS2(compound(to));
        if (!extrema.Perform() || extrema.NbSolution() <= 0) throw std::runtime_error("no distance between the groups");
        if (straight) *straight = extrema.Value();
        if (extrema.Value() > limit) return path;
        for (int i = 1; i <= extrema.NbSolution(); i++) {
            gp_Pnt a = extrema.PointOnShape1(i), b = extrema.PointOnShape2(i);
            V3 p{a.X(), a.Y(), a.Z()}, q{b.X(), b.Y(), b.Z()};
            if (!obstacles.crossed(p, q, skip, from, to)) {
                path.length = extrema.Value();
                path.points = {p, q};
                return path;
            }
        }
        return path;
    }

    // The path through the nodes of chain, from ends[0] to ends[1], its bends sliding along the edges of m and
    // dropped while it shortens and its segments stay through the air, or, when groove is not negative, on the
    // surfaces and across the grooves. The ends are on the groups from and to when atSource and atTarget, and move
    // with the bends next to them, else they are the first and last bends.
    Path straighten(const std::vector<int> &chain, const Graph &graph, const Mesh &m, std::vector<V3> ends, int from,
                    int to, bool atSource, bool atTarget, double groove) {
        bool creeping = groove >= 0;
        // at a point of the mesh, along an edge, or, on the surfaces, inside a triangle
        struct Bend {
            int point, edge, triangle;
            V3 at, lifted;
        };
        std::vector<Bend> bends;
        for (int u : chain) {
            const Node &node = graph.nodes[u];
            bends.push_back({node.point, node.edge, -1, node.at, node.lifted});
        }
        V3 start = ends[0], end = ends[1];
        const TriangleTree &source = tree(from), &target = tree(to);
        auto trianglesOf = [&](const Bend &b) -> std::vector<int> {
            if (b.triangle >= 0) return {b.triangle};
            return b.edge >= 0 ? m.edges[b.edge].triangles : m.pointTriangles[b.point];
        };
        auto onSameTriangle = [&](const Bend &a, const Bend &b) {
            std::vector<int> ta = trianglesOf(a), tb = trianglesOf(b);
            for (int t : ta) if (std::find(tb.begin(), tb.end(), t) != tb.end()) return true;
            return false;
        };
        auto linked = [&](const Bend &a, const Bend &b) {
            if (!creeping) return !obstacles.crossed(a.lifted, b.lifted, skip);
            return onSameTriangle(a, b) || across(a.lifted, trianglesOf(a), b.lifted, trianglesOf(b), groove);
        };
        // from or to the conductor of group at c
        auto reaches = [&](V3 c, const Bend &b, int group) {
            if (!creeping) return !obstacles.crossed(c, b.lifted, skip, group);
            return bridged(c, b.lifted, trianglesOf(b), groove, group, NO_GROUP);
        };
        auto closest = [&](const TriangleTree &tree, V3 p) {
            V3 c;
            tree.closest(p, c);
            return c;
        };
        auto length = [&]() {
            double l = atSource ? dist(start, bends.front().at) : 0;
            for (size_t i = 1; i < bends.size(); i++) l += dist(bends[i - 1].at, bends[i].at);
            return l + (atTarget ? dist(bends.back().at, end) : 0);
        };
        double current = length();
        for (int pass = 0; pass < 1000; pass++) {
            double before = current;
            // the bends no longer needed
            for (size_t i = 0; i < bends.size() && bends.size() > 1;) {
                bool first = i == 0, last = i + 1 == bends.size();
                bool dropped = false;
                if (first && atSource) {
                    V3 s = closest(source, bends[1].at);
                    dropped = reaches(s, bends[1], from);
                    if (dropped) start = s;
                } else if (last && atTarget) {
                    V3 t = closest(target, bends[i - 1].at);
                    dropped = reaches(t, bends[i - 1], to);
                    if (dropped) end = t;
                } else if (!first && !last) {
                    dropped = linked(bends[i - 1], bends[i + 1]);
                }
                if (dropped) bends.erase(bends.begin() + i);
                else i++;
            }
            // each bend along its edge, or along those of its point, to the shortest
            for (size_t i = 0; i < bends.size(); i++) {
                Bend &b = bends[i];
                bool first = i == 0, last = i + 1 == bends.size();
                if ((first && !atSource) || (last && !atTarget)) continue;
                // in the air, along the convex edges it is on; on the surfaces, along the edges of its triangles
                std::vector<int> edges;
                if (creeping) {
                    for (int t : trianglesOf(b))
                        for (int e : m.triangleEdges[t])
                            if (std::find(edges.begin(), edges.end(), e) == edges.end()) edges.push_back(e);
                } else if (b.edge >= 0) {
                    edges.push_back(b.edge);
                } else {
                    for (int e : m.pointEdges[b.point]) if (m.edges[e].convex) edges.push_back(e);
                }
                auto neighbours = [&](V3 p, V3 &prev, V3 &next) {
                    prev = first ? closest(source, p) : bends[i - 1].at;
                    next = last ? closest(target, p) : bends[i + 1].at;
                };
                Bend kept = b;
                V3 keptStart = start, keptEnd = end;
                double keptLength = dist(first ? start : bends[i - 1].at, b.at) + dist(b.at, last ? end : bends[i + 1].at);
                for (int e : edges) {
                    V3 a = m.points[m.edges[e].a], c = m.points[m.edges[e].b];
                    auto around = [&](double t) {
                        V3 p = lerp(a, c, t), prev, next;
                        neighbours(p, prev, next);
                        return dist(prev, p) + dist(p, next);
                    };
                    // golden section: the length is convex along the edge, the closest points of the groups aside
                    const double r = (std::sqrt(5.0) - 1) / 2;
                    double lo = 0, hi = 1, x1 = hi - r * (hi - lo), x2 = lo + r * (hi - lo);
                    double f1 = around(x1), f2 = around(x2);
                    for (int k = 0; k < 50; k++) {
                        if (f1 < f2) {
                            hi = x2, x2 = x1, f2 = f1, x1 = hi - r * (hi - lo), f1 = around(x1);
                        } else {
                            lo = x1, x1 = x2, f1 = f2, x2 = lo + r * (hi - lo), f2 = around(x2);
                        }
                    }
                    Bend moved = b;
                    moved.edge = e;
                    moved.at = lerp(a, c, (lo + hi) / 2);
                    moved.lifted = moved.at + m.edges[e].normal * LIFT;
                    V3 prev, next;
                    neighbours(moved.at, prev, next);
                    double l = dist(prev, moved.at) + dist(moved.at, next);
                    if (l >= keptLength - 1e-12) continue;
                    if (!(first ? reaches(prev, moved, from) : linked(bends[i - 1], moved))) continue;
                    if (!(last ? reaches(next, moved, to) : linked(moved, bends[i + 1]))) continue;
                    kept = moved;
                    keptLength = l;
                    if (first) keptStart = prev;
                    if (last) keptEnd = next;
                }
                // the ends of the path and of the jumps across the grooves are inside the faces mostly
                bool free = creeping && (first || last || !onSameTriangle(bends[i - 1], kept) ||
                                         !onSameTriangle(kept, bends[i + 1]));
                for (int t : free ? trianglesOf(kept) : std::vector<int>()) {
                    const auto &tri = m.triangles[t];
                    V3 a = m.points[tri[0]], c1 = m.points[tri[1]] - a, c2 = m.points[tri[2]] - a;
                    V3 normal = m.triangleNormals[t];
                    V3 u = unit(c1), w = cross(normal, u);
                    // the barycentric coordinates of p, inside the triangle when both and their sum are in [0, 1]
                    double d11 = dot(c1, c1), d12 = dot(c1, c2), d22 = dot(c2, c2), det = d11 * d22 - d12 * d12;
                    auto inTriangle = [&](V3 p) {
                        V3 r = p - a;
                        double r1 = dot(r, c1), r2 = dot(r, c2);
                        double s1 = (d22 * r1 - d12 * r2) / det, s2 = (d11 * r2 - d12 * r1) / det;
                        return s1 >= -1e-12 && s2 >= -1e-12 && s1 + s2 <= 1 + 1e-12;
                    };
                    Bend moved = kept;
                    moved.point = moved.edge = -1;
                    moved.triangle = t;
                    // pattern search in the plane of the triangle
                    const V3 directions[8] = {u, u * -1, w, w * -1, unit(u + w), unit(u - w), unit(w - u), unit((u + w) * -1)};
                    double h = std::sqrt(std::max(d11, d22)) / 4;
                    for (int k = 0; k < 400 && h > 1e-7; k++) {
                        bool improved = false;
                        for (const V3 &d : directions) {
                            V3 q = moved.at + d * h;
                            if (!inTriangle(q)) continue;
                            V3 prev, next;
                            neighbours(q, prev, next);
                            double l = dist(prev, q) + dist(q, next);
                            if (l >= keptLength - 1e-13) continue;
                            Bend candidate = moved;
                            candidate.at = q;
                            candidate.lifted = q + normal * LIFT;
                            if (!(first ? reaches(prev, candidate, from) : linked(bends[i - 1], candidate))) continue;
                            if (!(last ? reaches(next, candidate, to) : linked(candidate, bends[i + 1]))) continue;
                            moved = candidate;
                            kept = candidate;
                            keptLength = l;
                            if (first) keptStart = prev;
                            if (last) keptEnd = next;
                            improved = true;
                            break;
                        }
                        if (!improved) h /= 2;
                    }
                }
                b = kept;
                start = keptStart;
                end = keptEnd;
            }
            current = length();
            if (before - current < 1e-9) break;
        }
        Path path;
        path.length = current;
        if (atSource) path.points.push_back(start);
        for (const Bend &b : bends) {
            // the bends along a straight line left out
            size_t n = path.points.size();
            if (n >= 2) {
                V3 u = path.points[n - 1] - path.points[n - 2], v = b.at - path.points[n - 1];
                if (norm(cross(u, v)) <= 1.0e-9 * norm(u) * norm(v) && dot(u, v) > 0) path.points.pop_back();
            }
            path.points.push_back(b.at);
        }
        if (atTarget) path.points.push_back(end);
        return path;
    }
};

extern "C" InsulationPaths *new_InsulationPaths__shapes_groups_deflection_step(
        const TopTools_ListOfShape *shapes, const int *groups, double deflection, double step) {
    TRACE("");
    return guard(__func__, [&] { return new InsulationPaths(*shapes, groups, deflection, step); });
}

extern "C" void delete_InsulationPaths(InsulationPaths *paths) {
    delete paths;
}

static double *pathValues(const Path &path) {
    double *values = new double[2 + 3 * path.points.size()];
    values[0] = path.length;
    values[1] = (double) path.points.size();
    for (size_t i = 0; i < path.points.size(); i++) {
        values[2 + 3 * i] = path.points[i].x;
        values[3 + 3 * i] = path.points[i].y;
        values[4 + 3 * i] = path.points[i].z;
    }
    return values;
}

extern "C" double *R_InsulationPaths__clearance__from_to_limit(InsulationPaths *paths, int from, int to, double limit) {
    TRACE("");
    return guard(__func__, [&] { return pathValues(paths->clearance(from, to, limit)); });
}

extern "C" double *R_InsulationPaths__creepage__from_to_groove_limit(InsulationPaths *paths, int from, int to,
                                                                      double groove, double limit) {
    TRACE("");
    return guard(__func__, [&] { return pathValues(paths->creepage(from, to, groove, limit)); });
}
