//
// C API: the faces and boundaries of the shapes as the viewer draws them, written to binary glTF files without a viewer.
//

#include "capi_internal.hxx"

#include <BRepBndLib.hxx>
#include <BRepMesh_IncrementalMesh.hxx>
#include <Bnd_Box.hxx>
#include <Graphic3d_ArrayOfPrimitives.hxx>
#include <Graphic3d_ArrayOfSegments.hxx>
#include <Graphic3d_ArrayOfTriangles.hxx>
#include <Prs3d_Drawer.hxx>
#include <StdPrs_ShadedShape.hxx>
#include <StdPrs_WFShape.hxx>
#include <Precision.hxx>
#include <TopExp_Explorer.hxx>
#include <gp.hxx>
#include <gp_Quaternion.hxx>

#include <algorithm>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <fstream>
#include <limits>
#include <locale>
#include <map>
#include <sstream>
#include <stdexcept>
#include <string>
#include <vector>

namespace {
// Chordal deflection of the mesh, relative to the diagonal of the shape bounding box
constexpr double RELATIVE_DEFLECTION = 0.001;
// Deflection when the shape has no bounding box
constexpr double DEFAULT_DEFLECTION = 0.01;
// Angular deflection of the mesh, in radians
constexpr double ANGULAR_DEFLECTION = 0.5;

constexpr uint32_t GLB_MAGIC = 0x46546C67;  // "glTF"
constexpr uint32_t GLB_VERSION = 2;
constexpr uint32_t GLB_HEADER_SIZE = 12;
constexpr uint32_t GLB_CHUNK_HEADER_SIZE = 8;
constexpr uint32_t GLB_CHUNK_JSON = 0x4E4F534A;  // "JSON"
constexpr uint32_t GLB_CHUNK_BIN = 0x004E4942;  // "BIN\0"
constexpr size_t GLB_ALIGNMENT = 4;
constexpr int COMPONENT_FLOAT = 5126;
constexpr int COMPONENT_UNSIGNED_INT = 5125;
constexpr int TARGET_NONE = 0;
constexpr int TARGET_ARRAY_BUFFER = 34962;
constexpr int TARGET_ELEMENT_ARRAY_BUFFER = 34963;
constexpr int MODE_LINES = 1;
constexpr int MODE_TRIANGLES = 4;

// The models are in millimeters, Z up; glTF is in meters, Y up
constexpr double MILLIMETERS_TO_METERS = 0.001;
// Linear RGB of the parts without a color, and of the boundary lines
constexpr double DEFAULT_COLOR[3] = {0.6, 0.6, 0.6};
constexpr double EDGE_COLOR[3] = {0, 0, 0};
constexpr double ROUGHNESS = 0.5;

// Shaded faces and their boundaries, in the shape coordinates
struct Tessellation {
    std::vector<float> positions;     // x, y, z per vertex
    std::vector<float> normals;       // x, y, z per vertex
    std::vector<uint32_t> triangles;  // 3 vertex indices per triangle, from 0
    std::vector<float> segments;      // x, y, z per point, 2 points per segment
};

void appendPoint(std::vector<float> &values, double x, double y, double z) {
    values.push_back(static_cast<float>(x));
    values.push_back(static_cast<float>(y));
    values.push_back(static_cast<float>(z));
}

// Adds the lines of a segments or polylines array as segments
void appendSegments(const occ::handle<Graphic3d_ArrayOfPrimitives> &lines, std::vector<float> &segments) {
    if (lines.IsNull()) return;
    const bool indexed = lines->EdgeNumber() > 0;
    const int count = indexed ? lines->EdgeNumber() : lines->VertexNumber();
    auto add = [&](int i) {
        double x, y, z;
        lines->Vertice(indexed ? lines->Edge(i) : i, x, y, z);
        appendPoint(segments, x, y, z);
    };
    if (lines->Type() == Graphic3d_TOPA_SEGMENTS) {
        for (int i = 1; i < count; i += 2) {
            add(i);
            add(i + 1);
        }
        return;
    }
    // polylines, one per bound, or a single one without bounds
    const int boundCount = std::max(lines->BoundNumber(), 0);
    int start = 1;
    for (int b = 1; b <= std::max(boundCount, 1); b++) {
        const int length = boundCount > 0 ? lines->Bound(b) : count;
        for (int i = start; i + 1 < start + length; i++) {
            add(i);
            add(i + 1);
        }
        start += length;
    }
}

Tessellation capi_tessellate(const TopoDS_Shape &shape) {
    capi_mesh_shape(shape);
    Tessellation t;
    occ::handle<Graphic3d_ArrayOfTriangles> triangles = StdPrs_ShadedShape::FillTriangles(shape);
    if (!triangles.IsNull()) {
        for (int i = 1; i <= triangles->VertexNumber(); i++) {
            double x, y, z, nx = 0, ny = 0, nz = 0;
            triangles->Vertice(i, x, y, z);
            if (triangles->HasVertexNormals()) triangles->VertexNormal(i, nx, ny, nz);
            appendPoint(t.positions, x, y, z);
            appendPoint(t.normals, nx, ny, nz);
        }
        if (triangles->EdgeNumber() > 0) {
            for (int i = 1; i <= triangles->EdgeNumber(); i++) t.triangles.push_back(triangles->Edge(i) - 1);
        } else {
            for (int i = 0; i < triangles->VertexNumber(); i++) t.triangles.push_back(i);
        }
    }
    // the face boundaries, as the viewer draws them; a shape without faces is drawn by its edges
    if (TopExp_Explorer(shape, TopAbs_FACE).More())
        appendSegments(StdPrs_ShadedShape::FillFaceBoundaries(shape), t.segments);
    else
        appendSegments(StdPrs_WFShape::AddAllEdges(shape, new Prs3d_Drawer()), t.segments);
    return t;
}

std::string quoted(const std::string &s) {
    std::ostringstream out;
    out << '"';
    for (unsigned char c : s) {
        if (c == '"' || c == '\\') out << '\\' << c;
        else if (c < 0x20) {
            static const char HEX[] = "0123456789abcdef";
            out << "\\u00" << HEX[c >> 4] << HEX[c & 0xF];
        } else out << c;
    }
    out << '"';
    return out.str();
}

// Shortest text read back as the same float, whatever the locale
std::string number(double v) {
    std::ostringstream out;
    out.imbue(std::locale::classic());
    out.precision(std::numeric_limits<float>::max_digits10);
    out << v;
    return out.str();
}

std::string array(const std::vector<std::string> &items) {
    std::string s = "[";
    for (size_t i = 0; i < items.size(); i++) s += (i ? "," : "") + items[i];
    return s + "]";
}

std::string numbers(std::initializer_list<double> values) {
    std::vector<std::string> items;
    for (double v : values) items.push_back(number(v));
    return array(items);
}

std::string object(const std::vector<std::string> &fields) {
    std::string s = "{";
    for (size_t i = 0; i < fields.size(); i++) s += (i ? "," : "") + fields[i];
    return s + "}";
}

std::string field(const char *name, const std::string &json) {
    return quoted(name) + ":" + json;
}

std::string field(const char *name, int value) {
    return field(name, std::to_string(value));
}

struct Node {
    std::string name;
    gp_Trsf location;
    int mesh = -1;
    std::vector<int> children;
};

// An assembly tree as a glTF scene: a mesh per part, a node per part or assembly, for each place it occupies
class GltfWriter {
public:
    GltfWriter(int frameCount, double fps) : frameCount(fps > 0 ? frameCount : 0), fps(fps) {}

