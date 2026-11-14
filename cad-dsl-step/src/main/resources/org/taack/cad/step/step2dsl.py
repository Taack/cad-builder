"""
STEP to CAD DSL: reads a STEP file in FreeCAD and writes a Groovy script that rebuilds it with the DSL.

The assembly tree is kept: a part per distinct solid, placed as often as the STEP places it, and a sub-assembly per
distinct sub-assembly. Each solid is recognized, from the simplest to the most general form:
  - a primitive: an axis-aligned box, a cylinder, a sphere;
  - a 2.5D solid along an axis, all its faces normal or parallel to it: prisms of the cross-sections between the
    heights of its planar faces;
  - a solid of revolution: the revolution of its section by a half-plane through the axis; a threaded one too, its
    thread (free-form flanks between two close radii) made of rings;
  - a threaded solid of revolution with other features: the threaded solid as base, the flats across its thread
    cut from it (OCCT does not defeature them), then the other features (pockets, holes...);
  - else a 2.5D base, the faces that do not fit removed by defeaturing, then the material removed from it cut (holes,
    pockets, fillets...) and the material added to it fused, each one a 2.5D solid, a primitive or a revolution.
    Free-form faces, and the faces touching them, are not removed: a solid that keeps some is approximated.
Curves other than lines and arcs of circle are approximated by polylines. What cannot be converted is reported in the
script, which says how far its volume is from the STEP one when the solid is approximated.
"""
import math
import os

import FreeCAD as App
import Part

V = App.Vector
# Tolerances: on lengths (mm), on the cosines of angles
LIN = 1e-6
COS = 1e-7
# Largest distance between a curve and the polyline approximating it
DEFLECTION = 0.01
# Most heights a solid is cut at, and most directions tried for it; beyond, it is approximated
MAX_LEVELS = 50
MAX_AXES = 8
# Most faces removed to find the base of a solid, and most groups of them tried one by one
MAX_FEATURE_FACES = 200
MAX_FEATURE_GROUPS = 40
MAX_DEFEATURING_PASSES = 3


# ---------------------------------------------------------------------------------------------------------------------
# Groovy text
# ---------------------------------------------------------------------------------------------------------------------

def num(x):
    if abs(x) < 5e-7:
        return '0'
    s = ('%.6f' % x).rstrip('0').rstrip('.')
    return '0' if s == '-0' else s


def vec(v):
    return 'new Vec(%s, %s, %s)' % (num(v.x), num(v.y), num(v.z))


def string(s):
    return "'" + s.replace('\\', '\\\\').replace("'", "\\'") + "'"


def indent(lines, n=1):
    return ['    ' * n + l if l else l for l in lines]


# ---------------------------------------------------------------------------------------------------------------------
# Geometry
# ---------------------------------------------------------------------------------------------------------------------

def unit(v):
    return v * (1.0 / v.Length)


def canonical(d):
    """d, or -d, pointing to +Z (else +Y, else +X)"""
    d = unit(d)
    eps = 1e-12
    if d.z < -eps or (abs(d.z) <= eps and (d.y < -eps or (abs(d.y) <= eps and d.x < 0))):
        d = -d
    return d


def parallel(a, b):
    return abs(abs(a.dot(b)) - 1) < COS


def perpendicular(a, b):
    return abs(a.dot(b)) < COS


def same_direction(directions, d):
    return any(parallel(e, d) for e in directions)


def samples(face, n=5):
    """Normals of the face on a grid of its parameters"""
    u0, u1, v0, v1 = face.ParameterRange
    normals = []
    for i in range(n):
        for j in range(n):
            u = u0 + (u1 - u0) * (i + 0.5) / n
            v = v0 + (v1 - v0) * (j + 0.5) / n
            try:
                normals.append(face.normalAt(u, v))
            except Exception:
                pass
    return normals


def face_kind(face, d):
    """'cap' when the face is normal to d, 'side' when it runs along d, None else"""
    s = face.Surface
    if isinstance(s, Part.Plane):
        if parallel(s.Axis, d):
            return 'cap'
        return 'side' if perpendicular(s.Axis, d) else None
    if isinstance(s, Part.Cylinder):
        return 'side' if parallel(s.Axis, d) else None
    if isinstance(s, (Part.Sphere, Part.Toroid, Part.Cone)):
        return None
    normals = samples(face)
    if normals and all(abs(n.dot(d)) < 1e-6 for n in normals):
        return 'side'
    if normals and all(abs(abs(n.dot(d)) - 1) < 1e-6 for n in normals):
        return 'cap'
    return None


def is_prismatic(shape, d):
    return all(face_kind(f, d) is not None for f in shape.Faces)


