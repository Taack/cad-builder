"""
FreeCAD kernel of the CAD DSL: what org.taack.cad.occt.CadDslVisitor does with OCCT shapes, done with the features
of a FreeCAD document, so that the model can be saved as an editable FCStd file.

Primitives are Part::Box, Part::Cylinder, ...; contours drawn on a face are sketches, and the face they bound a
Part::Face; prisms are Part::Extrusion, booleans Part::Cut, Part::MultiFuse, Part::MultiCommon; moved and patterned
copies are Part::Compound. The numbers given to the features are kept, so that those equal to a parameter (see
CadDsl.param) are bound to its cell of the Params spreadsheet when the document is saved.
"""
import importlib.util
import math
import os
import re
import subprocess
import sys

import FreeCAD as App
import Part
import Sketcher

V = App.Vector
V2 = App.Base.Vector2d
G = Part.Geom2d

FACE_MAKER = 'Part::FaceMakerBullseye'
# Distance under which two end points of a sketch are joined by a coincident constraint
COINCIDENCE = 1e-5
# Contours of more edges are not constrained: solving the sketch would take long
MAX_CONSTRAINED = 200
PLATE_C0 = 0

# Models, assemblies and parts known to the JVM, by handle
handles = {}
# 2D constructions shared with the JVM (circles, arcs, ...), by id
constructions = {}
# Parameters of the model, name -> value (see CadDsl.param)
params = {}
_last_handle = [0]


def register(o):
    _last_handle[0] += 1
    handles[_last_handle[0]] = o
    return _last_handle[0]


def dispatch(handle, method, args):
    if handle is not None:
        return getattr(handles[handle], method)(*args)
    if '.' in method:
        module, name = method.rsplit('.', 1)
        return getattr(sys.modules[module], name)(*args)
    return FUNCTIONS[method](*args)


# ---------------------------------------------------------------------------------------------------------------------
# Vectors and frames
# ---------------------------------------------------------------------------------------------------------------------

def vec(a):
    return None if a is None else V(a[0], a[1], a[2])


def pt(a):
    return None if a is None else (float(a[0]), float(a[1]))


def v2(p):
    return V2(p[0], p[1])


def xyz(v):
    return [v.x, v.y, v.z]


def unit(v):
    n = v.Length
    if n == 0:
        raise ValueError('Null direction')
    return V(v.x / n, v.y / n, v.z / n)


def placement(origin, x, y):
    """Placement of the frame of origin and x, y directions (y made perpendicular to x)."""
    x = unit(x)
    z = unit(x.cross(y))
    y = z.cross(x)
    return App.Placement(origin, App.Rotation(x, y, z, 'ZXY'))


def plane_frame(origin, x, y):
    """Plane whose (u, v) parameters run along x and y from origin."""
    plane = Part.Plane()
    plane.transform(placement(origin, x, y).toMatrix())
    return plane


def frame_of(surface):
    """Placement of the (u, v) frame of a planar surface."""
    o = surface.value(0, 0)
    return placement(o, surface.value(1, 0) - o, surface.value(0, 1) - o)


def ax3(origin, normal, x):
    """As gp_Ax3(origin, normal, x): z normal, x projected on the plane normal to it."""
    z = unit(normal)
    x = unit(x - z * x.dot(z))
    return placement(origin, x, z.cross(x))


def to_placement(m):
    """Placement of the 12 values a11, a12, ... a34 of a Trsf."""
    return App.Placement(App.Matrix(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11],
                                    0, 0, 0, 1))


def coplanar(pl, other):
    n1 = pl.Rotation.multVec(V(0, 0, 1))
    n2 = other.Rotation.multVec(V(0, 0, 1))
    return abs(abs(n1.dot(n2)) - 1) < 1e-9 and abs((other.Base - pl.Base).dot(n1)) < 1e-7


def coords_projection(p, direction, p00):
    """Vec.coordsProjection, the 2D position of the center of a face"""
    sx = -1.0 if p.x - p00.x < 0 else 1.0
    d = V(abs(direction.x), abs(direction.y), abs(direction.z))
    t = p - p00
    return (d.z * t.x + d.x * t.y + sx * d.y * t.x, d.z * t.y - d.x * t.z + sx * d.y * t.z)


# ---------------------------------------------------------------------------------------------------------------------
# 2D shapes, as the DSL ones (org.taack.cad.dsl.geom)
# ---------------------------------------------------------------------------------------------------------------------

class Edge2d:
    def __init__(self, to):
        self.to = to


class Arc2d:
    def __init__(self, to, via):
        self.to = to
        self.via = via


class Spline2d:
    def __init__(self, through):
        self.through = through
        self.to = through[-1]


class Circle2d:
    def __init__(self, pos, radius, reverse):
        self.pos = pos
        self.radius = radius
        self.reverse = reverse


class Ellipse2d:
    def __init__(self, pos, direction, major, minor):
        self.pos = pos
        self.direction = direction
        self.major = major
        self.minor = minor


class Trimmable:
    """Arc of a closed curve; its curve is made once, its end points being asked while drawing."""
    _geom = None

    def geom(self):
        if self._geom is None:
            self._geom = self.make_geom()
        return self._geom

    @property
    def start(self):
        g = self.geom()
        p = g.value(g.FirstParameter)
        return (p.x, p.y)

    @property
    def to(self):
        g = self.geom()
        p = g.value(g.LastParameter)
        return (p.x, p.y)


class ArcOfCircle2d(Trimmable):
    def __init__(self, circle, angle1, angle2, pt_from, pt_to, reversed_):
        self.circle = circle
        self.angle1 = angle1
        self.angle2 = angle2
        self.pt_from = pt_from
        self.pt_to = pt_to
        self.reversed = reversed_

    def make_geom(self):
        # the circle of an arc is never reversed, as gp_Circ2d
        c = G.Circle2d(v2(self.circle.pos), self.circle.radius)
        if self.angle1 != self.angle2:
            arc = G.ArcOfCircle2d(c, self.angle1, self.angle2)
        elif self.pt_to is None:
            arc = G.ArcOfCircle2d(c, c.parameter(v2(self.pt_from)), self.angle1)
        else:
            arc = G.ArcOfCircle2d(c, c.parameter(v2(self.pt_from)), c.parameter(v2(self.pt_to)))
        if self.reversed:
            arc.reverse()
        return arc


class TrimmedCurve2d(Trimmable):
    def __init__(self, curve, u1, u2, reversed_):
        self.curve = curve
        self.u1 = u1
        self.u2 = u2
        self.reversed = reversed_

    def make_geom(self):
        c = self.curve
        u1, u2 = adjust_periodic(self.u1, self.u2)
        if isinstance(c, Circle2d):
            circle = G.Circle2d(v2(c.pos), c.radius)
            if c.reverse:
                # a reversed circle runs clockwise: its parameter u is the angle -u of the circle
                arc = G.ArcOfCircle2d(circle, -u2, -u1)
                arc.reverse()
            else:
                arc = G.ArcOfCircle2d(circle, u1, u2)
        else:
            arc = G.ArcOfEllipse2d(closed_geom(c), u1, u2)
        if self.reversed:
            arc.reverse()
        return arc