    void add(const CadNode &root) {
        gp_Trsf modelToGltf;
        modelToGltf.SetRotation(gp::OX(), -M_PI / 2);
        modelToGltf.SetScaleFactor(MILLIMETERS_TO_METERS);
        int index = addNode(root, modelToGltf);
        scene.push_back(index);
    }

    void write(const char *fileName) {
        std::string json = this->json();
        json.append((GLB_ALIGNMENT - json.size() % GLB_ALIGNMENT) % GLB_ALIGNMENT, ' ');
        const bool hasBin = !bin.empty();
        const uint32_t total = GLB_HEADER_SIZE + GLB_CHUNK_HEADER_SIZE + static_cast<uint32_t>(json.size())
                               + (hasBin ? GLB_CHUNK_HEADER_SIZE + static_cast<uint32_t>(bin.size()) : 0);
        std::vector<char> glb;
        appendU32(glb, GLB_MAGIC);
        appendU32(glb, GLB_VERSION);
        appendU32(glb, total);
        appendU32(glb, static_cast<uint32_t>(json.size()));
        appendU32(glb, GLB_CHUNK_JSON);
        glb.insert(glb.end(), json.begin(), json.end());
        if (hasBin) {
            appendU32(glb, static_cast<uint32_t>(bin.size()));
            appendU32(glb, GLB_CHUNK_BIN);
            glb.insert(glb.end(), bin.begin(), bin.end());
        }
        std::ofstream out(fileName, std::ios::binary);
        out.write(glb.data(), static_cast<std::streamsize>(glb.size()));
        out.close();
        if (!out) throw std::runtime_error(std::string("cannot write glTF file ") + fileName);
    }

private:
    const int frameCount;
    const double fps;
    std::vector<int> scene;
    std::vector<Node> nodes;
    std::vector<std::string> meshes, materials, accessors, bufferViews, channels, samplers;
    std::vector<char> bin;  // every value is 4 bytes long, so the views stay aligned
    std::map<const CadNode *, int> meshOfPart;
    int edgeMaterial = -1;
    int times = -1;

