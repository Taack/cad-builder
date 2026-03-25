package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.ShapeEnum
import org.taack.cad.builder.SurfaceBounds
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class CadDslVisitor implements ICadDslVisitor {

    MemorySegment shape
    MemorySegment face
    List<MemorySegment> makeWires = []

    Vec fromVec = new Vec()
    Vec2d fromVec2d = new Vec2d()
    Vec2d oldFromVec2d
    Vec direction = new Vec(1)
    Vec directionNormal = null
    SurfaceBounds bounds
    Vec ptParam11
    Vec ptParam00
    List<OpenShape2D> openShape2DList = []
    List<ClosedShape2D> closedShape2dList = []
    List<MemorySegment> boolShape = []

    interface ClosedShape2D {
        MemorySegment makeWireAdd(MemorySegment makeWire)
    }

    interface OpenShape2D {
        MemorySegment makeWireAdd(Vec2d fromLocal)
        Vec2d getTo()
    }


    class Edge implements OpenShape2D {
        Vec2d to

        Edge(Vec2d to) {
            this.to = to
        }

        @Override
        MemorySegment makeWireAdd(Vec2d fromLocal) {
            return handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(fromLocal.toGpPnt2d(), to.toGpPnt2d())
        }

        @Override
        String toString() {
            return "Edge{" +
                    "to=" + to +
                    '}'
        }
    }

    class Arc implements OpenShape2D {
        Vec2d to
        Vec2d center

        Arc(Vec2d to, Vec2d center) {
            this.to = to
            this.center = center
        }

        @Override
        MemorySegment makeWireAdd(Vec2d fromLocal) {
            return handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(fromLocal.toGpPnt2d(), center.toGpPnt2d(), to.toGpPnt2d())
        }

        @Override
        String toString() {
            return "Arc{" +
                    "to=" + to +
                    ", center=" + center +
                    '}'
        }
    }

    class Circle implements ClosedShape2D {
        final Vec2d pos
        final double radius

        Circle(Vec2d pos, double radius) {
            this.pos = pos
            this.radius = radius
        }

        @Override
        MemorySegment makeWireAdd(MemorySegment surf) {
            def MW = new_BRepBuilderAPI_MakeWire()
            def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), pos.toGpDir2d()), radius)
            def aline = handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c)
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(aline, surf))
            return MW
        }
    }

    class Rectangle implements ClosedShape2D {
        final double sX
        final double sY
        final Vec2d pos

        Rectangle(double sX, double sY, Vec2d pos, Vec2d dir) {
            this.sX = sX
            this.sY = sY
            this.pos = pos
        }

        @Override
        MemorySegment makeWireAdd(MemorySegment surf) {
            def MW = new_BRepBuilderAPI_MakeWire()

            Vec2d p1v = pos - new Vec2d(-sX / 2, -sY/2)
            Vec2d p2v = pos - new Vec2d(sX / 2, -sY/2)
            Vec2d p3v = pos - new Vec2d(sX / 2, sY/2)
            Vec2d p4v = pos - new Vec2d(-sX / 2, sY/2)

            MemorySegment p1 = p1v.toGpPnt2d()
            MemorySegment p2 = p2v.toGpPnt2d()
            MemorySegment p3 = p3v.toGpPnt2d()
            MemorySegment p4 = p4v.toGpPnt2d()

            def e1 = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p1, p2)
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(e1, surf, 0d, gp_Pnt2d__Distance__p1_p2(p1, p2)))
            def e2 = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p2, p3)
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(e2, surf, 0d, gp_Pnt2d__Distance__p1_p2(p2, p3)))
            def e3 = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p3, p4)
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(e3, surf, 0d, gp_Pnt2d__Distance__p1_p2(p3, p4)))
            def e4 = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p4, p1)
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(e4, surf, 0d, gp_Pnt2d__Distance__p1_p2(p4, p1)))

            return MW
        }
    }

    @Override
    void visitFrom(Vec pos) {
        fromVec = pos
    }

    @Override
    void visitFromEnd(Vec pos) {
    }

    @Override
    void visitFrom(Vec2d pos) {
        fromVec2d = pos
    }

    @Override
    void visitFromEnd(Vec2d posOri) {
        Vec2d pos = posOri
        def makeWire = new_BRepBuilderAPI_MakeWire()
        for (OpenShape2D s2d in openShape2DList) {
            def trimmedCurve = s2d.makeWireAdd(pos)
            pos = s2d.to
//            def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(trimmedCurve, handle_Geom_Plan__gp_Pln(new Vec(0, 1, 0).toGpPln()))
            def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge2d__Geom2d_Curve(trimmedCurve)
            _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(makeWire, arcEdge)
        }
        openShape2DList.clear()
        fromVec2d = pos
        makeWires << makeWire
    }

    @Override
    void visitBox(Number length, Number height, Number thickness) {
        println "box($length, $height, $thickness) from: $fromVec, direction: $direction, directionNormal: $directionNormal"
        def ax2 = directionNormal ?new_gp_Ax2__gp_Pnt_gp_Dir_Normal(fromVec.toGpPnt(), direction.toGpDir(), directionNormal.toGpDir()) : new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__Ax2_x_y_z(ax2, length.toDouble(), height.toDouble(), thickness.toDouble()))
        if (boolShape.size() > 0) boolShape << shape
    }

    @Override
    void visitSphere(Number radius) {

    }

    @Override
    void visitCylinder(Number radius, Number height) {

    }

    @Override
    void visitTorus(Number torusRadius, Number ringRadius) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        shape = new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, torusRadius.toDouble(), ringRadius.toDouble())
        if (boolShape.size() > 0) boolShape << shape
    }

    @Override
    void visitCut() {
        boolShape << shape
        shape = null
    }

    @Override
    void visitCutEnd() {
        def firstShape = boolShape.first()
        println boolShape
        for (def otherShape in boolShape[1..boolShape.size() - 1]) {
            firstShape = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(firstShape, otherShape)
        }
        shape = firstShape
        boolShape.clear()
    }

    @Override
    void visitFuse() {
        boolShape << shape
        shape = null
    }

    @Override
    void visitFuseEnd() {
        def firstShape = boolShape.first()
        println boolShape
        for (def otherShape in boolShape[1..boolShape.size() - 1]) {
            firstShape = new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(firstShape, otherShape)
        }
        shape = firstShape
        boolShape.clear()
    }

    @Override
    void visitFace(Vec direction) {
        double positionMax = Double.NEGATIVE_INFINITY
        this.direction = direction
        for (def aFaceExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(shape, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             _TopExp_Explorer__More(aFaceExplorer);
             _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer)
            def aSurface = handle_Geom_Surface__TopoDS_Face(aFace)
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
//                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
//                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
                def aPnt = new_gp_Pnt__CentreOfMass__TopoDS_Shape(aFace)
                fromVec = Vec.fromAPnt(aPnt)
                double aZ = fromVec.cord(direction)

                if (aZ > positionMax) {
                    println "Face Selected"
                    positionMax = aZ
                    bounds = new SurfaceBounds(R4_Geom_Surface__Bounds(aSurface))
                    def pt = gp_Pnt__Geom_Surface__Value(aSurface, 1d, 1d)
                    ptParam11 = Vec.fromAPnt(pt)
                    pt = gp_Pnt__Geom_Surface__Value(aSurface, 0d, 0d)
                    ptParam00 = Vec.fromAPnt(pt)
                    face = aFace
                }
                println "aZ: $aZ, positionMax: $positionMax"
            }
        }
    }

    @Override
    void display(String fileName) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                if (fileName.endsWith(".step")) {
                    write_step(this.shape, t)
                } else if (fileName.endsWith(".stl")) {
                    write_stl(this.shape, t)
                } else
                    dumpShape(shape, 1920, 1080, t)
            }
        } else visualize(shape)

    }

    @Override
    void visitCenter() {
        fromVec = Vec.fromAPnt(new_gp_Pnt__CentreOfMass__TopoDS_Shape(face))
        fromVec2d = fromVec.coordsProjection(direction, ptParam00)
    }

    @Override
    void visitCenterEnd() {
    }

    @Override
    void visiteCircle(Number radius) {
        closedShape2dList << new Circle(fromVec2d, radius.toDouble())
    }

    @Override
    void visitHole(Number depth) {
        def surf = handle_Geom_Surface__TopoDS_Face(face)
        def makeFace = new_BRepBuilderAPI_MakeFace()
        closedShape2dList.each {
            def MW = it.makeWireAdd(surf)
//            def MW = new_BRepBuilderAPI_MakeWire()
//            def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(it.pos.toGpPnt2d(), it.pos.toGpDir2d()), it.radius)
//            def aline = handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c)
//            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(aline, surf))
            _BRepBuilderAPI_MakeFace__Init(makeFace, surf, 0, 0.01d)
            _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(makeFace, MW)
            def FP = TopoDS_Face__BRepBuilderAPI_MakeFace__Face(makeFace)
            _BRepLib__BuildCurves3d__TopoDS_Shape FP

//        def CurvePoles = new_TColgp_Array1OfPnt__Low_Up(1,3)
//        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(150,0,150).toGpPnt(), 1)
//        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(200,100,150).toGpPnt(), 2)
//        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(150,200,150).toGpPnt(), 3)
//        def curve = handle_Geom_BezierCurve__TColgp_Array1OfPnt(CurvePoles)
//        def E = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve curve
//        def W = new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1 E

//        def MKDP = new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(shape,FP,face,W,0,1)

            def MKDP = new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(shape, FP, surf, 0, 0, 1)
            _BRepFeat_MakeDPrism__Perform__Height(MKDP, -(direction.z + direction.y + direction.x) * depth.toDouble())
//            _BRepFeat_MakeDPrism__Perform__Height(MKDP, depth.toDouble())
//        _BRepFeat_MakePipe__Perform(MKDP)
            shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape MKDP
        }
        closedShape2dList.clear()
    }

    @Override
    void visitRect(Number sX, Number sY, Closure c) {
        if (c) { // Construction points
            double sYd = sY.toDouble()
            double sXd = sX.toDouble()
            oldFromVec2d = fromVec2d
            fromVec2d += new Vec2d(sXd / 2, sYd / 2)
            c.delegate = new CadDslEdge2d(visitor: this)
            c.call()
            fromVec2d = oldFromVec2d + new Vec2d(sXd / 2, -sYd.toDouble() / 2)
            c.delegate = new CadDslEdge2d(visitor: this)
            c.call()
            fromVec2d = oldFromVec2d + new Vec2d(-sXd / 2, -sYd / 2)
            c.delegate = new CadDslEdge2d(visitor: this)
            c.call()
            fromVec2d = oldFromVec2d + new Vec2d(-sXd / 2, sYd / 2)
            c.delegate = new CadDslEdge2d(visitor: this)
            c.call()
            fromVec2d = oldFromVec2d
        } else {
            closedShape2dList << new Rectangle(sX.toDouble(), sY.toDouble(), fromVec2d, direction)
        }
    }

    @Override
    void visitMove(Vec2d to) {
        fromVec2d = to + fromVec2d
    }

    @Override
    void visitTo(Vec2d to) {
        fromVec2d = to
    }

    @Override
    void visitEdge(Vec2d to) {
        openShape2DList << new Edge(to)
    }

    @Override
    void visitArc(Vec2d to, Vec2d via) {
        openShape2DList << new Arc(to, via)
    }

    @Override
    void visitToFace() {
        MemorySegment wire = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(makeWires.first())
        face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wire)
        if (makeWires.size() > 1) {
            def builder = new_BRep_Builder()
            for (MemorySegment w in makeWires[1..makeWires.size() - 1]) {
                MemorySegment wire2 = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(w)
                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, face, wire2)
            }
        }
    }

    @Override
    void visitRevolution(Vec from, Vec dir) {
        def ax1 = new_gp_Ax1__p_dir(from.toGpPnt(), dir.toGpDir())
        shape = new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(face, ax1)
    }

    @Override
    void visitPrism(Vec dir) {
        shape = new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(face, dir.toGpVec())
        face = null
    }

    @Override
    void visitMirror(Vec2d pos, Vec2d dir) {
        visitMirror(new Vec(pos), new Vec(dir))
    }
    private MemorySegment addCurrentWireNative(MemorySegment wireNative = null) {
        MemorySegment ret = wireNative ?: new_BRepBuilderAPI_MakeWire()
        if (makeWires.size() > 0) {
            makeWires.eachWithIndex { MemorySegment it, int i ->
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(ret, it)
            }
        }
        ret
    }

    MemorySegment toShape() {
        if (shape) shape
        else if (face) face
        else ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative())
    }

    @Override
    void visitMirror(Vec pt, Vec dir) {
        def ax1 = new_gp_Ax1__p_dir(pt.toGpPnt(), dir.toGpDir())
        def aTrsf = new_gp_Trsf()
        _gp_Trsf__SetMirror__gp_Ax1(aTrsf, ax1)
        def shape = toShape()
        _TopoDS__Shape__Reverse(shape)
        def aBRepTrsf = new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(shape, aTrsf, 0, 0)
        def aMirroredShape = ref_TopoDS__Wire__TopoDS_Shape(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aBRepTrsf))
        def mkWire = new_BRepBuilderAPI_MakeWire()
        _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(mkWire, aMirroredShape)
        mkWire = addCurrentWireNative(mkWire)
        makeWires.pop()
        makeWires.push(mkWire)
    }

    @Override
    void visitDirection(Vec axis, Vec normal) {
        direction = axis
    }

    @Override
    void visitCommon() {
        boolShape << shape
        shape = null
    }

    @Override
    void visitCommonEnd() {
        def firstShape = boolShape.first()
        println boolShape
        for (def otherShape in boolShape[1..boolShape.size() - 1]) {
            firstShape = new_TopoDS_Shape__brep_algoapi_common__s1_s2(firstShape, otherShape)
        }
        shape = firstShape
        boolShape.clear()

    }
}