class Mirrored2d(Trimmable):
    def __init__(self, curve, pos, direction):
        self.curve = curve
        self.pos = pos
        self.direction = direction

    def make_geom(self):
        g = self.curve.geom().copy()
        g.mirror(v2(self.pos), v2(self.direction))
        g.reverse()
        return g


def adjust_periodic(u1, u2):
    """As ElCLib::AdjustPeriodic on [0, 2 PI]: u1 in [0, 2 PI[, u2 in ]u1, u1 + 2 PI]"""
    period = 2 * math.pi
    u1 -= math.floor(u1 / period) * period
    u2 -= math.floor((u2 - u1) / period) * period
    if u2 - u1 < 1e-12:
        u2 += period
    return u1, u2


def construction(d):
    """The 2D construction sent by the JVM: the first time its description and id, then {"ref": id}"""
    if d is None:
        return None
    if 'ref' in d:
        return constructions[d['ref']]
    t = d['t']
    if t == 'Circle2d':
        c = Circle2d(pt(d['pos']), d['radius'], d['reverse'])
    elif t == 'Ellipse2d':
        c = Ellipse2d(pt(d['pos']), pt(d['dir']), d['major'], d['minor'])
    elif t == 'ArcOfCircle2d':
        c = ArcOfCircle2d(construction(d['circle']), d['angle1'], d['angle2'], pt(d.get('from')), pt(d.get('to')),
                          d['reversed'])
    elif t == 'TrimmedCurve2d':
        c = TrimmedCurve2d(construction(d['curve']), d['u1'], d['u2'], d['reversed'])
    elif t == 'Mirrored2d':
        c = Mirrored2d(construction(d['curve']), pt(d['pos']), pt(d['dir']))
    elif t == 'Spline2d':
        c = Spline2d([pt(p) for p in d['through']])
    elif t == 'Edge2d':
        c = Edge2d(pt(d['to']))
    elif t == 'Arc2d':
        c = Arc2d(pt(d['to']), pt(d['via']))
    else:
        raise ValueError('Unsupported 2D shape: %s' % t)
    if 'id' in d:
        constructions[d['id']] = c
    return c


def closed_geom(s):
    """The 2D curve of a closed shape"""
    if isinstance(s, Circle2d):
        c = G.Circle2d(v2(s.pos), s.radius)
        if s.reverse:
            c.reverse()
        return c
    if isinstance(s, Ellipse2d):
        x, y = s.direction
        n = math.hypot(x, y)
        x, y = x / n, y / n
        cx, cy = s.pos
        return G.Ellipse2d(V2(cx + x * s.major, cy + y * s.major), V2(cx - y * s.minor, cy + x * s.minor), v2(s.pos))
    raise ValueError('Unsupported 2D closed shape: %s' % s)


def open_geom(s, start):
    """The 2D curve of an open shape drawn from start"""
    if isinstance(s, Edge2d):
        return G.Line2dSegment(v2(start), v2(s.to))
    if isinstance(s, Arc2d):
        return G.ArcOfCircle2d(v2(start), v2(s.via), v2(s.to))
    if isinstance(s, Spline2d):
        spline = G.BSplineCurve2d()
        spline.interpolate([v2(start)] + [v2(p) for p in s.through])
        return spline
    if isinstance(s, Trimmable):
        return s.geom()
    raise ValueError('Unsupported 2D shape: %s' % s)


# ---------------------------------------------------------------------------------------------------------------------
# Sketches
# ---------------------------------------------------------------------------------------------------------------------

def flat(v):
    return V(v.x, v.y, 0)


def sketch_geometry(edge):
    """The sketch geometry of an edge in the sketch coordinates, None for an edge too short"""
    if edge.Length < 1e-9:
        return None
    c = edge.Curve
    f, l = edge.ParameterRange
    if isinstance(c, Part.Line):
        return Part.LineSegment(flat(edge.valueAt(f)), flat(edge.valueAt(l)))
    if isinstance(c, Part.Circle):
        if edge.isClosed():
            return Part.Circle(flat(c.Center), V(0, 0, 1), c.Radius)
        a, m, b = flat(edge.valueAt(f)), flat(edge.valueAt((f + l) / 2)), flat(edge.valueAt(l))
        arc = Part.ArcOfCircle(a, m, b)
        # the arcs of a sketch turn counterclockwise
        return Part.ArcOfCircle(b, m, a) if arc.Axis.z < 0 else arc
    if isinstance(c, Part.Ellipse) and edge.isClosed():
        center = flat(c.Center)
        x = unit(flat(c.XAxis))
        return Part.Ellipse(center + x * c.MajorRadius, center + V(-x.y, x.x, 0) * c.MinorRadius, center)
    spline = c.toBSpline(f, l)
    poles = spline.getPoles()
    for i, p in enumerate(poles):
        spline.setPole(i + 1, flat(p))
    return spline


def add_edges(model, sketch, edges, chain):
    """Add edges, in the sketch coordinates, to a sketch: their radius constrained, and their ends joined by
    coincident constraints when they follow each other (chain True), or no constraints (chain None)"""
    geometries = [g for g in (sketch_geometry(e) for e in edges) if g is not None]
    if not geometries:
        return []
    # all at once: the sketch is solved at each call
    ids = sketch.addGeometry(geometries, False)
    if chain is None or len(geometries) > MAX_CONSTRAINED:
        return ids
    constraints = [Sketcher.Constraint('Radius', i, g.Radius) for i, g in zip(ids, geometries)
                   if isinstance(g, (Part.Circle, Part.ArcOfCircle))]
    radii = len(constraints)
    if chain:
        pairs = list(zip(range(len(ids)), range(1, len(ids))))
        if len(ids) > 2:
            pairs.append((len(ids) - 1, 0))
        for a, b in pairs:
            c = coincident(ids[a], geometries[a], ids[b], geometries[b])
            if c is not None:
                constraints.append(c)
    if constraints:
        first = len(sketch.Constraints)
        sketch.addConstraint(constraints)
        for k in range(radii):
            model.bindings.append((sketch, 'Constraints[%d]' % (first + k), constraints[k].Value))
    return ids