    static void appendU32(std::vector<char> &bytes, uint32_t v) {
        const char le[4] = {char(v), char(v >> 8), char(v >> 16), char(v >> 24)};
        bytes.insert(bytes.end(), le, le + sizeof le);
    }

    static void appendF32(std::vector<char> &bytes, float v) {
        uint32_t bits;
        std::memcpy(&bits, &v, sizeof bits);
        appendU32(bytes, bits);
    }

    int view(size_t offset, int target) {
        std::vector<std::string> fields = {field("buffer", 0), field("byteOffset", static_cast<int>(offset)),
                                           field("byteLength", static_cast<int>(bin.size() - offset))};
        if (target != TARGET_NONE) fields.push_back(field("target", target));
        bufferViews.push_back(object(fields));
        return static_cast<int>(bufferViews.size()) - 1;
    }

    // Accessor to floats grouped by components (3 for VEC3...), with their bounds when withBounds
    int floatAccessor(const std::vector<float> &values, int components, const char *type, int target, bool withBounds) {
        const size_t offset = bin.size();
        for (float v : values) appendF32(bin, v);
        const int count = static_cast<int>(values.size()) / components;
        std::vector<std::string> fields = {field("bufferView", view(offset, target)),
                                           field("componentType", COMPONENT_FLOAT), field("count", count),
                                           field("type", quoted(type))};
        if (withBounds) {
            std::vector<std::string> min, max;
            for (int c = 0; c < components; c++) {
                float lo = std::numeric_limits<float>::max(), hi = std::numeric_limits<float>::lowest();
                for (size_t i = c; i < values.size(); i += components) {
                    lo = std::min(lo, values[i]);
                    hi = std::max(hi, values[i]);
                }
                min.push_back(number(lo));
                max.push_back(number(hi));
            }
            fields.push_back(field("min", array(min)));
            fields.push_back(field("max", array(max)));
        }
        accessors.push_back(object(fields));
        return static_cast<int>(accessors.size()) - 1;
    }

    int indexAccessor(const std::vector<uint32_t> &indices) {
        const size_t offset = bin.size();
        for (uint32_t i : indices) appendU32(bin, i);
        accessors.push_back(object({field("bufferView", view(offset, TARGET_ELEMENT_ARRAY_BUFFER)),
                                    field("componentType", COMPONENT_UNSIGNED_INT),
                                    field("count", static_cast<int>(indices.size())), field("type", quoted("SCALAR"))}));
        return static_cast<int>(accessors.size()) - 1;
    }

