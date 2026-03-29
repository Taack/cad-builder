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

    private class Tr {
        private static int inc = 0

        static void ind(String s) {
            println "    " * inc++ + s
        }

        static void dec(String s) {
            println "    " * --inc + s
        }

        static void cur(String s) {
            println "    " * inc + s
        }
    }
    MemorySegment shape
    MemorySegment shapeCutBase = null
    MemorySegment face
    List<MemorySegment> makeWires = []

    Stack<Vec> fromVecStack = new Stack<>()

    Vec getFromVec() {
        fromVecStack.empty() ? new Vec() : fromVecStack.peek()
    }
    Vec2d fromVec2d = new Vec2d()
    Vec2d oldFromVec2d
    Vec direction = new Vec(1)
    Vec directionNormal = null
    SurfaceBounds bounds
    Vec ptParam11
    Vec ptParam00
    List<OpenShape2D> openShape2dList = []
    List<OpenShape> openShapeList = []
    List<ClosedShape2D> closedShape2dList = []
    final Stack<List<MemorySegment>> boolShapes

    CadDslVisitor() {
        boolShapes = new Stack<List<MemorySegment>>()
        boolShapes.push([])
    }

    interface ClosedShape2D {
        MemorySegment makeWireAdd2d(MemorySegment makeWire)
    }

    interface ClosedShape {
        MemorySegment makeWireAdd(MemorySegment makeWire)
    }

    interface OpenShape2D {
        MemorySegment makeWireAdd(Vec2d fromLocal)

        Vec2d getTo()
    }

    interface OpenShape {
        MemorySegment makeWireAdd(Vec fromLocal)

        Vec getTo()
    }


    class Edge2d implements OpenShape2D {
        Vec2d to

        Edge2d(Vec2d to) {
            this.to = to
        }

        @Override
        MemorySegment makeWireAdd(Vec2d fromLocal) {
            return handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(fromLocal.toGpPnt2d(), to.toGpPnt2d())
        }

        @Override
        String toString() {
            return "Edge2d{" +
                    "to=" + to +
                    '}'
        }
    }

    class Edge implements OpenShape {
        Vec to

        Edge(Vec to) {
            this.to = to
        }

        @Override
        MemorySegment makeWireAdd(Vec fromLocal) {
            return handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(fromLocal.toGpPnt(), to.toGpPnt())
        }

        @Override
        String toString() {
            return "Edge{" +
                    "to=" + to +
                    '}'
        }
    }

    class Arc2d implements OpenShape2D {
        Vec2d to
        Vec2d center

        Arc2d(Vec2d to, Vec2d center) {
            this.to = to
            this.center = center
        }

        @Override
        MemorySegment makeWireAdd(Vec2d fromLocal) {
            return handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(fromLocal.toGpPnt2d(), center.toGpPnt2d(), to.toGpPnt2d())
        }

        @Override
        String toString() {
            return "Arc2d{" +
                    "to=" + to +
                    ", center=" + center +
                    '}'
        }
    }

    class Arc implements OpenShape {
        Vec to
        Vec center

        Arc(Vec to, Vec center) {
            this.to = to
            this.center = center
        }

        @Override
        MemorySegment makeWireAdd(Vec fromLocal) {
            return handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(fromLocal.toGpPnt(), center.toGpPnt(), to.toGpPnt())
        }

        @Override
        String toString() {
            return "Arc2d{" +
                    "to=" + to +
                    ", center=" + center +
                    '}'
        }
    }

    class Circle2d implements ClosedShape2D {
        final Vec2d pos
        final double radius

        Circle2d(Vec2d pos, double radius) {
            this.pos = pos
            this.radius = radius
        }

        @Override
        MemorySegment makeWireAdd2d(MemorySegment surf) {
            def MW = new_BRepBuilderAPI_MakeWire()
            def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), pos.toGpDir2d()), radius)
            def aline = handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c)
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(aline, surf))
            return MW
        }
    }

    class Circle implements ClosedShape {
        final Vec pos
        final double radius

        Circle(Vec pos, double radius) {
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

    class Rectangle2d implements ClosedShape2D {
        final double sX
        final double sY
        final Vec2d pos

        Rectangle2d(double sX, double sY, Vec2d pos, Vec2d dir) {
            this.sX = sX
            this.sY = sY
            this.pos = pos
        }

        @Override
        MemorySegment makeWireAdd2d(MemorySegment surf) {
            def MW = new_BRepBuilderAPI_MakeWire()

            Vec2d p1v = pos - new Vec2d(-sX / 2, -sY / 2)
            Vec2d p2v = pos - new Vec2d(sX / 2, -sY / 2)
            Vec2d p3v = pos - new Vec2d(sX / 2, sY / 2)
            Vec2d p4v = pos - new Vec2d(-sX / 2, sY / 2)

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
        Tr.ind("visitFrom $pos")
        fromVecStack << pos
    }

    @Override
    void visitFromEnd(Vec posOri) {
        Vec pos = posOri
        if (!openShapeList.empty) {
            def makeWire = new_BRepBuilderAPI_MakeWire()
            for (OpenShape s3d in openShapeList) {
                def trimmedCurve = s3d.makeWireAdd(pos)
                pos = s3d.to
                def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(trimmedCurve)
                _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(makeWire, arcEdge)
            }
            makeWires << makeWire
        }
        openShapeList.clear()
        fromVecStack.pop()
        Tr.dec("visitFromEnd $pos")
    }

    @Override
    void visitFrom(Vec2d pos) {
        Tr.ind("visitFrom $pos")
        fromVec2d = pos
    }

    @Override
    void visitFromEnd(Vec2d posOri) {
        Vec2d pos = posOri
        if (!openShape2dList.empty) {
            def makeWire = new_BRepBuilderAPI_MakeWire()
            for (OpenShape2D s2d in openShape2dList) {
                def trimmedCurve = s2d.makeWireAdd(pos)
                pos = s2d.to
//            def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(trimmedCurve, handle_Geom_Plan__gp_Pln(new Vec(0, 1, 0).toGpPln()))
                def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge2d__Geom2d_Curve(trimmedCurve)
                _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(makeWire, arcEdge)
            }
            makeWires << makeWire
        }
        openShape2dList.clear()
        fromVec2d = pos
        Tr.dec("visitFromEnd $pos")
    }

    @Override
    void visitBox(Number length, Number height, Number thickness) {
        def ax2 = directionNormal ? new_gp_Ax2__gp_Pnt_gp_Dir_Normal(fromVec.toGpPnt(), direction.toGpDir(), directionNormal.toGpDir()) : new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__Ax2_x_y_z(ax2, length.toDouble(), height.toDouble(), thickness.toDouble()))

        Tr.cur "box($length, $height, $thickness) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitSphere(Number radius, Number radian1, Number radian2) {
        Tr.cur "sphere($radius, $radian1, $radian2) from: $fromVec, direction: $direction, directionNormal: $directionNormal"

        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(ax2, radius.toDouble(), radian1.toDouble(), radian2.toDouble())
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitCylinder(Number radius, Number height) {

        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(ax2, radius.toDouble(), height.toDouble())

        Tr.cur "cylinder($radius, $height) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitTorus(Number torusRadius, Number ringRadius) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, torusRadius.toDouble(), ringRadius.toDouble())

        Tr.cur "torus($torusRadius, $ringRadius) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitCut() {
        Tr.ind("visitCut")
        boolShapes.peek().remove(shape)
        boolShapes.push([])
        Tr.cur "shape: $shape"
        if (shape) {
            shapeCutBase = shape
        } else Tr.cur "visitCut: Empty shape !!"
//        shape = null
    }

    @Override
    void visitCutEnd() {
        if (boolShapes.peek().empty) return
        def cutLasts = boolShapes.peek()
        def firstShape = shapeCutBase//cutLasts.first()
        Tr.cur "cutLasts: $cutLasts"
        for (def otherShape in cutLasts) {
            Tr.cur "cutting; $firstShape, $otherShape"
            firstShape = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(firstShape, otherShape)
        }
        shape = firstShape
        boolShapes.pop()
//        boolShapes.peek().removeFirst()
//        boolShapes.peek().addFirst(shape)
        Tr.dec("visitCutEnd $shape")
    }

    @Override
    void visitFuse() {
        Tr.ind("visitFuse")
        boolShapes.push([])
        Tr.cur("shape: $shape")
        if (shape) boolShapes.peek() << shape
        else Tr.cur "visitFuse: Empty shape !!"
//        shape = null
    }

    @Override
    void visitFuseEnd() {
        if (boolShapes.peek().empty) return
        def firstShape = shape// boolShapes.peek().first()
        def fuseLasts = boolShapes.peek()
        Tr.cur "fuseLasts: $fuseLasts"
        for (def otherShape in fuseLasts) {
            firstShape = new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(firstShape, otherShape)
        }
        shape = firstShape
        boolShapes.pop()
        boolShapes.peek().removeFirst()
        boolShapes.peek().addFirst(shape)
        Tr.dec("visitFuseEnd $shape")
    }

    @Override
    void visitCommon() {
        Tr.ind("visitCommon")
        boolShapes.push([])
        Tr.cur("shape: $shape")
        if (shape) boolShapes.peek() << shape
        else Tr.cur "visitCommon: Empty shape !!"
//        shape = null
    }

    @Override
    void visitCommonEnd() {
        if (boolShapes.peek().empty) return
        def firstShape = shape
        def commonLasts = boolShapes.peek()
        Tr.cur "commonLasts: $commonLasts"
        for (def otherShape in commonLasts) {
            firstShape = new_TopoDS_Shape__brep_algoapi_common__s1_s2(firstShape, otherShape)
        }
        shape = firstShape
        boolShapes.pop()
        boolShapes.peek().removeFirst()
        boolShapes.peek().addFirst(shape)
        Tr.dec("visitCommonEnd")
    }

    @Override
    void visitMirrorWire2d(Vec2d pos, Vec2d dir) {
        visitMirror(new Vec(pos), new Vec(dir))
    }

    @Override
    void visitMirrorWire(Vec pos, Vec dir) {
        visitMirror(pos, dir)
    }

    @Override
    void visitFillet(Number radius) {
        def mkFillet = new_BRepFilletAPI_MakeFillet__TopoDS_Shape(shape)
        def anEdgeExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(face ?: shape, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index)
        while (_TopExp_Explorer__More(anEdgeExplorer)) {
            def anEdge = ref_TopoDS_Edge__TopoDS_Shape(new_TopoDS_Shape__TopExp_Explorer__Current(anEdgeExplorer))
            //Add edge to fillet algorithm
            _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(mkFillet, radius.toDouble(), anEdge)
            _TopExp_Explorer__Next(anEdgeExplorer)
        }

        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(mkFillet)
    }

    @Override
    void visitHollowedSolid(Number thickness) {
        def facesToRemove = new_TopTools_ListOfShape()
        if (face) {
            _TopTools_ListOfShape__Append__TopoDS_Shape(facesToRemove, face)
        }
        def aSolidMaker = new_BRepOffsetAPI_MakeThickSolid()
        _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(aSolidMaker, shape, facesToRemove, thickness.toDouble(), 0.001d)

        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aSolidMaker)
    }

    @Override
    void visitEllipse2d(Vec2d dir, Number majDia, Number minDia) {

    }

    @Override
    void visitThruSection() {

    }

    @Override
    void visitWireFromSurface() {

    }

    @Override
    void visitWireFromSurfaceEnd() {

    }

    @Override
    void visitTrimmed(CadDslEdge2d curve, Number from, Number tp) {

    }

    @Override
    void visitThruSectionEnd() {

    }

    @Override
    void visitCylindricalSurface(Number number) {

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
//                fromVecStack.pop()
                fromVecStack << Vec.fromAPnt(aPnt)
                double aZ = fromVec.cord(direction)

                if (aZ > positionMax) {
                    Tr.cur "Face Selected"
                    positionMax = aZ
                    bounds = new SurfaceBounds(R4_Geom_Surface__Bounds(aSurface))
                    def pt = gp_Pnt__Geom_Surface__Value(aSurface, 1d, 1d)
                    ptParam11 = Vec.fromAPnt(pt)
                    pt = gp_Pnt__Geom_Surface__Value(aSurface, 0d, 0d)
                    ptParam00 = Vec.fromAPnt(pt)
                    face = aFace
                }
                Tr.cur "aZ: $aZ, positionMax: $positionMax"
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
        } else visualize(shape ?: face ?: ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative()))

    }

    @Override
    void visitCenter() {
        fromVecStack << Vec.fromAPnt(new_gp_Pnt__CentreOfMass__TopoDS_Shape(face))
        fromVec2d = fromVec.coordsProjection(direction, ptParam00)
        Tr.ind("visitCenter $fromVecStack $fromVec2d")
    }

    @Override
    void visitCenterEnd() {
        fromVecStack.pop()
        Tr.dec("visitCenterEnd $fromVecStack")
    }

    @Override
    void visiteCircle2d(Number radius) {
        closedShape2dList << new Circle2d(fromVec2d, radius.toDouble())
    }

    @Override
    void visitHole(Number depth) {
        def surf = handle_Geom_Surface__TopoDS_Face(face)
        def makeFace = new_BRepBuilderAPI_MakeFace()
        closedShape2dList.each {
            def MW = it.makeWireAdd2d(surf)
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
    void visitRect2d(Number sX, Number sY, Closure c) {
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
            closedShape2dList << new Rectangle2d(sX.toDouble(), sY.toDouble(), fromVec2d, direction)
        }
    }

    @Override
    void visitMove(Vec2d to) {
        fromVec2d = to + fromVec2d
    }

    @Override
    void visitMove(Vec to) {

    }

    @Override
    void visitTo(Vec2d to) {
        fromVec2d = to
    }

    @Override
    void visitTo(Vec to) {
        fromVecStack.pop()
        fromVecStack.push(to)
    }

    @Override
    void visitEdge(Vec2d to) {
        openShape2dList << new Edge2d(to)
    }

    @Override
    void visitEdge(Vec to) {
        openShapeList << new Edge(to)
    }

    @Override
    void visitArc(Vec2d to, Vec2d via) {
        openShape2dList << new Arc2d(to, via)
    }

    @Override
    void visitArc(Vec to, Vec via) {
        openShapeList << new Arc(to, via)
    }

    @Override
    void visitToFace() {
        Tr.cur("visitToFace: makeWires: $makeWires")
        MemorySegment wire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(makeWires.first())
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
    void visitToFaceFrom2d() {
        Tr.cur("visitToFace: makeWires: $makeWires")
//        MemorySegment wire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(makeWires.first())
//        face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wire)
        face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(new Vec(1).toGpPln(0))
//        face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wire)
        if (makeWires.size() > 0) {
            def builder = new_BRep_Builder()
            for (MemorySegment w in makeWires[0..makeWires.size() - 1]) {
                MemorySegment wire2 = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(w)
                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, face, wire2)
            }
        }
    }

    @Override
    void visitRevolution(Vec from, Vec dir) {
        def ax1 = new_gp_Ax1__p_dir(from.toGpPnt(), dir.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(face, ax1)

        MemorySegment trsf = new_gp_Trsf()
        _gp_Trsf__SetTranslation__gp_Vec(trsf, fromVec.toGpVec())
        shape = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, trsf, 0)

        Tr.cur "revol($from, $dir) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"

        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitPrism(Vec dir) {
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(face, dir.toGpVec())
        face = null
        makeWires = null
        Tr.cur "prism($dir) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"

        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
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

//    MemorySegment toShape() {
//        if (shape) shape
//        else if (face) face
//        else ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative())
//    }

    @Override
    void visitMirror(Vec pt, Vec dir) {
        Tr.cur("visitMirror($pt, $dir)")
        def ax1 = new_gp_Ax1__p_dir(pt.toGpPnt(), dir.toGpDir())
        def aTrsf = new_gp_Trsf()
        _gp_Trsf__SetMirror__gp_Ax1(aTrsf, ax1)
        def topoWire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(addCurrentWireNative())
        _TopoDS__Shape__Reverse(topoWire)
        def aBRepTrsf = new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(topoWire, aTrsf, 0, 0)
        def aMirroredWire = ref_TopoDS__Wire__TopoDS_Shape(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aBRepTrsf))
        def mkWire = new_BRepBuilderAPI_MakeWire()
        _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(mkWire, aMirroredWire)
        mkWire = addCurrentWireNative(mkWire)
        makeWires.pop()
        makeWires.push(mkWire)
    }

    @Override
    void visitDirection(Vec axis, Vec normal) {
        direction = axis
    }

}