def coincident(a, ga, b, gb):
    """The constraint joining the closest ends of two geometries, None if they do not touch"""
    if isinstance(ga, (Part.Circle, Part.Ellipse)) or isinstance(gb, (Part.Circle, Part.Ellipse)):
        return None
    best = None
    for pa, va in ((1, ga.StartPoint), (2, ga.EndPoint)):
        for pb, vb in ((1, gb.StartPoint), (2, gb.EndPoint)):
            d = (va - vb).Length
            if best is None or d < best[0]:
                best = (d, pa, pb)
    if best[0] < COINCIDENCE:
        return Sketcher.Constraint('Coincident', a, best[1], b, best[2])
    return None


def to_local(edges, pl):
    m = pl.inverse().toMatrix()
    local = []
    for e in edges:
        e = e.copy()
        e.transformShape(m)
        local.append(e)
    return local


def make_wire(edges):
    try:
        return Part.Wire(edges)
    except Part.OCCError:
        return Part.Wire(Part.__sortEdges__(edges))


def font_file(font, bold, italic):
    """The file of a font: font itself when it is a path, else the file fontconfig finds for the font name"""
    if '/' in font or '\\' in font:
        return font
    pattern = font + (':bold' if bold else '') + (':italic' if italic else '')
    try:
        found = subprocess.run(['fc-match', '-f', '%{family}|%{file}', pattern], capture_output=True,
                               text=True).stdout.strip()
    except OSError:
        raise ValueError('font not found: %s (fc-match finds the font names, else give a font file)' % font)
    families, _, file_name = found.partition('|')
    # fc-match falls back to a default font for unknown names, the aliases excepted
    def key(name):
        return name.replace(' ', '').lower()
    if not file_name or (font not in ('sans-serif', 'serif', 'monospace') and
                         key(font) not in [key(f) for f in families.split(',')]):
        raise ValueError('font not found: %s' % font)
    return file_name


# ---------------------------------------------------------------------------------------------------------------------
# Models
# ---------------------------------------------------------------------------------------------------------------------

class FaceRef:
    """The face sketches are drawn on, and that fillet() and hollowedSolid() use: a planar face of a feature, the
    default plane (normal to the direction, through the origin), or a feature whose shape is the face (toFace, plate)"""

    def __init__(self, shape=None, owner=None, surface=None, feature=None):
        self.shape = shape
        self.owner = owner
        self._surface = surface
        self.feature = feature

    def face_shape(self):
        if self.shape is not None:
            return self.shape
        if self.feature is not None:
            self.feature.Document.recompute()
            return self.feature.Shape.Faces[0]
        return None

    def surface(self):
        if self._surface is not None:
            return self._surface
        return self.face_shape().Surface

    def normal(self, u, v):
        f = self.face_shape()
        return f.normalAt(u, v) if f is not None else self._surface.normal(u, v)

    def sub_name(self):
        """FaceN of the face in its owner, None if it is not the face of a feature"""
        if self.owner is None:
            return None
        for i, f in enumerate(self.owner.Shape.Faces):
            if f.isSame(self.shape):
                return 'Face%d' % (i + 1)
        return None