    int material(const std::string &name, const double rgb[3]) {
        std::vector<std::string> fields;
        if (!name.empty()) fields.push_back(field("name", quoted(name)));
        fields.push_back(field("pbrMetallicRoughness", object({
                field("baseColorFactor", numbers({rgb[0], rgb[1], rgb[2], 1})),
                field("metallicFactor", number(0)),
                field("roughnessFactor", number(ROUGHNESS))})));
        materials.push_back(object(fields));
        return static_cast<int>(materials.size()) - 1;
    }

    // Mesh of the part, -1 when it has neither faces nor edges
    int mesh(const CadNode &part) {
        auto found = meshOfPart.find(&part);
        if (found != meshOfPart.end()) return found->second;
        Tessellation t = capi_tessellate(part.shape);
        std::vector<std::string> primitives;
        if (!t.triangles.empty()) {
            double rgb[3] = {DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2]};
            if (part.hasColor) part.color.Values(rgb[0], rgb[1], rgb[2], Quantity_TOC_RGB);
            int position = floatAccessor(t.positions, 3, "VEC3", TARGET_ARRAY_BUFFER, true);
            int normal = floatAccessor(t.normals, 3, "VEC3", TARGET_ARRAY_BUFFER, false);
            primitives.push_back(object({field("attributes", object({field("POSITION", position), field("NORMAL", normal)})),
                                         field("indices", indexAccessor(t.triangles)),
                                         field("material", material(part.name, rgb)),
                                         field("mode", MODE_TRIANGLES)}));
        }
        if (!t.segments.empty()) {
            if (edgeMaterial < 0) edgeMaterial = material("edges", EDGE_COLOR);
            int position = floatAccessor(t.segments, 3, "VEC3", TARGET_ARRAY_BUFFER, true);
            primitives.push_back(object({field("attributes", object({field("POSITION", position)})),
                                         field("material", edgeMaterial), field("mode", MODE_LINES)}));
        }
        int index = -1;
        if (!primitives.empty()) {
            std::vector<std::string> fields;
            if (!part.name.empty()) fields.push_back(field("name", quoted(part.name)));
            fields.push_back(field("primitives", array(primitives)));
            meshes.push_back(object(fields));
            index = static_cast<int>(meshes.size()) - 1;
        }
        meshOfPart[&part] = index;
        return index;
    }

    // A node for node, and one for each of its components, placed at location in its parent
    int addNode(const CadNode &node, const gp_Trsf &location) {
        const int index = static_cast<int>(nodes.size());
        nodes.push_back({node.name, location});
        if (!node.isAssembly) {
            int m = mesh(node);
            nodes[index].mesh = m;
            return index;
        }
        for (const CadNode::Component &c : node.components) {
            int child = addNode(*c.node, c.location);
            nodes[index].children.push_back(child);
            if (moves(c)) animate(child, c);
        }
        return index;
    }

    // Whether the component leaves its location at rest during the animation: the DSL gives every component frames
    bool moves(const CadNode::Component &c) const {
        for (int i = 0; i < frameCount; i++)
            for (int row = 1; row <= 3; row++)
                for (int col = 1; col <= 4; col++)
                    if (std::abs(c.at(i).Value(row, col) - c.location.Value(row, col)) > Precision::Confusion()) return true;
        return false;
    }

    // Seconds of each frame, shared by the animations
    int frameTimes() {
        if (times < 0) {
            std::vector<float> seconds;
            for (int i = 0; i < frameCount; i++) seconds.push_back(static_cast<float>(i / fps));
            times = floatAccessor(seconds, 1, "SCALAR", TARGET_NONE, true);
        }
        return times;
    }

    void animate(int node, const CadNode::Component &c) {
        std::vector<float> translations, rotations;
        gp_Quaternion previous;
        for (int i = 0; i < frameCount; i++) {
            const gp_Trsf &t = c.at(i);
            appendPoint(translations, t.TranslationPart().X(), t.TranslationPart().Y(), t.TranslationPart().Z());
            gp_Quaternion q = t.GetRotation();
            // the same rotation, on the side of the previous one, so that the interpolation takes the short way
            if (i > 0 && q.Dot(previous) < 0) q = -q;
            previous = q;
            for (double v : {q.X(), q.Y(), q.Z(), q.W()}) rotations.push_back(static_cast<float>(v));
        }
        channel(node, "translation", floatAccessor(translations, 3, "VEC3", TARGET_NONE, false));
        channel(node, "rotation", floatAccessor(rotations, 4, "VEC4", TARGET_NONE, false));
    }