def axis_candidates(shape):
    """Directions a 2.5D solid may run along: normals of the planar faces, by decreasing area, and cylinder axes"""
    weighted = []
    for f in shape.Faces:
        s = f.Surface
        if isinstance(s, Part.Plane):
            weighted.append((canonical(s.Axis), f.Area))
        elif isinstance(s, Part.Cylinder):
            weighted.append((canonical(s.Axis), f.Area))
    directions = []
    for d, area in weighted:
        for e in directions:
            if parallel(e[0], d):
                e[1] += area
                break
        else:
            directions.append([d, area])
    directions.sort(key=lambda e: -e[1])
    result = [e[0] for e in directions[:MAX_AXES]]
    for d in (V(0, 0, 1), V(1, 0, 0), V(0, 1, 0)):
        if not same_direction(result, d):
            result.append(d)
    return result


def fitting_area(shape, d):
    return sum(f.Area for f in shape.Faces if face_kind(f, d) is not None)


def heights(shape, d):
    """Heights along d of the caps of the solid and of its ends, increasing; at most MAX_LEVELS, those of the
    largest caps, when it has more (it is then not 2.5D)"""
    ends = [p.Point.dot(d) for p in shape.Vertexes]
    caps = sorted(((f.Vertexes[0].Point.dot(d) if f.Vertexes else f.CenterOfMass.dot(d), f.Area)
                   for f in shape.Faces if face_kind(f, d) == 'cap'), key=lambda c: -c[1])
    levels = distinct(ends + [z for z, area in caps])
    if len(levels) <= MAX_LEVELS:
        return levels
    levels = distinct([min(ends), max(ends)])
    for z, area in caps:
        if len(levels) >= MAX_LEVELS:
            break
        levels = distinct(levels + [z])
    return levels


def distinct(zs):
    levels = []
    for z in sorted(zs):
        if not levels or z - levels[-1] > LIN:
            levels.append(z)
    return levels


def section_face(wires):
    try:
        return Part.makeFace(wires, 'Part::FaceMakerBullseye')
    except Exception:
        return None


def same_section(a, b):
    """Whether two planar faces (in the same plane) are the same region"""
    if a is None or b is None or abs(a.Area - b.Area) > 1e-7 * max(a.Area, 1):
        return False
    try:
        return abs(a.common(b).Area - a.Area) < 1e-6 * max(a.Area, 1)
    except Exception:
        return False


class Slab:
    def __init__(self, z0, z1, wires, face):
        self.z0 = z0
        self.z1 = z1
        self.wires = wires
        self.face = face


def slabs(shape, d):
    """The prisms whose union is the 2.5D solid: its cross-sections between consecutive heights, the same ones merged"""
    result = []
    levels = heights(shape, d)
    for z0, z1 in zip(levels, levels[1:]):
        middle = (z0 + z1) / 2
        wires = [w for w in shape.slice(d, middle) if w.isClosed()]
        if not wires:
            continue
        # the section at the bottom of the slab
        wires = [w.translated(d * (z0 - middle)) for w in wires]
        face = section_face(wires)
        if result and abs(result[-1].z1 - z0) < LIN and result[-1].face is not None and face is not None and \
                same_section(result[-1].face.translated(d * (z0 - result[-1].z0)), face):
            result[-1].z1 = z1
        else:
            result.append(Slab(z0, z1, wires, face))
    return result


def flattening(wires):
    """The projection of points on the plane of the wires: the vertices of a section drift from it by the tolerances
    of the STEP file, by up to 1e-4 mm, and OCCT makes no face of a wire that is not planar"""
    try:
        plane = Part.makeCompound(wires).findPlane(1e-3)
    except Exception:
        plane = None
    if plane is None:
        return lambda p: p
    origin, n = plane.Position, unit(plane.Axis)
    for k in range(3):
        if abs(n[k]) > 1 - 1e-9:
            # a plane across an axis at a written coordinate: the points rounded to 1e-6 stay in it, the edges having
            # a tolerance of 1e-7
            level = float(num(origin[k]))

            def snap(p, k=k, level=level):
                q = V(p)
                q[k] = level
                return q
            return snap
    return lambda p: p - n * (p - origin).dot(n)


def arc(start, via, end):
    """An arc of circle, a line when it is almost straight (its points would be aligned once written)"""
    chord = end - start
    if chord.Length > 0:
        offset = via - start
        sagitta = (offset - chord * (offset.dot(chord) / chord.Length ** 2)).Length
        if sagitta < 1e-4:
            return 'edge(%s)' % vec(end)
    return 'arc(%s, %s)' % (vec(end), vec(via))