class Model:
    """A model being built, the FreeCAD counterpart of a CadDslVisitor"""

    def __init__(self):
        self.doc = App.newDocument('Model')
        self.shape = None
        self.shape_cut_base = None
        self.face = None
        # Features of the contours drawn since the last prism (makeWires), and the sketch drawn on
        self.profile = []
        self.sketch = None
        # Texts and SVG drawings of the current sketch (drawnShapes)
        self.drawn = []
        self.current_surface = None
        self.section_surface = None
        self.section_wires = []
        self.from_stack = []
        self.from2d = (0.0, 0.0)
        self.direction = V(0, 0, 1)
        self.pt00 = None
        self.open2d = []
        self.open3d = []
        self.closed2d = []
        self.bool_shapes = [[]]
        self.pattern_starts = []
        # (feature, property path, number) of the numbers given to the features, see bind_params
        self.bindings = []
        self.helix = None
        self.wing_sections = []
        self.plate = None
        self.saved = None

    @property
    def from_vec(self):
        return self.from_stack[-1] if self.from_stack else V(0, 0, 0)

    # features

    def add(self, type_name, name):
        return self.doc.addObject(type_name, name)

    def num(self, obj, prop, value):
        setattr(obj, prop, value)
        self.bindings.append((obj, prop, value))

    def place(self, obj, pl):
        obj.Placement = pl
        for c in ('x', 'y', 'z'):
            self.bindings.append((obj, 'Placement.Base.' + c, getattr(pl.Base, c)))

    def ax2(self):
        """Placement of gp_Ax2(fromVec, direction): its x direction is that of a plane of normal direction"""
        frame = frame_of(Part.Plane(V(0, 0, 0), self.direction))
        return App.Placement(self.from_vec, frame.Rotation)

    def primitive(self, obj):
        self.bool_shapes[-1].append(obj)
        if self.shape is None:
            self.shape = obj
        return obj

    def boolean(self, type_name, shapes):
        unique = []
        for s in shapes:
            if s is not None and s not in unique:
                unique.append(s)
        if len(unique) == 1:
            return unique[0]
        o = self.add(type_name, type_name.split('::Multi')[-1])
        o.Shapes = unique
        o.Refine = False
        return o

    def cut(self, base, tool):
        o = self.add('Part::Cut', 'Cut')
        o.Base = base
        o.Tool = tool
        o.Refine = False
        return o

    def moved_copy(self, obj, pl):
        """Copy of obj moved by pl (a compound: Part::Mirroring ignores the placement of a link)"""
        o = self.add('Part::Compound', obj.Name + 'Moved')
        o.Links = [obj]
        o.Placement = pl
        return o

    def static(self, shape, name):
        o = self.add('Part::Feature', name)
        o.Shape = shape
        return o

    def recompute(self):
        self.doc.recompute()

    def current_shape(self):
        self.recompute()
        return self.shape.Shape

    # contours

    def sketch_for(self, pl, drawn=False):
        """Sketch on the plane of pl: the one being drawn when it is on this plane, else a new one"""
        if not drawn and self.sketch is not None and coplanar(self.sketch.Placement, pl):
            return self.sketch
        s = self.add('Sketcher::SketchObject', 'Sketch')
        self.place(s, pl)
        if drawn:
            self.drawn.append(s)
        else:
            self.sketch = s
            self.profile.append(s)
        return s

    def add_contour(self, edges, frame, chain=True, drawn=False):
        """Add a contour of global edges: to a sketch when frame (its plane) is given, else as a wire feature"""
        if frame is None:
            o = self.static(make_wire(edges), 'Wire')
            (self.drawn if drawn else self.profile).append(o)
            return o
        s = self.sketch_for(frame, drawn)
        add_edges(self, s, to_local(edges, s.Placement), chain)
        return s

    def surface_contour(self, curves, surface, chain=True):
        edges = [c.toShape(surface) for c in curves]
        if isinstance(surface, Part.Plane):
            return self.add_contour(edges, frame_of(surface), chain)
        return self.add_contour(edges, None)

    def wire2d_curves(self):
        start = self.from2d or (0.0, 0.0)
        curves = []
        for s in self.open2d:
            if not (isinstance(s, Edge2d) and s.to == start):
                curves.append(open_geom(s, start))
            start = s.to
        self.open2d = []
        return curves

    def edges3d(self):
        start = self.from_vec
        edges = []
        for kind, to, extra in self.open3d:
            if kind == 'edge':
                if (to - start).Length > 0:
                    edges.append(Part.LineSegment(start, to).toShape())
            elif kind == 'arc':
                edges.append(Part.ArcOfCircle(start, extra, to).toShape())
            else:
                edges.append(extra)
            start = to
        self.open3d = []
        return edges

    def wire3d(self):
        edges = self.edges3d()
        plane = make_wire(edges).findPlane()
        self.add_contour(edges, frame_of(plane) if plane is not None else None)

    def default_face(self):
        if self.face is None:
            self.face = FaceRef(surface=Part.Plane(V(0, 0, 0), self.direction))

    # ICadDslVisitor

    def visitFrom3d(self, pos):
        self.from_stack.append(vec(pos))

    def visitFromEnd3d(self, pos):
        self.default_face()
        self.current_surface = self.face.surface()
        if self.open3d:
            self.wire3d()
        if self.from_stack:
            self.from_stack.pop()

    def visitFrom2d(self, pos):
        if pos is not None:
            self.from2d = pt(pos)

    def visitFromEnd2d(self, pos):
        self.default_face()
        self.current_surface = self.face.surface()
        if self.open2d:
            self.surface_contour(self.wire2d_curves(), self.current_surface)
        for s in self.closed2d:
            self.surface_contour([closed_geom(s)], self.current_surface, chain=False)
        self.closed2d = []

    def visitBox(self, length, height, thickness):
        o = self.add('Part::Box', 'Box')
        self.num(o, 'Length', length)
        self.num(o, 'Width', height)
        self.num(o, 'Height', thickness)
        self.place(o, self.ax2())
        self.primitive(o)

    def visitSphere(self, radius, radian1, radian2):
        o = self.add('Part::Sphere', 'Sphere')
        self.num(o, 'Radius', radius)
        o.Angle1 = math.degrees(radian1)
        o.Angle2 = math.degrees(radian2)
        self.place(o, self.ax2())
        self.primitive(o)

    def visitCylinder(self, radius, height):
        o = self.add('Part::Cylinder', 'Cylinder')
        self.num(o, 'Radius', radius)
        self.num(o, 'Height', height)
        self.place(o, self.ax2())
        self.primitive(o)

    def visitCone(self, r1, r2, height):
        o = self.add('Part::Cone', 'Cone')
        self.num(o, 'Radius1', r1)
        self.num(o, 'Radius2', r2)
        self.num(o, 'Height', height)
        self.place(o, self.ax2())
        self.primitive(o)

    def visitTorus(self, torus_radius, ring_radius):
        o = self.add('Part::Torus', 'Torus')
        self.num(o, 'Radius1', torus_radius)
        self.num(o, 'Radius2', ring_radius)
        self.place(o, self.ax2())
        self.primitive(o)

    def visitCut(self):
        if self.shape in self.bool_shapes[-1]:
            self.bool_shapes[-1].remove(self.shape)
        self.bool_shapes.append([])
        if self.shape is not None:
            self.shape_cut_base = self.shape

    def visitCutEnd(self):
        if not self.bool_shapes[-1]:
            return
        result = self.shape_cut_base
        for other in self.bool_shapes[-1]:
            result = self.cut(result, other)
        self.shape = result
        self.bool_shapes.pop()
        # the result is the last solid, as after a fuse or a prism
        self.bool_shapes[-1].append(result)

    def visitFuse(self):
        self.bool_shapes.append([])
        if self.shape is not None:
            self.bool_shapes[-1].append(self.shape)

    def visitFuseEnd(self):
        if not self.bool_shapes[-1]:
            return
        self.shape = self.boolean('Part::MultiFuse', [self.shape] + self.bool_shapes[-1])
        self.pop_shape()

    def visitCommon(self):
        self.visitFuse()

    def visitCommonEnd(self):
        if not self.bool_shapes[-1]:
            return
        self.shape = self.boolean('Part::MultiCommon', [self.shape] + self.bool_shapes[-1])
        self.pop_shape()

    def pop_shape(self):
        self.bool_shapes.pop()
        if self.bool_shapes[-1]:
            self.bool_shapes[-1].pop(0)
        self.bool_shapes[-1].insert(0, self.shape)

    def visitFillet(self, radius):
        shape = self.current_shape()
        face = self.face.face_shape() if self.face is not None else None
        edges = (face if face is not None else shape).Edges
        o = self.add('Part::Fillet', 'Fillet')
        o.Base = self.shape
        o.Edges = [(i + 1, radius, radius) for i, e in enumerate(shape.Edges) if any(e.isSame(f) for f in edges)]
        self.shape = o

    def visitHollowedSolid(self, thickness):
        self.current_shape()
        sub = self.face.sub_name() if self.face is not None and self.face.owner is self.shape else None
        if sub is None:
            faces = [self.face.face_shape()] if self.face is not None and self.face.face_shape() is not None else []
            self.shape = self.static(self.shape.Shape.makeThickness(faces, thickness, 0.001), 'Thickness')
            return
        o = self.add('Part::Thickness', 'Thickness')
        o.Faces = (self.shape, [sub])
        self.num(o, 'Value', thickness)
        o.Mode = 'Skin'
        o.Join = 'Arc'
        self.shape = o

    def visitEllipse2d(self, direction, major, minor, rid):
        e = Ellipse2d(self.from2d, pt(direction), major, minor)
        constructions[rid] = e
        self.closed2d.append(e)
        return list(e.pos)

    def visitCircle2d(self, radius, reverse, rid):
        c = Circle2d(self.from2d, radius, reverse)
        constructions[rid] = c
        self.closed2d.append(c)
        return list(c.pos)

    def visitWireFromSurface(self):
        pass

    def visitWireFromSurfaceEnd(self):
        self.section_wires.append(self.surface_contour(self.wire2d_curves(), self.section_surface))
        if self.section_wires[-1] in self.profile:
            self.profile.remove(self.section_wires[-1])
        if self.sketch is self.section_wires[-1]:
            self.sketch = None
        # Only open shapes make the wire; closed ones were constructions for them
        self.closed2d = []

    def visitTrimmed(self, trimmed):
        self.open2d.append(construction(trimmed))

    def visitMirror(self, mirrored):
        self.open2d.append(construction(mirrored))

    def visitCylindricalSurface(self, radius):
        self.section_surface = Part.Cylinder(Part.Circle(self.from_vec, self.direction, radius))

    def visitClosedWire(self):
        self.visitFrom2d(list(self.from2d))

    def visitClosedWireEnd(self):
        self.visitFromEnd2d(list(self.from2d))

    def visitRemoveFromConstruction(self, to_remove):
        for c in [construction(d) for d in to_remove]:
            self.closed2d = [s for s in self.closed2d if s is not c]
            self.open2d = [s for s in self.open2d if s is not c]

    def visitAddToConstruction(self, to_add):
        for c in [construction(d) for d in to_add]:
            if not isinstance(c, (Circle2d, Ellipse2d)):
                self.open2d.append(c)

    def visitAdapt3d(self, trimmed, dir_x, dir_y):
        # as GeomAPI::To3d on the plane gp_Ax3(origin, dirX, dirY): normal dirX, x direction dirY
        frame = ax3(V(0, 0, 0), vec(dir_x), vec(dir_y))
        edge = construction(trimmed).geom().toShape(plane_frame(V(0, 0, 0), frame.Rotation.multVec(V(1, 0, 0)),
                                                                frame.Rotation.multVec(V(0, 1, 0))))
        self.open3d.append(('adapter', edge.valueAt(edge.LastParameter), edge))

    def last_shape(self):
        return self.bool_shapes[-1][-1] if self.bool_shapes[-1] else self.shape

    def visitSolidMirror(self, pos, direction):
        s = self.last_shape()
        if s is not None:
            o = self.add('Part::Mirroring', 'Mirror')
            o.Source = s
            o.Base = vec(pos)
            o.Normal = vec(direction)
            self.primitive(o)

    def moved(self, pl, clone):
        s = self.last_shape()
        if s is None:
            return
        o = self.moved_copy(s, pl)
        if not self.bool_shapes[-1]:
            self.shape = o
        else:
            if not clone:
                self.bool_shapes[-1].pop()
            self.bool_shapes[-1].append(o)

    def visitSolidTranslate(self, distance):
        self.moved(App.Placement(vec(distance), App.Rotation()), False)

    def visitSolidRotate(self, pos, direction, angle, clone):
        self.moved(App.Placement(V(0, 0, 0), App.Rotation(vec(direction), math.degrees(angle)), vec(pos)), clone)

    def visitHelix(self, radius, pitch, height, end_radius):
        axis = unit(self.direction)
        radial = unit(V(0, 1, 0).cross(axis) if abs(axis.y) < 0.9 else V(0, 0, 1).cross(axis))
        o = self.add('Part::Helix', 'Helix')
        self.num(o, 'Pitch', abs(pitch))
        self.num(o, 'Height', height)
        self.num(o, 'Radius', radius)
        o.Angle = math.degrees(math.atan2(end_radius - radius, height))
        o.LocalCoord = 1 if pitch < 0 else 0
        self.place(o, placement(self.from_vec, radial, axis.cross(radial)))
        self.helix = (o, self.from_vec + radial * radius, radial, axis)

    def begin_profile(self, origin, dir_x, dir_y, start):
        """Start drawing a profile on the plane of origin "origin", whose 2D x and y go along dir_x and dir_y"""
        self.saved = (self.face, self.current_surface, self.profile, self.sketch, self.from2d)
        self.profile = []
        self.sketch = None
        self.face = FaceRef(surface=plane_frame(origin, dir_x, dir_y))
        self.visitFrom2d(start)

    def end_profile(self, what, start):
        """End the profile started by begin_profile, restore the drawing state and return the profile feature"""
        self.visitFromEnd2d(start)
        if not self.profile:
            raise ValueError('%s: nothing drawn' % what)
        profile = self.profile[0]
        self.face, self.current_surface, self.profile, self.sketch, self.from2d = self.saved
        return profile

    def visitHelixProfile(self, start):
        helix, origin, radial, axis = self.helix
        # Axial section plane through the start of the helix: x radial, y along the axis
        self.begin_profile(origin, radial, axis, start)

    def visitHelixProfileEnd(self, start):
        profile = self.end_profile('helix profile', start)
        o = self.add('Part::Sweep', 'Sweep')
        o.Sections = [profile]
        o.Spine = (self.helix[0], [])
        o.Solid = True
        o.Frenet = True
        self.primitive(o)

    def visitSpline(self, through):
        self.open2d.append(Spline2d([pt(p) for p in through]))

    def visitWing(self):
        self.wing_sections = []

    def visitWingSection(self, leading_edge, chord, twist, start):
        self.wing_section = (vec(leading_edge), chord, twist)
        # Unit chord profile on the XZ plane, scaled and placed afterward
        self.begin_profile(V(0, 0, 0), V(1, 0, 0), V(0, 0, 1), start)

    def visitWingSectionEnd(self, start):
        leading_edge, chord, twist = self.wing_section
        profile = self.end_profile('wing section', start)
        # scale to the chord, twist around the quarter chord (nose up), then move to the leading edge
        local = []
        for g in profile.Geometry:
            e = g.toShape()
            e.scale(chord, V(0, 0, 0))
            local.append(e)
        profile.deleteAllGeometry()
        self.bindings = [b for b in self.bindings if b[0] is not profile]
        add_edges(self, profile, local, True)
        profile.Placement = App.Placement(self.from_vec + leading_edge, App.Rotation()).multiply(
            App.Placement(V(0, 0, 0), App.Rotation(V(0, 1, 0), twist), V(chord / 4, 0, 0))).multiply(
            profile.Placement)
        self.wing_sections.append(profile)

    def visitWingEnd(self):
        if len(self.wing_sections) < 2:
            raise ValueError('wing: at least 2 sections expected, got %d' % len(self.wing_sections))
        o = self.add('Part::Loft', 'Wing')
        o.Sections = self.wing_sections
        o.MaxDegree = 8
        o.Solid = True
        o.Ruled = False
        self.wing_sections = []
        self.primitive(o)

    def visitPlate(self, options):
        self.plate = {'options': options, 'origin': self.from_vec, 'boundary': None, 'curves': [], 'points': []}

    def visitPlateCurve(self, start, boundary):
        if boundary and self.plate['boundary'] is not None:
            raise ValueError('plate: a single boundary expected')
        self.plate['drawing_boundary'] = boundary
        self.from_stack.append(vec(start) + self.plate['origin'])

    def visitPlateCurveEnd(self, start):
        if not self.open3d:
            raise ValueError('plate curve from %s: nothing drawn' % start)
        wire = self.static(make_wire(self.edges3d()), 'PlateCurve')
        self.from_stack.pop()
        if self.plate['drawing_boundary']:
            self.plate['boundary'] = wire
        else:
            self.plate['curves'].append(wire)

    def visitPlateThrough(self, point):
        p = vec(point) + self.plate['origin']
        o = self.add('Part::Vertex', 'PlatePoint')
        o.X, o.Y, o.Z = p.x, p.y, p.z
        self.plate['points'].append(o)

    def visitPlateEnd(self):
        plate = self.plate
        if plate['boundary'] is None:
            raise ValueError('plate: boundary expected')
        options = plate['options']

        def edges(o):
            return (o, tuple('Edge%d' % (i + 1) for i in range(len(o.Shape.Edges))))

        o = self.add('Surface::Filling', 'Plate')
        o.BoundaryEdges = [edges(plate['boundary'])]
        o.BoundaryOrder = [PLATE_C0] * len(plate['boundary'].Shape.Edges)
        if plate['curves']:
            o.UnboundEdges = [edges(c) for c in plate['curves']]
            o.UnboundOrder = [PLATE_C0] * sum(len(c.Shape.Edges) for c in plate['curves'])
        if plate['points']:
            o.Points = [(p, ('Vertex1',)) for p in plate['points']]
        o.Degree = options['degree']
        o.PointsOnCurve = options['pointsOnCurve']
        o.Iterations = options['iterations']
        o.Tolerance3d = options['tolerance']
        o.MaximumSegments = options['maxSegments']
        o.MaximumDegree = options['maxDegree']
        self.face = FaceRef(feature=o)
        self.profile = []
        self.sketch = None
        self.plate = None

    def visitCircularPattern(self):
        self.pattern_starts.append(len(self.bool_shapes[-1]))

    def visitCircularPatternEnd(self, count, axis):
        shapes = self.bool_shapes[-1]
        pattern = shapes[self.pattern_starts.pop():]
        for i in range(1, count):
            rotation = App.Placement(V(0, 0, 0), App.Rotation(vec(axis), 360.0 * i / count), self.from_vec)
            for s in pattern:
                shapes.append(self.moved_copy(s, rotation))

    def visitThruSection(self):
        self.section_wires = []

    def visitThruSectionEnd(self):
        if not self.section_wires:
            return
        o = self.add('Part::Loft', 'ThruSection')
        o.Sections = self.section_wires
        o.MaxDegree = 8
        o.Solid = True
        o.Ruled = False
        self.section_wires = []
        self.primitive(o)

    def visitFace(self, direction, position):
        direction, position = vec(direction), vec(position)
        best = -math.inf if position is None else math.inf
        self.direction = direction
        shape = self.current_shape()
        for f in shape.Faces:
            surface = f.Surface
            if not isinstance(surface, Part.Plane):
                continue
            if position is None:
                origin = f.CenterOfMass
                z = origin.dot(direction)
            else:
                segment = Part.LineSegment(position, position + direction * 0.2).toShape()
                z, points, info = segment.distToShape(f)
                origin = points[0][1]
            if (position is None and z > best) or (position is not None and z < best):
                best = z
                self.pt00 = surface.value(0, 0)
                self.face = FaceRef(shape=f, owner=self.shape)
                if self.from_stack:
                    self.from_stack.pop()
                self.from_stack.append(origin)

    def visitCenter(self):
        self.from_stack.append(self.face.face_shape().CenterOfMass)
        self.from2d = coords_projection(self.from_vec, self.direction, self.pt00 or V(0, 0, 0))

    def visitCenterEnd(self):
        self.from_stack.pop()

    def pen_on_face(self, u, v, angle):
        """Frame at (u, v) on the face: x along u turned by angle, z out of the face, so that it reads from outside"""
        surface = self.face.surface()
        p = surface.value(u, v)
        normal = unit(self.face.normal(u, v))
        du = surface.value(u + 1, v) - p
        return ax3(p, normal, App.Rotation(normal, math.degrees(angle)).multVec(du))

    def pen_3d(self, options):
        pos = self.open3d[-1][1] if self.open3d else self.from_vec
        return ax3(pos, vec(options['normal']), vec(options['direction']))

    def text(self, content, size, options, pen):
        import Draft
        o = Draft.make_shapestring(content, font_file(options['font'], options['bold'], options['italic']), size)
        o.MakeFace = False
        o.Justification = '%s-%s' % ({'BOTTOM': 'Bottom', 'CENTER': 'Middle', 'TOP': 'Top'}[options['valign']],
                                     options['align'].capitalize())
        o.Placement = pen
        self.bindings.append((o, 'Size', size))
        self.drawn.append(o)

    def visitText(self, content, size, options):
        self.default_face()
        u, v = self.from2d or (0.0, 0.0)
        self.text(content, size, options, self.pen_on_face(u, v, math.radians(options['angle'])))

    def visitText3d(self, content, size, options):
        self.text(content, size, options, self.pen_3d(options))

    def svg(self, contours, pen):
        s = self.add('Sketcher::SketchObject', 'Drawing')
        s.Placement = pen
        for c in contours:
            start = V(c['start'][0], c['start'][1], 0)
            edges = []
            for seg in c['segments']:
                to = V(seg[-2], seg[-1], 0)
                if seg[0] == 'L':
                    if (to - start).Length > 0:
                        edges.append(Part.LineSegment(start, to).toShape())
                elif seg[0] == 'A':
                    edges.append(Part.ArcOfCircle(start, V(seg[1], seg[2], 0), to).toShape())
                else:
                    bezier = Part.BezierCurve()
                    bezier.setPoles([start, V(seg[1], seg[2], 0), V(seg[3], seg[4], 0), to])
                    edges.append(bezier.toShape())
                start = to
            add_edges(self, s, edges, None)
        self.drawn.append(s)

    def visitSvg(self, contours, options):
        self.default_face()
        u, v = self.from2d or (0.0, 0.0)
        self.svg(contours, self.pen_on_face(u, v, math.radians(options['angle'])))

    def visitSvg3d(self, contours, options):
        self.svg(contours, self.pen_3d(options))

    def visitMove2d(self, to):
        self.from2d = (self.from2d[0] + to[0], self.from2d[1] + to[1])

    def visitMove3d(self, to):
        pass

    def visitTo2d(self, to):
        self.from2d = pt(to)

    def visitTo3d(self, to):
        if self.from_stack:
            self.from_stack.pop()
        self.from_stack.append(vec(to))

    def visitEdge2d(self, to):
        self.open2d.append(Edge2d(pt(to)))

    def visitEdge3d(self, to):
        self.open3d.append(('edge', vec(to), None))

    def visitArc2d(self, to, via):
        self.open2d.append(Arc2d(pt(to), pt(via)))

    def visitArc3d(self, to, via):
        self.open3d.append(('arc', vec(to), vec(via)))

    def visitToFace(self):
        self.contours_to_face()

    def visitToFaceFrom2d(self):
        self.contours_to_face()

    def contours_to_face(self):
        """The face bounded by the contours, texts and SVG drawings of the sketch, a face per outer contour"""
        sources = self.profile + self.drawn
        self.drawn = []
        if not sources:
            return
        o = self.add('Part::Face', 'Face')
        o.Sources = sources
        o.FaceMakerClass = FACE_MAKER
        self.face = FaceRef(feature=o)

    def face_feature(self):
        """The feature whose shape is the face, a copy of it for the face of a solid"""
        if self.face is None:
            raise ValueError('No face: draw a contour and call toFace() first')
        if self.face.feature is None:
            if self.face.shape is None:
                raise ValueError('No face: draw a contour and call toFace() first')
            self.face.feature = self.static(self.face.shape.copy(), 'Face')
        return self.face.feature

    def visitRevolution(self, start, direction, angle):
        o = self.add('Part::Revolution', 'Revolution')
        o.Source = self.face_feature()
        o.Base = vec(start)
        o.Axis = vec(direction)
        self.num(o, 'Angle', 360.0 if angle == 0 else math.degrees(angle))
        o.Solid = True
        o.FaceMakerClass = FACE_MAKER
        obj = o
        if self.from_vec.Length > 0:
            obj = self.moved_copy(o, App.Placement(self.from_vec, App.Rotation()))
        self.primitive(obj)
        # the face and its wires are consumed, as by a prism
        self.face = None
        self.profile = []
        self.sketch = None

    def visitPrism(self, direction, cut):
        self.prism(vec(direction), cut)

    def visitPrismHeight(self, height, cut):
        self.prism(self.direction * height, cut)

    def prism(self, direction, cut):
        o = self.add('Part::Extrusion', 'Hole' if cut else 'Prism')
        o.Base = self.face_feature()
        o.DirMode = 'Custom'
        o.Dir = unit(direction)
        self.num(o, 'LengthFwd', direction.Length)
        o.Solid = True
        o.FaceMakerClass = FACE_MAKER
        shape = o
        if self.shape is not None:
            shape = self.cut(self.shape, o) if cut else self.boolean('Part::MultiFuse', [self.shape, o])
            # the result replaces the solid it is made from, which stays the last solid
            if self.shape in self.bool_shapes[-1]:
                self.bool_shapes[-1].remove(self.shape)
            self.bool_shapes[-1].append(shape)
        self.face = None
        self.profile = []
        self.sketch = None
        self.shape = shape

    def visitDirection(self, axis, normal):
        self.direction = vec(axis)

    # results

    def result_shape(self):
        """The feature built so far (the last solid, or the face), None if none"""
        s = self.bool_shapes[-1][-1] if self.bool_shapes and self.bool_shapes[-1] else self.shape
        if s is None and self.face is not None:
            s = self.face.feature
        return s

    def check(self, result):
        if result is None:
            raise ValueError('Nothing built')
        self.recompute()
        check_document(self.doc)
        if result.Shape.isNull():
            raise ValueError('%s: empty shape' % result.Label)

    def display(self, file_name):
        if file_name is None:
            self.check(self.result_shape())
            return
        result = self.shape or (self.face.feature if self.face is not None else None)
        self.check(result)
        export(self.doc, result, result.Shape, file_name, self.bindings)

    def save(self, file_name):
        """Save the document, e.g. to show it in FreeCAD"""
        result = self.result_shape()
        self.check(result)
        save_document(self.doc, [result], file_name, self.bindings)