    void channel(int node, const char *path, int output) {
        samplers.push_back(object({field("input", frameTimes()), field("output", output),
                                   field("interpolation", quoted("LINEAR"))}));
        channels.push_back(object({field("sampler", static_cast<int>(samplers.size()) - 1),
                                   field("target", object({field("node", node), field("path", quoted(path))}))}));
    }

    static std::string nodeJson(const Node &n) {
        std::vector<std::string> fields;
        if (!n.name.empty()) fields.push_back(field("name", quoted(n.name)));
        if (n.mesh >= 0) fields.push_back(field("mesh", n.mesh));
        if (!n.children.empty()) {
            std::vector<std::string> children;
            for (int c : n.children) children.push_back(std::to_string(c));
            fields.push_back(field("children", array(children)));
        }
        const gp_XYZ &p = n.location.TranslationPart();
        gp_Quaternion q = n.location.GetRotation();
        const double s = n.location.ScaleFactor();
        fields.push_back(field("translation", numbers({p.X(), p.Y(), p.Z()})));
        fields.push_back(field("rotation", numbers({q.X(), q.Y(), q.Z(), q.W()})));
        fields.push_back(field("scale", numbers({s, s, s})));
        return object(fields);
    }

    std::string json() const {
        std::vector<std::string> sceneNodes, nodeItems;
        for (int n : scene) sceneNodes.push_back(std::to_string(n));
        for (const Node &n : nodes) nodeItems.push_back(nodeJson(n));
        std::vector<std::string> fields = {
                field("asset", object({field("version", quoted("2.0")), field("generator", quoted("cad-dsl-native"))})),
                field("scene", 0),
                field("scenes", array({object({field("nodes", array(sceneNodes))})})),
                field("nodes", array(nodeItems))};
        // glTF arrays may not be empty: they are left out instead
        if (!meshes.empty()) fields.push_back(field("meshes", array(meshes)));
        if (!materials.empty()) fields.push_back(field("materials", array(materials)));
        if (!accessors.empty()) fields.push_back(field("accessors", array(accessors)));
        if (!bufferViews.empty()) fields.push_back(field("bufferViews", array(bufferViews)));
        if (!bin.empty()) fields.push_back(field("buffers", array({object({field("byteLength", static_cast<int>(bin.size()))})})));
        if (!channels.empty())
            fields.push_back(field("animations", array({object({field("name", quoted("animation")),
                                                                field("channels", array(channels)),
                                                                field("samplers", array(samplers))})})));
        return object(fields);
    }
};

void write_glb_file(const CadNode &root, int frameCount, double fps, const char *fileName) {
    GltfWriter writer(frameCount, fps);
    writer.add(root);
    writer.write(fileName);
}
}

void capi_mesh_shape(const TopoDS_Shape &shape) {
    Bnd_Box box;
    BRepBndLib::Add(shape, box);
    double deflection = box.IsVoid() ? DEFAULT_DEFLECTION : RELATIVE_DEFLECTION * std::sqrt(box.SquareExtent());
    BRepMesh_IncrementalMesh mesh(shape, deflection, false, ANGULAR_DEFLECTION, true);
}

extern "C" int write_glb(const TopoDS_Shape *shape, const char *fileName) {
    TRACE("");
    return guard(__func__, [&] {
        CadNode part;
        part.shape = *shape;
        write_glb_file(part, 0, 0, fileName);
        return 1;
    });
}

extern "C" int write_glb_assembly(const occ::handle<CadNode> *assembly, int frameCount, double fps, const char *fileName) {
    TRACE("");
    return guard(__func__, [&] {
        write_glb_file(**assembly, frameCount, fps, fileName);
        return 1;
    });
}