def circle_via(points):
    """The middle point of the arc of circle through the points, None if they are not on an arc"""
    a, b, c = points[0], points[len(points) // 2], points[-1]
    try:
        circle = Part.Circle(a, b, c)
    except Exception:
        return None
    if all(abs((p - circle.Center).Length - circle.Radius) < 1e-6 for p in points):
        return b
    return None


def surface_axis(s):
    """(point, direction) of the axis of a surface of revolution, None for other surfaces"""
    if isinstance(s, (Part.Cylinder, Part.Toroid)):
        return s.Center, unit(s.Axis)
    if isinstance(s, Part.Cone):
        return s.Apex, unit(s.Axis)
    if isinstance(s, Part.SurfaceOfRevolution):
        return s.Location, unit(s.Direction)
    return None


def turns_around(face, axis):
    """Whether the face is a surface of revolution around the axis, or a plane normal to it"""
    s = face.Surface
    if isinstance(s, Part.Plane):
        return parallel(s.Axis, axis[1])
    if isinstance(s, Part.Sphere):
        return on_axis(axis, s.Center)
    a = surface_axis(s)
    return a is not None and parallel(axis[1], a[1]) and on_axis(axis, a[0])


def is_thread(face, axis):
    """Whether the face is the flank of a thread around the axis: a free-form face between two close radii, winding
    around the axis"""
    if not isinstance(face.Surface, Part.BSplineSurface):
        return False
    try:
        points = face.tessellate(0.05)[0]
    except Exception:
        return False
    origin, d = axis
    x = unit(Part.Plane(V(0, 0, 0), d).value(1, 0) - Part.Plane(V(0, 0, 0), d).value(0, 0))
    y = d.cross(x)
    radii = []
    sectors = set()
    for p in points:
        v = p - origin
        radial = v - d * v.dot(d)
        radii.append(radial.Length)
        sectors.add(int((math.atan2(radial.dot(y), radial.dot(x)) + math.pi) / (math.pi / 6)) % 12)
    if not radii or min(radii) < LIN:
        return False
    # a band narrower than a fifth of its radius, over a quarter turn at least
    return max(radii) - min(radii) <= 0.2 * max(radii) and len(sectors) >= 4


def main_axis(shape):
    """The axis of the surfaces of revolution of the solid of largest area, None if it has none"""
    axes = []
    for f in shape.Faces:
        a = surface_axis(f.Surface)
        if a is None:
            continue
        for e in axes:
            if parallel(e[0][1], a[1]) and on_axis(e[0], a[0]):
                e[1] += f.Area
                break
        else:
            axes.append([a, f.Area])
    if not axes:
        return None
    a = max(axes, key=lambda e: e[1])[0]
    return a[0], canonical(a[1])


def revolution_axis(shape, threads=False, flat_faces=()):
    """(point, direction) of the axis all the faces of the solid turn around, None if they do not; with threads, the
    flanks of threads around it are accepted, and the flat faces"""
    axis = None
    for f in shape.Faces:
        a = surface_axis(f.Surface)
        if a is None:
            continue
        if axis is None:
            axis = a
        elif not (parallel(axis[1], a[1]) and on_axis(axis, a[0])):
            return None
    if axis is None:
        return None
    flanks = [e for f in shape.Faces if threads and is_thread(f, axis) for e in f.Edges]
    for f in shape.Faces:
        if not (turns_around(f, axis) or (threads and is_thread(f, axis)) or thread_end(f, axis, flanks)
                or any(f.isSame(g) for g in flat_faces)):
            return None
    return axis[0], canonical(axis[1])


def thread_section(shape, axis):
    """The radial direction of the section of a threaded solid farthest from the ends of its threads, where a tooth
    ends in the section"""
    origin, d = axis
    flanks = [e for f in shape.Faces if is_thread(f, axis) for e in f.Edges]
    ends = [f.CenterOfMass - origin for f in shape.Faces if thread_end(f, axis, flanks)] + \
           [f.CenterOfMass - origin for f in flats(shape, axis)]
    x = unit(radial_direction(d))
    y = d.cross(x)
    directions = [x * math.cos(i * math.pi / 8) + y * math.sin(i * math.pi / 8) for i in range(16)]
    return max(directions, key=lambda r: min([r.getAngle(e - d * e.dot(d)) for e in ends] or [0]))


def thread_end(face, axis, flanks):
    """Whether the face closes a thread at one of its ends: a plane along the axis, bounded by edges of the flanks, in
    the band of the thread (a flat cutting the core of the thread, under its root, is no end)"""
    if not (flanks and isinstance(face.Surface, Part.Plane) and abs(unit(face.Surface.Axis).dot(axis[1])) < 1e-6
            and any(e.isSame(g) for e in face.Edges for g in flanks)):
        return False
    # the point of the plane closest to the axis, at the middle of the face: on a flat, in the band of an end
    origin, d = axis
    foot = face.Surface.projectPoint(origin + d * (face.CenterOfMass - origin).dot(d))
    if not face.isInside(foot, 1e-6, True):
        return True
    root = min(distance_to_axis(axis, v.Point) for e in flanks for v in e.Vertexes)
    return distance_to_axis(axis, foot) > root - 1e-3


def distance_to_axis(axis, p):
    v = p - axis[0]
    return (v - axis[1] * v.dot(axis[1])).Length


def flats(shape, axis):
    """The flats across the thread of a threaded solid of revolution: planes along the axis facing away from it,
    bounded by edges of the flanks, which are no thread ends. Defeaturing does not remove them, as they touch the
    flanks: they are cut from the revolution instead"""
    flanks = [e for f in shape.Faces if is_thread(f, axis) for e in f.Edges]
    result = []
    for f in shape.Faces:
        s = f.Surface
        if not isinstance(s, Part.Plane) or abs(unit(s.Axis).dot(axis[1])) > 1e-6 or thread_end(f, axis, flanks) \
                or not any(e.isSame(g) for e in f.Edges for g in flanks):
            continue
        foot = s.projectPoint(axis[0])
        n = f.normalAt(*f.Surface.parameter(f.CenterOfMass))
        if distance_to_axis(axis, foot) > LIN and (foot - axis[0]).dot(n) > 0:
            result.append(f)
    return result


def on_axis(axis, p):
    v = p - axis[0]
    return (v - axis[1] * v.dot(axis[1])).Length < 1e-5


def radial_direction(d):
    """A direction normal to d, the same for the same d"""
    return Part.Plane(V(0, 0, 0), d).value(1, 0) - Part.Plane(V(0, 0, 0), d).value(0, 0)


def half_plane_section(shape, axis, radial=None):
    """Wires of the section of the solid by a half-plane bounded by the axis, along the radial direction"""
    origin, d = axis
    radial = radial or radial_direction(d)
    size = shape.BoundBox.DiagonalLength * 2 + 10
    # centered along the axis on the solid
    c = origin + d * (shape.BoundBox.Center - origin).dot(d)
    corners = [c - d * size, c + d * size, c + d * size + radial * size, c - d * size + radial * size]
    half_plane = Part.Face(Part.makePolygon(corners + [corners[0]]))
    section = shape.common(half_plane)
    return [w for f in section.Faces for w in f.Wires]


# ---------------------------------------------------------------------------------------------------------------------
# Solids to DSL statements
# ---------------------------------------------------------------------------------------------------------------------

class SolidWriter:
    """The statements building a solid; they start with {R}, the receiver (empty in a part closure)"""

    def __init__(self):
        self.notes = []
        # direction of the primitives, which stays set
        self.direction = V(0, 0, 1)
        # projection of the points of the wires written on their plane
        self.flat = lambda p: p

    def at(self, origin, d, statement):
        """A primitive along d at origin"""
        lines = []
        if not (d - self.direction).Length < 1e-12:
            lines.append('direction(%s)' % vec(d))
            self.direction = d
        lines.append(statement)
        if origin.Length < 1e-9:
            return ['{R}' + l for l in lines]
        return ['{R}position(%s) {' % vec(origin)] + indent(lines) + ['}']

    def wire_statements(self, wires, end):
        """{R}wireFrom(...) { ... }.move(...) { ... }<end>, for planar wires"""
        self.flat = flattening(wires)
        lines = []
        for i, w in enumerate(wires):
            start, body = self.wire_body(w)
            head = ('{R}wireFrom(%s) {' if i == 0 else '}.move(%s) {') % vec(start)
            lines.append(head)
            lines += indent(body)
        lines.append('}' + end)
        return lines

    def wire_body(self, wire):
        """Start point and statements of a closed wire"""
        edges = wire.OrderedEdges
        flat = self.flat
        vertexes = [flat(v.Point) for v in wire.OrderedVertexes]
        start = vertexes[0]
        body = []
        position = start
        for i, e in enumerate(edges):
            end = vertexes[(i + 1) % len(vertexes)] if len(vertexes) > 1 else start
            f, l = e.ParameterRange
            c = e.Curve
            forward = (e.valueAt(f) - position).Length <= (e.valueAt(l) - position).Length
            if isinstance(c, Part.Line):
                body.append('edge(%s)' % vec(end))
            elif isinstance(c, Part.Circle):
                if e.isClosed():
                    # a full circle: two half circles
                    q = (l - f) / 4
                    a = [flat(e.valueAt(f + k * q)) for k in range(4)] if forward else \
                        [flat(e.valueAt(l - k * q)) for k in range(4)]
                    body.append('arc(%s, %s)' % (vec(a[2]), vec(a[1])))
                    body.append('arc(%s, %s)' % (vec(position), vec(a[3])))
                else:
                    body.append(arc(position, flat(e.valueAt((f + l) / 2)), end))
            else:
                points = [flat(p) for p in e.discretize(Deflection=DEFLECTION)]
                if not forward:
                    points.reverse()
                points[-1] = end
                if len(points) == 2:
                    # a straight B-spline
                    body.append('edge(%s)' % vec(end))
                    position = end
                    continue
                via = circle_via(points)
                if via is not None:
                    body.append(arc(position, via, end))
                    position = end
                    continue
                body.append('// %s approximated by %d segments' % (type(c).__name__, len(points) - 1))
                if len(points) > 4:
                    body.append('[%s].each { edge(new Vec(it[0], it[1], it[2])) }'
                                % ', '.join('[%s, %s, %s]' % (num(p.x), num(p.y), num(p.z)) for p in points[1:]))
                else:
                    body += ['edge(%s)' % vec(p) for p in points[1:]]
            position = end
        return start, body

    def primitive(self, shape):
        """A box along the axes, a cylinder or a sphere; None if the solid is none of them"""
        faces = shape.Faces
        kinds = sorted(type(f.Surface).__name__ for f in faces)
        points = [v.Point for v in shape.Vertexes]
        if kinds == ['Plane'] * 6 and all(same_direction([V(1, 0, 0), V(0, 1, 0), V(0, 0, 1)], f.Surface.Axis)
                                          for f in faces):
            low = V(min(p.x for p in points), min(p.y for p in points), min(p.z for p in points))
            size = V(max(p.x for p in points), max(p.y for p in points), max(p.z for p in points)) - low
            if abs(shape.Volume - size.x * size.y * size.z) < 1e-6 * shape.Volume:
                return self.at(low, V(0, 0, 1), 'box(%s, %s, %s)' % (num(size.x), num(size.y), num(size.z)))
        if kinds == ['Cylinder', 'Plane', 'Plane']:
            c = [f.Surface for f in faces if isinstance(f.Surface, Part.Cylinder)][0]
            d = canonical(c.Axis)
            zs = [v.Point.dot(d) for v in shape.Vertexes]
            h = max(zs) - min(zs)
            if abs(shape.Volume - math.pi * c.Radius ** 2 * h) < 1e-6 * shape.Volume:
                base = c.Center + d * (min(zs) - c.Center.dot(d))
                return self.at(base, d, 'cylinder(%s, %s)' % (num(c.Radius), num(h)))
        if kinds == ['Sphere'] and abs(shape.Volume - 4 / 3 * math.pi * faces[0].Surface.Radius ** 3) < 1e-6 * shape.Volume:
            s = faces[0].Surface
            return self.at(s.Center, self.direction, 'sphere(%s)' % num(s.Radius))
        return None

    def prismatic(self, shape, d, cut):
        """The 2.5D solid along d, as prisms (holes when cut)"""
        lines = []
        for s in slabs(shape, d):
            lines += self.wire_statements(s.wires, '.toFace().%s(%s)' % ('hole' if cut else 'prism', vec(d * (s.z1 - s.z0))))
        return lines

    def approximated(self, shape, d, cut):
        """Prisms of the cross-sections of a solid that is not 2.5D, noted with the volume they make"""
        pieces = slabs(shape, d)
        volume = sum(s.face.Area * (s.z1 - s.z0) for s in pieces if s.face is not None)
        self.notes.append('%s of volume %s approximated by %d prisms along %s, of volume %s'
                          % ('a hole' if cut else 'a solid', num(shape.Volume), len(pieces), vec(d), num(volume)))
        lines = []
        for s in pieces:
            lines += self.wire_statements(s.wires, '.toFace().%s(%s)' % ('hole' if cut else 'prism', vec(d * (s.z1 - s.z0))))
        return lines

    def revolved(self, shape, axis, radial=None):
        wires = half_plane_section(shape, axis, radial)
        if not wires:
            return None
        return self.wire_statements(wires, '.toFace().revolution(%s, %s, 360)' % (vec(axis[0]), vec(axis[1])))

    def solid_or_box(self, shape):
        """The statements of the solid, its bounding box when it cannot be converted"""
        try:
            return self.solid(shape)
        except Exception as e:
            bb = shape.BoundBox
            self.notes.append('a solid of volume %s could not be converted (%s), replaced by its bounding box'
                              % (num(shape.Volume), e))
            self.direction = V(0, 0, 1)
            return ['{R}position(%s) {' % vec(V(bb.XMin, bb.YMin, bb.ZMin)), '    direction(Vec.vZ)',
                    '    box(%s, %s, %s)' % (num(bb.XLength), num(bb.YLength), num(bb.ZLength)), '}']

    def solid(self, shape):
        """Statements making the solid, the first ones creating it"""
        lines = self.primitive(shape)
        if lines:
            return lines
        candidates = axis_candidates(shape)
        for d in candidates:
            if is_prismatic(shape, d):
                return self.prismatic(shape, d, False)
        axis = revolution_axis(shape)
        if axis is not None:
            lines = self.revolved(shape, axis)
            if lines:
                return lines
        axis = revolution_axis(shape, threads=True)
        if axis is not None:
            lines = self.threaded(shape, axis)
            if lines:
                return lines
        return self.threaded_base(shape) or self.featured(shape, candidates)

    def threaded(self, shape, axis):
        """A threaded solid of revolution: the revolution of its section, the thread made of rings"""
        lines = self.revolved(shape, axis, thread_section(shape, axis))
        if lines:
            flanks = [f for f in shape.Faces if is_thread(f, axis)]
            self.notes.append('a thread of %d faces around %s drawn as rings, not as a helix' % (len(flanks), vec(axis[1])))
        return lines

    def threaded_base(self, shape):
        """A threaded solid of revolution as base, then the material removed from it and added to it; None if the
        solid has no thread"""
        axis = main_axis(shape)
        if axis is None or not any(is_thread(f, axis) for f in shape.Faces):
            return None
        base = shape
        # OCCT may leave some of the features: removed again, while it removes some
        for _ in range(MAX_DEFEATURING_PASSES):
            kept = flats(base, axis)
            features = [f for f in base.Faces if not turns_around(f, axis) and not is_thread(f, axis)
                        and not any(f.isSame(g) for g in kept)]
            if not features or len(features) > MAX_FEATURE_FACES:
                break
            b = defeatured(base, features)
            if b is None:
                # with one of the shoulders, the other planes cut being ends of the solid, then with all of them
                shoulders = notched(base, features, axis)
                for extra in [[f] for f in shoulders] + ([shoulders] if len(shoulders) > 1 else []):
                    b = defeatured(base, features + extra)
                    if b is not None:
                        break
            if b is None or len(b.Faces) >= len(base.Faces):
                break
            base = b
        if base is shape and not flats(base, axis) or revolution_axis(base, True, flats(base, axis)) is None:
            return None
        lines = self.threaded(base, axis)
        if not lines:
            return None
        for f in flats(base, axis):
            lines += self.flat_cut(base, axis, f)
        for s in shape.cut(base).Solids:
            lines += self.feature(s, False)
        for s in base.cut(shape).Solids:
            lines += self.feature(s, True)
        return lines

    def featured(self, shape, candidates):
        """A base, defeatured from the solid, then the material removed from it and added to it"""
        d = max(candidates, key=lambda c: fitting_area(shape, c))
        features = [f for f in shape.Faces if face_kind(f, d) is None]
        base = defeatured(shape, features) if len(features) <= MAX_FEATURE_FACES else None
        if base is None or not is_prismatic(base, d):
            return self.approximated(shape, d, False)
        lines = self.prismatic(base, d, False)
        added = shape.cut(base).Solids
        removed = base.cut(shape).Solids
        for s in added:
            lines += self.feature(s, False)
        for s in removed:
            lines += self.feature(s, True)
        return lines

    def flat_cut(self, shape, axis, face):
        """The cut of a flat from a threaded solid of revolution: a prism from the plane of the face, over its length
        along the axis (past the ends of the solid it reaches), facing away from the axis"""
        origin, d = axis
        n = unit(face.normalAt(*face.Surface.parameter(face.CenterOfMass)))
        foot = face.Surface.projectPoint(origin)
        w = d.cross(n)
        size = shape.BoundBox.DiagonalLength
        along = [v.Point.dot(d) for v in face.Vertexes]
        ends = [v.Point.dot(d) for v in shape.Vertexes]
        a, b = min(along), max(along)
        if a - min(ends) < LIN:
            a -= 1
        if max(ends) - b < LIN:
            b += 1
        base = foot - d * foot.dot(d)
        corners = [base + d * a - w * size, base + d * b - w * size, base + d * b + w * size, base + d * a + w * size]
        wire = Part.makePolygon(corners + [corners[0]])
        self.notes.append('a flat on the plane through %s facing %s' % (vec(foot), vec(n)))
        return self.wire_statements([wire], '.toFace().hole(%s)' % vec(n * size))

    def feature(self, shape, cut):
        """Statements adding a solid to the one built, or cutting it"""
        if shape.Volume < 1e-9:
            return []
        for d in axis_candidates(shape):
            if is_prismatic(shape, d):
                return self.prismatic(shape, d, cut)
        lines = self.primitive(shape)
        axis = revolution_axis(shape) if lines is None else None
        if lines is None and axis is not None:
            lines = self.revolved(shape, axis)
        if lines is None:
            # prisms join the solid (or cut it) by themselves, but not inside a cut or a fuse
            d = max(axis_candidates(shape), key=lambda c: fitting_area(shape, c))
            return self.approximated(shape, d, cut)
        # primitives and revolutions do not join the solid by themselves
        return ['{R}%s {' % ('cut' if cut else 'fuse')] + indent([l.replace('{R}', '') for l in lines]) + ['}']


def defeatured(shape, faces):
    """The solid without the faces (its features), None if they cannot be removed. Faces touching a free-form face
    are kept, and free-form faces themselves: OCCT takes minutes for each one, or does not end (the helical flanks of a
    thread, a flat along it)"""
    if not faces:
        return shape
    free_form = [e for f in shape.Faces if isinstance(f.Surface, Part.BSplineSurface) for e in f.Edges]
    faces = [f for f in faces if not any(e.isSame(g) for e in f.Edges for g in free_form)]
    if not faces:
        return None
    try:
        base = shape.defeaturing(faces)
        if base.isValid() and len(base.Solids) == 1 and not base.isSame(shape):
            return base.Solids[0]
    except Exception:
        pass
    # feature by feature (the faces connected to each other), keeping what can be removed
    base = shape
    for group in connected(faces)[:MAX_FEATURE_GROUPS]:
        try:
            b = base.defeaturing([g for g in base.Faces if any(g.isSame(f) for f in group)])
            if b.isValid() and len(b.Solids) == 1:
                base = b.Solids[0]
        except Exception:
            pass
    return base if not base.isSame(shape) else None


def notched(shape, features, axis):
    """The planes normal to the axis cut by the features, not bounded by circles alone: a flat leaves a shoulder that
    OCCT does not extend when removing it"""
    return [f for f in shape.Faces
            if isinstance(f.Surface, Part.Plane) and turns_around(f, axis) and not any(f.isSame(g) for g in features)
            and any(not isinstance(e.Curve, Part.Circle) for e in f.Edges)
            and any(e.isSame(h) for e in f.Edges for g in features for h in g.Edges)]


def connected(faces):
    """The faces in groups sharing edges"""
    groups = []
    for f in faces:
        joined = [g for g in groups if any(e.isSame(h) for e in f.Edges for o in g for h in o.Edges)]
        merged = [f] + [o for g in joined for o in g]
        groups = [g for g in groups if g not in joined] + [merged]
    return groups


# ---------------------------------------------------------------------------------------------------------------------
# Assembly tree
# ---------------------------------------------------------------------------------------------------------------------

class Node:
    def __init__(self, label, placement, shape=None, children=None):
        self.label = label
        self.placement = placement
        self.shape = shape
        self.children = children or []

    @property
    def is_part(self):
        return self.shape is not None


def is_node(o):
    return o.TypeId in ('App::Part', 'App::Link', 'App::LinkGroup') or \
        (o.isDerivedFrom('Part::Feature') and not o.isDerivedFrom('Part::Part2DObject'))


def tree(o):
    if o.TypeId == 'App::Link':
        n = tree(o.LinkedObject)
        n.placement = o.Placement
        return n
    if o.TypeId in ('App::Part', 'App::LinkGroup'):
        children = [tree(c) for c in o.Group if is_node(c)]
        return Node(o.Label, o.Placement, children=[c for c in children if c is not None])
    shape = o.Shape.copy()
    shape.Placement = App.Placement()
    solids = shape.Solids
    if len(solids) == 1:
        return Node(o.Label, o.Placement, shape=solids[0])
    if not solids:
        return None
    return Node(o.Label, o.Placement, children=[Node('%s %d' % (o.Label, i + 1), App.Placement(), shape=s)
                                                for i, s in enumerate(solids)])


def fingerprint(shape):
    """Same for the same solid, wherever it is in the STEP (its coordinates are local)"""
    m = shape.MatrixOfInertia
    c = shape.CenterOfMass
    values = [shape.Volume, shape.Area, len(shape.Faces), len(shape.Edges), c.x, c.y, c.z] + \
             [m.A[i] for i in (0, 1, 2, 5, 6, 10)]
    return tuple(round(v, 4) for v in values)


def placement_statements(pl):
    lines = []
    if abs(pl.Rotation.Angle) > 1e-12:
        lines.append('rotate(%s, %s)' % (vec(pl.Rotation.Axis), num(math.degrees(pl.Rotation.Angle))))
    if pl.Base.Length > 1e-9:
        lines.append('translate(%s, %s, %s)' % (num(pl.Base.x), num(pl.Base.y), num(pl.Base.z)))
    return lines


class Script:
    def __init__(self):
        self.notes = []
        self.parts = {}      # fingerprint -> (variable, lines)
        self.assemblies = {}  # signature -> (variable, lines)
        self.order = []      # definitions, in order

    def part(self, node):
        key = fingerprint(node.shape)
        if key not in self.parts:
            name = 'part%d' % (len(self.parts) + 1)
            writer = SolidWriter()
            lines = [l.replace('{R}', '') for l in writer.solid_or_box(node.shape)]
            self.notes += ['%s: %s' % (node.label, n) for n in writer.notes]
            self.parts[key] = name
            self.order += ['def %s = part(%s) {' % (name, string(node.label))] + indent(lines) + ['}']
        return self.parts[key]

    def placements(self, node):
        lines = []
        for child in node.children:
            name = self.part(child) if child.is_part else self.assembly(child)
            moves = placement_statements(child.placement)
            lines.append('place(%s)%s' % (name, ' { %s }' % '; '.join(moves) if moves else ''))
        return lines

    def assembly(self, node):
        lines = self.placements(node)
        # the same sub-assembly placed several times is read several times, renamed by FreeCAD (axle, axle001)
        key = tuple(lines)
        if key not in self.assemblies:
            name = 'assembly%d' % (len(self.assemblies) + 1)
            self.assemblies[key] = name
            self.order += ['def %s = assembly(%s) {' % (name, string(node.label))] + indent(lines) + ['}']
        return self.assemblies[key]


def convert(path):
    """The Groovy script rebuilding the STEP file with the DSL"""
    import Import
    doc = App.newDocument('Step')
    try:
        Import.insert(path, doc.Name)
        roots = [o for o in doc.Objects if is_node(o) and o.getParentGeoFeatureGroup() is None and
                 o.getParentGroup() is None and not any(is_node(p) and p.TypeId == 'App::Link' for p in o.InList)]
        nodes = [n for n in (tree(o) for o in roots) if n is not None]
        if not nodes:
            raise ValueError('%s: no solid found' % path)
        name = os.path.basename(path)
        header = ['/*', ' * %s converted by step2dsl.' % name]
        if len(nodes) == 1 and nodes[0].is_part:
            shape = nodes[0].shape.copy()
            shape.Placement = nodes[0].placement
            writer = SolidWriter()
            body = [l.replace('{R}', 'm.') for l in writer.solid_or_box(shape)]
            notes = writer.notes
            body = ['CadDsl m = cd()'] + body + ['m.display(args ? args[0] : null)']
        else:
            root = nodes[0] if len(nodes) == 1 else Node(os.path.splitext(name)[0], App.Placement(), children=nodes)
            script = Script()
            lines = script.placements(root)
            body = ['CadDslAssembly a = assembly(%s) {' % string(root.label)] + indent(script.order + lines) + \
                   ['}', 'a.display(args ? args[0] : null)']
            notes = script.notes
        if notes:
            header += [' *', ' * Approximations:'] + [' *   - %s' % n for n in notes]
        header += [' *', ' *   cad-dsl <script.groovy>             opens the viewer',
                   ' *   cad-dsl <script.groovy> out.step    exports (.step, .stl, ...)', ' */',
                   'import org.taack.cad.dsl.*', 'import org.taack.cad.dsl.geom.Vec', '',
                   'import static org.taack.cad.dsl.CadDsl.*', '']
        return '\n'.join(header + body) + '\n'
    finally:
        App.closeDocument(doc.Name)


def volume(path):
    """Volume of the solids of a STEP file"""
    return Part.read(path).Volume


def difference(path1, path2):
    """Volume of the symmetric difference of the solids of two STEP files, the sum of those of each solid of the
    first one with its match in the second one (see differences)"""
    return sum(d[2] for d in differences(path1, path2) if d[2] is not None)


def solid_difference(a, b):
    """Volume of the symmetric difference of two solids"""
    # against helical faces, the booleans fail, leaving solids whole or empty, the common empty though both overlap:
    # they are also made with a tolerance, the results kept when the cuts and the common add up to the volumes, the
    # smallest one, as failing booleans miss some of the common
    results = []
    for tolerance in (0.0, 1e-4):
        try:
            common = a.common(b, tolerance).Volume
            a_b, b_a = a.cut(b, tolerance).Volume, b.cut(a, tolerance).Volume
        except Exception:
            continue
        if max(abs(common + a_b - a.Volume), abs(common + b_a - b.Volume)) < 1e-3 * a.Volume:
            results.append(a_b + b_a)
    return min(results) if results else None


def labelled_solids(path):
    """(label, solid) of each solid of a STEP file, placed in it"""
    import Import
    doc = App.newDocument('Solids')
    try:
        Import.insert(path, doc.Name)
        roots = [o for o in doc.Objects if is_node(o) and o.getParentGeoFeatureGroup() is None and
                 o.getParentGroup() is None and not any(is_node(p) and p.TypeId == 'App::Link' for p in o.InList)]
        result = []

        def walk(node, placement):
            pl = placement.multiply(node.placement)
            if node.is_part:
                s = node.shape.copy()
                s.Placement = pl
                result.append((node.label, s))
            for c in node.children:
                walk(c, pl)
        for n in (tree(o) for o in roots):
            if n is not None:
                walk(n, App.Placement())
        return result
    finally:
        App.closeDocument(doc.Name)


def differences(path1, path2):
    """[label, volume, symmetric difference, volume built] of each solid of the first STEP file with the solid of the second one
    closest to it (by center of mass and volume): the solids of an assembly overlap, the difference of the whole
    files would count them twice, and fails on them. The difference is None when the booleans fail"""
    def closest(solids, a):
        return min(solids, key=lambda s: (s.CenterOfMass - a.CenterOfMass).Length +
                   abs(s.Volume - a.Volume) / max(a.Volume, 1e-9))
    # the solids as read, their placement applied: the booleans fail on some placed by the assembly tree
    solids = Part.read(path1).Solids
    built = Part.read(path2).Solids
    result = []
    for label, placed in labelled_solids(path1):
        a = closest(solids, placed)
        b = closest(built, a) if built else None
        result.append([label, a.Volume, solid_difference(a, b) if b else a.Volume, b.Volume if b else 0])
    return result