def check_document(doc):
    """Raise the features FreeCAD failed to build, with their inputs; the document is saved to look at them"""
    failed = [o for o in doc.Objects if 'Invalid' in o.State or 'Error' in o.State]
    if not failed:
        return
    import tempfile
    path = os.path.join(tempfile.mkdtemp(prefix='cad-dsl-'), doc.Name + '.FCStd')
    doc.saveAs(path)
    raise ValueError('FreeCAD failed to build %s; see %s' % (', '.join(
        '%s (%s of %s)' % (o.Label, ' '.join(st for st in o.State if st != 'Touched'),
                           ', '.join(i.Label for i in o.OutList)) for o in failed), path))


def export(doc, result, shape, file_name, bindings):
    ext = os.path.splitext(file_name)[1].lower()
    if ext in ('.step', '.stp'):
        shape.exportStep(file_name)
    elif ext == '.stl':
        shape.exportStl(file_name)
    elif ext in ('.brep', '.brp'):
        shape.exportBrep(file_name)
    elif ext in ('.iges', '.igs'):
        shape.exportIges(file_name)
    elif ext == '.fcstd':
        save_document(doc, [result], file_name, bindings)
    else:
        raise ValueError('%s: the FreeCAD kernel exports .step, .stl, .brep, .iges and .FCStd files, not images'
                         % file_name)


def alias(name):
    """The spreadsheet alias of a parameter: an identifier that is not a cell address"""
    a = re.sub(r'\W', '_', name)
    if not a or a[0].isdigit() or re.fullmatch(r'[A-Za-z]{1,3}[0-9]+', a):
        a = 'p_' + a
    return a


def param_expression(value):
    """The expression of the parameter equal to value (or to its opposite), None if none"""
    if value == 0:
        return None
    for sign in (1, -1):
        for name, p in params.items():
            if abs(sign * value - p) <= 1e-9 * max(1.0, abs(p)):
                return ('Params.%s' if sign > 0 else '-Params.%s') % alias(name)
    return None


def bind_params(doc, bindings):
    """Write the parameters in the Params spreadsheet, and bind the numbers equal to one of them to its cell"""
    if not params:
        return
    sheet = doc.getObject('Params') or doc.addObject('Spreadsheet::Sheet', 'Params')
    for i, (name, value) in enumerate(params.items(), 1):
        sheet.set('A%d' % i, name)
        sheet.set('B%d' % i, repr(float(value)))
        sheet.setAlias('B%d' % i, alias(name))
    doc.recompute()
    names = set(o.Name for o in doc.Objects)
    for obj, path, value in bindings:
        expression = param_expression(value)
        if expression and obj.Name in names:
            obj.setExpression(path, expression)
    doc.recompute()


def save_document(doc, visible, file_name, bindings):
    """Save doc, showing only the visible features (None to keep the visibility as it is)"""
    bind_params(doc, bindings)
    if visible is not None:
        for o in doc.Objects:
            if hasattr(o, 'Visibility'):
                o.Visibility = o in visible
    doc.saveAs(os.path.abspath(file_name))


# ---------------------------------------------------------------------------------------------------------------------
# Assemblies
# ---------------------------------------------------------------------------------------------------------------------

class PartNode:
    """A part of an assembly: the result of a model"""

    def __init__(self, model, name, color):
        self.model = model
        self.obj = model.result_shape()
        if self.obj is None:
            raise ValueError('Empty part %s: build a solid before adding it to the assembly' % (name or ''))
        self.name = name
        self.color = color

    def shape(self):
        self.model.check(self.obj)
        return self.obj.Shape


class Assembly:
    """An assembly: an App::Part of links to the parts and sub-assemblies placed in it, copied in its document"""

    def __init__(self, name):
        self.doc = App.newDocument('Assembly')
        self.root = self.doc.addObject('App::Part', 'Assembly')
        self.root.Label = name or 'Assembly'
        self.sources = self.doc.addObject('App::DocumentObjectGroup', 'Sources')
        self.sources.Visibility = False
        self.links = []
        self.imported = {}

    def visitPart(self, name, color, model):
        return register(PartNode(handles[model], name, color))

    def import_node(self, handle):
        """The copy of a part or an assembly in this document, made once"""
        if handle in self.imported:
            return self.imported[handle]
        node = handles[handle]
        source = node.obj if isinstance(node, PartNode) else node.root
        before = set(o.Name for o in self.doc.Objects)
        copy = copy_with_placements(self.doc, source)
        # the copies are hidden in the Sources group, the links show them (the links of a sub-assembly stay visible)
        copied = [o for o in self.doc.Objects if o.Name not in before and not o.getParentGeoFeatureGroup()]
        for o in copied:
            o.Visibility = False
        self.sources.addObjects(copied)
        if isinstance(node, PartNode) and node.name:
            copy.Label = node.name
        self.imported[handle] = copy
        return copy

    def visitPlace(self, handle, location):
        target = self.import_node(handle)
        o = self.doc.addObject('App::Link', 'Link')
        o.LinkedObject = target
        o.Placement = to_placement(location)
        self.root.addObject(o)
        self.links.append(o)

    def visitMove(self, component, location, frames):
        self.links[component].Placement = to_placement(location)

    def visitAnimation(self, frame_count, fps):
        pass

    def visitFaceFrame(self, handle, direction, position):
        return planar_face_frame(handles[handle].shape(), vec(direction), vec(position))

    def visitAxisFrame(self, handle, position):
        return cylinder_axis_frame(handles[handle].shape(), vec(position))

    def check(self):
        self.doc.recompute()
        check_document(self.doc)

    def display(self, file_name):
        self.check()
        if file_name is None:
            return
        if '%' in file_name:
            raise ValueError('%s: the FreeCAD kernel renders no animation frames' % file_name)
        ext = os.path.splitext(file_name)[1].lower()
        if ext in ('.step', '.stp'):
            import Import
            Import.export([self.root], file_name)
        elif ext == '.fcstd':
            self.save(file_name)
        else:
            export(self.doc, self.root, Part.getShape(self.root), file_name, [])

    def save(self, file_name):
        self.check()
        save_document(self.doc, None, file_name, [])


def copy_with_placements(doc, source):
    """Copy source and what it depends on into doc. The copies of the sketches lose their placement (their attacher
    resets it): it is set back, the sources found by a temporary label."""
    sources = [source] + [o for o in source.OutListRecursive if hasattr(o, 'Placement')]
    labels = [o.Label for o in sources]
    for o in sources:
        o.Label = 'cad_dsl_copy_' + o.Name
    try:
        copy = doc.copyObject(source, True)
    finally:
        for o, label in zip(sources, labels):
            o.Label = label
    for o, label in zip(sources, labels):
        copies = doc.getObjectsByLabel('cad_dsl_copy_' + o.Name)
        if copies:
            copies[0].Label = label
            if copies[0].Placement != o.Placement and not copies[0].ExpressionEngine:
                copies[0].Placement = o.Placement
    return copy


def planar_face_frame(shape, direction, position):
    """[origin, normal] of the planar face of shape whose outward normal is direction: the farthest along it when
    position is None, else the closest to position, with the origin at position projected on it; None if none"""
    d = unit(direction)
    best = None
    for f in shape.Faces:
        surface = f.Surface
        if not isinstance(surface, Part.Plane):
            continue
        n = surface.Axis
        if f.Orientation == 'Reversed':
            n = -n
        if n.dot(d) < math.cos(math.pi / 180):
            continue
        if position is not None:
            score = -Part.Vertex(position).distToShape(f)[0]
        else:
            score = f.CenterOfMass.dot(d)
        if best is not None and score <= best[0]:
            continue
        origin = position - n * n.dot(position - surface.Position) if position is not None else f.CenterOfMass
        best = (score, origin, n)
    return None if best is None else xyz(best[1]) + xyz(best[2])


def cylinder_axis_frame(shape, position):
    """[origin, direction] of the axis of the cylindrical face of shape: the largest radius when position is None,
    else the closest to position; the direction points to +Z (else +Y, else +X), the origin is the start of the face
    along it, or position projected on the axis; None if none"""
    best = None
    eps = 1e-9
    for f in shape.Faces:
        c = f.Surface
        if not isinstance(c, Part.Cylinder):
            continue
        score = -Part.Vertex(position).distToShape(f)[0] if position is not None else c.Radius
        if best is not None and score <= best[0]:
            continue
        a = unit(c.Axis)
        d = a
        if d.z < -eps or (abs(d.z) <= eps and (d.y < -eps or (abs(d.y) <= eps and d.x < 0))):
            d = -d
        if position is not None:
            origin = c.Center + d * (position - c.Center).dot(d)
        else:
            umin, umax, vmin, vmax = f.ParameterRange
            e1 = c.Center + a * vmin
            e2 = c.Center + a * vmax
            origin = e1 if e1.dot(d) <= e2.dot(d) else e2
        best = (score, origin, d)
    return None if best is None else xyz(best[1]) + xyz(best[2])


# ---------------------------------------------------------------------------------------------------------------------
# Functions of the kernel
# ---------------------------------------------------------------------------------------------------------------------

def new_model():
    return register(Model())


def new_assembly(name):
    return register(Assembly(name))


def start_point(curve):
    return list(construction(curve).start)


def end_point(curve):
    return list(construction(curve).to)


def intersections(c1, c2):
    return [[p.x, p.y] for p in closed_geom(construction(c1)).intersectCC(closed_geom(construction(c2)))]


def volume(path):
    """Volume of the solids of a STEP or BREP file"""
    return Part.read(path).Volume


def set_params(values):
    params.clear()
    params.update(values)


def load_module(name, source):
    """Load Python code sent by the JVM as module name, e.g. the STEP converter"""
    spec = importlib.util.spec_from_loader(name, loader=None)
    module = importlib.util.module_from_spec(spec)
    exec(compile(source, name + '.py', 'exec'), module.__dict__)
    sys.modules[name] = module


def close(handle):
    o = handles.pop(handle, None)
    if o is not None and getattr(o, 'doc', None) is not None and not isinstance(o, PartNode):
        App.closeDocument(o.doc.Name)


FUNCTIONS = {
    'newModel': new_model,
    'newAssembly': new_assembly,
    'startPoint': start_point,
    'endPoint': end_point,
    'intersections': intersections,
    'setParams': set_params,
    'volume': volume,
    'loadModule': load_module,
    'close': close,
}
