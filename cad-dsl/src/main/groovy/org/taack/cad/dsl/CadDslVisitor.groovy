package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.*
import org.taack.cad.dsl.geom.*
import org.taack.cad.dsl.geom.adapter.Trimmable2dToIOpenShapeAdapter
import org.taack.cad.dsl.helper.SurfaceBounds
import org.taack.cad.dsl.helper.SurfaceDistance

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class CadDslVisitor implements ICadDslVisitor {

    class Tr {
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
    final List<MemorySegment> makeWires = []
    MemorySegment currentSurface
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
    List<IOpenShape2d> openShape2dList = []
    List<IOpenShape> openShapeList = []
    List<IClosedShape2d> closedShape2dList = []
    final Stack<List<MemorySegment>> boolShapes

    CadDslVisitor() {
        boolShapes = new Stack<List<MemorySegment>>()
        boolShapes.push([])
    }



    @Override
    void visitFrom(Vec pos) {
        Tr.ind("visitFrom $pos")
        fromVecStack << pos
    }

    @Override
    void visitFromEnd(Vec posOri) {
        Tr.cur("visitFromEnd $posOri")

        Tr.cur("visitFromEnd openShapeList $openShapeList")
        if (!face) face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(direction.toGpPln(0))
        currentSurface = handle_Geom_Surface__TopoDS_Face(face)
        if (!openShapeList.empty) {
            Vec pos = fromVec ?: new Vec()
            def makeWire = new_BRepBuilderAPI_MakeWire()
            for (IOpenShape s3d in openShapeList) {
                def trimmedCurve = s3d.makeWireAdd(pos)
                pos = s3d.to
//                def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(trimmedCurve)
//                def arcEdge = new_BRepBuilderAPI_MakeEdge__Geom_Curve(trimmedCurve)
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(makeWire, trimmedCurve)
            }
            makeWires << makeWire
        }
        openShapeList.clear()
        fromVecStack.pop()
        Tr.dec("visitFromEnd $fromVec")
    }

    @Override
    void visitFrom(Vec2d pos) {
        Tr.ind("visitFrom 2D $pos")
        if (pos) fromVec2d = pos
    }

    @Override
    void visitFromEnd(Vec2d posOri) {
        Tr.cur("visitFromEnd $posOri")

        Tr.cur("visitFromEnd openShape2dList $openShape2dList")
        if (!face) face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(direction.toGpPln(0))
        currentSurface = handle_Geom_Surface__TopoDS_Face(face)

        if (!openShape2dList.empty) {
            Vec2d pos = fromVec2d ?: new Vec2d()

            def makeWire = new_BRepBuilderAPI_MakeWire()
            for (IOpenShape2d s2d in openShape2dList) {
                def trimmedCurve = s2d.makeWireAdd(pos)
                pos = s2d.to
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(makeWire, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(trimmedCurve, currentSurface))
            }


            makeWires << makeWire
            openShape2dList.clear()
        }
        Tr.cur("visitFromEnd closedShape2dList $closedShape2dList")
        if (!closedShape2dList.empty) {
            for (IClosedShape2d s2d in closedShape2dList) {
                def c = s2d.make2dCurve()
                def makeWire = new_BRepBuilderAPI_MakeWire()
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(makeWire, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(c, currentSurface))
                makeWires << makeWire
            }
            closedShape2dList.clear()
        }
        Tr.dec("visitFromEnd $fromVec2d")
//        fromVec2d = null
    }

    @Override
    void visitBox(double length, double height, double thickness) {
        def ax2 = directionNormal ? new_gp_Ax2__gp_Pnt_gp_Dir_Normal(fromVec.toGpPnt(), direction.toGpDir(), directionNormal.toGpDir()) : new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__Ax2_x_y_z(ax2, length, height, thickness))

        Tr.cur "box($length, $height, $thickness) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitSphere(double radius, double radian1, double radian2) {
        Tr.cur "sphere($radius, $radian1, $radian2) from: $fromVec, direction: $direction, directionNormal: $directionNormal"

        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(ax2, radius, radian1, radian2)
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitCylinder(double radius, double height) {

        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(ax2, radius, height)

        Tr.cur "cylinder($radius, $height) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitTorus(double torusRadius, double ringRadius) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, torusRadius, ringRadius)

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

    private void popShape() {
        boolShapes.pop()
        if (!boolShapes.peek().empty) boolShapes.peek().removeFirst()
        boolShapes.peek().addFirst(shape)

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
        popShape()
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
        popShape()
        Tr.dec("visitCommonEnd")
    }

    @Override
    void visitFillet(double radius) {
        def mkFillet = new_BRepFilletAPI_MakeFillet__TopoDS_Shape(shape)
        def anEdgeExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(face ?: shape, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index)
        while (_TopExp_Explorer__More(anEdgeExplorer)) {
            def anEdge = ref_TopoDS_Edge__TopoDS_Shape(new_TopoDS_Shape__TopExp_Explorer__Current(anEdgeExplorer))
            //Add edge to fillet algorithm
            _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(mkFillet, radius, anEdge)
            _TopExp_Explorer__Next(anEdgeExplorer)
        }

        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(mkFillet)
    }

    @Override
    void visitHollowedSolid(double thickness) {
        def facesToRemove = new_TopTools_ListOfShape()
        if (face) {
            Tr.cur("visitHollowedSolid $face")
            _TopTools_ListOfShape__Append__TopoDS_Shape(facesToRemove, face)
        }
        def aSolidMaker = new_BRepOffsetAPI_MakeThickSolid()
        _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(aSolidMaker, shape, facesToRemove, thickness, 0.001d)

        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aSolidMaker)
    }

    @Override
    Ellipse2d visitEllipse2d(Vec2d dir, double majDia, double minDia) {
        Ellipse2d e = new Ellipse2d(fromVec2d, dir, majDia, minDia)
        closedShape2dList << e
        e
    }

    @Override
    void visitWireFromSurface() {

    }

    @Override
    void visitWireFromSurfaceEnd() {

    }

    @Override
    ITrimmable2d visitTrimmed(IClosedShape2d curve, double from, double tp, boolean reverse) {

    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, double from, double to, boolean reverse) {
        ArcOfCircle2d arc = new ArcOfCircle2d(circle2d, from, to, reverse)
        openShape2dList << arc
        arc
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, double to, boolean reverse) {
        ArcOfCircle2d arc = new ArcOfCircle2d(circle2d, from, to, reverse)
        openShape2dList << arc
        arc
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, Vec2d to, boolean reverse) {
        ArcOfCircle2d arc = new ArcOfCircle2d(circle2d, from, to, reverse)
        openShape2dList << arc
        arc
    }

    @Override
    void visitCylindricalSurface(double radius) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        currentSurface = handle_Geom_CylindricalSurface__ax2_radius(ax2, radius)
    }

    @Override
    void visitClosedWire() {
        Tr.ind "visitClosedWire dir: $direction, fromVec2d: $fromVec2d"
        visitFrom(fromVec2d)
    }

    @Override
    void visitClosedWireEnd() {
        visitFromEnd(fromVec2d)
        Tr.dec "visitClosedWireEnd dir: $direction, fromVec2d: $fromVec2d"
    }

    @Override
    void visitRemoveFromConstruction(IConstruction... toRemove) {
        closedShape2dList.removeAll(toRemove)
        openShape2dList.removeAll(toRemove)
    }

    @Override
    ITrimmable2d visitMirror(ITrimmable2d curve, Vec2d pos, Vec2d dir) {
        Mirrored2d mirrored2d = new Mirrored2d(curve, pos, dir)
        openShape2dList << mirrored2d
        return mirrored2d
    }

    @Override
    void visitAddToConstruction(IConstruction... toAdd) {
        for (IConstruction c in toAdd) {
            if (IOpenShape2d.isAssignableFrom(c.class)) {
                openShape2dList.addLast(c as IOpenShape2d)
            }
        }
    }

    @Override
    void visitAdapt3d(ITrimmable2d trimmed2dCurve, Vec dirX, Vec dirY) {
        openShapeList.add new Trimmable2dToIOpenShapeAdapter(trimmed2dCurve, fromVec, dirX, dirY)
    }

    @Override
    void visitSolidMirror(Vec pos, Vec dir) {
        Tr.cur("visitSolidMirror ${boolShapes.peek()}")

        def shape = boolShapes.peek().empty ? shape : boolShapes.peek().last()
        if (shape) {
            def mirror = new_gp_Trsf()
            _gp_Trsf__SetMirror__gp_Ax2(mirror, new_gp_Ax2__gp_Pnt_gp_Dir(pos.toGpPnt(), dir.toGpDir()))
            def mirrored_cut_1 = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, mirror, 0)
            boolShapes.peek() << mirrored_cut_1
            if (!this.shape) this.shape = mirrored_cut_1
        }
    }

    @Override
    void visitSolidTranslate(Vec distance) {
        Tr.cur("visitSolidTranslate ${boolShapes.peek()}")
        def shape = boolShapes.peek().empty ? shape : boolShapes.peek().last()
        if (shape) {
            def translate = new_gp_Trsf()
            _gp_Trsf__SetTranslation__gp_Vec(translate, distance.toGpVec())
            def moved = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, translate, 0)
            if (boolShapes.peek().empty) this.shape = moved
            else {
                boolShapes.peek().removeLast()
                boolShapes.peek() << moved
            }
        }
    }

    @Override
    void visitSolidRotate(Vec pos, Vec dir, double angle, boolean clone) {
        Tr.cur("visitSolidTranslatevisitSolidRotate ${boolShapes.peek()}")
        def shape = boolShapes.peek().empty ? shape : boolShapes.peek().last()
        if (shape) {
            def translate = new_gp_Trsf()
            _gp_Trsf__SetRotation__gp_Vec(translate, new_gp_Ax1__p_dir(pos.toGpPnt(), dir.toGpDir()), angle)
            def moved = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, translate, clone ? 1 : 0)
            if (boolShapes.peek().empty) this.shape = moved
            else {
                if (!clone)
                    boolShapes.peek().removeLast()
                boolShapes.peek() << moved
            }
        }

    }

    @Override
    void visitCone(double r1, double r2, double height) {
        def ax2 = directionNormal ? new_gp_Ax2__gp_Pnt_gp_Dir_Normal(fromVec.toGpPnt(), direction.toGpDir(), directionNormal.toGpDir()) : new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(ax2, r1, r2, height)

        Tr.cur "cone($r1, $r2, $height) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape

    }

    @Override
    void visitThruSection() {

    }

    @Override
    void visitThruSectionEnd() {

    }

    @Override
    void visitFace(Vec direction, Vec position) {
        Tr.cur("visitFace $direction $position")
        double positionMax = position == null ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY
        this.direction = direction
        Vec fromVecToPush = null

        for (def aFaceExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(shape, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             _TopExp_Explorer__More(aFaceExplorer);
             _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer)
            def aSurface = handle_Geom_Surface__TopoDS_Face(aFace)

            SurfaceBounds sBounds = new SurfaceBounds(aSurface)
            Tr.cur("sBounds $sBounds")
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
                double aZ
                if (position == null)  {
//                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
//                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
                    MemorySegment aPnt = new_gp_Pnt__CentreOfMass__TopoDS_Shape(aFace)
                    fromVecToPush = Vec.fromAPnt(aPnt)
                    aZ = fromVecToPush.cord(direction)
//                fromVecStack.pop()

                } else {
                    def ax1 = new_gp_Ax1__p_dir(position.toGpPnt(), direction.toGpDir())
                    def line = handle_Geom_Line__ax1(ax1)
                    def segment = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(line, 0d, 0.2d))
                    SurfaceDistance d = new SurfaceDistance(segment, aFace)
                    aZ = d.dist
                    fromVecToPush = d.v2
//                    def extrema = new_GeomAPI_ExtremaCurveSurface__curve_surface(segment, aSurface)
//                    int nbExtrema = i_GeomAPI_ExtremaCurveSurface__NbExtrema(extrema)
//                    Tr.cur("nbExtrema: $nbExtrema")
//                    if (nbExtrema > 0) {
//                        double distance = Double.NEGATIVE_INFINITY
//                        for (int i = 1; i <= nbExtrema; i++) {
////                            double extremaDistance = r_GeomAPI_ExtremaCurveSurface__Distance__index(extrema, i)
////                            Tr.cur("extremaDistance: $extremaDistance, $i")
////
////                            if (extremaDistance <= distance) {
////                                distance = extremaDistance
////                                SurfaceExtrema surfaceExtrema = new SurfaceExtrema(R6_GeomAPI_ExtremaCurveSurface__NbExtrema(extrema, i))
////                                fromVecToPush = new Vec(surfaceExtrema.p1x, surfaceExtrema.p1y, surfaceExtrema.p1z)
////                                aZ = fromVecToPush.cord(direction)
////                            }
//
////                            double extremaDistance = r_GeomAPI_ExtremaCurveSurface__Distance__index(extrema, i)
////                            Tr.cur("extremaDistance: $extremaDistance, $i")
//
//                            if (aZ > distance) {
////                                distance = extremaDistance
////                                SurfaceExtrema surfaceExtrema = new SurfaceExtrema(R6_GeomAPI_ExtremaCurveSurface__NbExtrema(extrema, i))
////                                fromVecToPush = new Vec(surfaceExtrema.p1x, surfaceExtrema.p1y, surfaceExtrema.p1z)
//                                MemorySegment aPnt = new_gp_Pnt__CentreOfMass__TopoDS_Shape(aFace)
//                                fromVecToPush = Vec.fromAPnt(aPnt)
//
//                                aZ = r_GeomAPI_ExtremaCurveSurface__Distance__index(extrema, i)
//                                distance = aZ
//                                Tr.cur "extrema: $aZ, fromVec = $fromVecToPush"
//                            }
                        }
//                    }
//                }


                if ((position == null && aZ > positionMax) || (position != null && aZ < positionMax)) {
//                if (aZ > positionMax) {
                    Tr.cur "Face Selected"
                    positionMax = aZ
                    bounds = new SurfaceBounds(aSurface)
                    def pt = gp_Pnt__Geom_Surface__Value(aSurface, 1d, 1d)
                    ptParam11 = Vec.fromAPnt(pt)
                    pt = gp_Pnt__Geom_Surface__Value(aSurface, 0d, 0d)
                    ptParam00 = Vec.fromAPnt(pt)
                    face = aFace
                    if (fromVecStack.size() > 0) fromVecStack.pop()
                    if (fromVecToPush) fromVecStack.push(fromVecToPush)
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
        } else {
            MemorySegment s = !boolShapes.empty() && !boolShapes.peek().empty? boolShapes.peek().last() : shape
            visualize(s ?: face ?: ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative()))
        }

    }

    @Override
    void visitCenter() {
        fromVecStack << Vec.fromAPnt(new_gp_Pnt__CentreOfMass__TopoDS_Shape(face))
        fromVec2d = fromVec.coordsProjection(direction, ptParam00)
        Tr.ind("visitCenter $fromVec $fromVec2d $ptParam00")
    }

    @Override
    void visitCenterEnd() {
        fromVecStack.pop()
        Tr.dec("visitCenterEnd $fromVecStack")
    }

    @Override
    Circle2d visitCircle2d(double radius, boolean reverse) {
        Circle2d c = new Circle2d(fromVec2d, radius, reverse)
        closedShape2dList << c
        return c
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
        Tr.cur "visitTo $to"
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
        Tr.cur("visitToFaceFrom2d: makeWires: $makeWires")
        MemorySegment wire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(makeWires.first())

        face = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wire)

//        if (makeWireReverse.contains(makeWires.first())) {
//            Tr.cur "reverse face 1"
//            _TopoDS__Shape__Reverse(face)
//        }

        if (makeWires.size() > 1) {
            def builder = new_BRep_Builder()
            for (MemorySegment w in makeWires[1..makeWires.size() - 1]) {
                MemorySegment wire2 = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(w)
                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, face, wire2)
//                if (makeWireReverse.contains(w)) {
//                    Tr.cur "reverse face W"
//                    _TopoDS__Shape__Reverse(face)
//                }

            }
        }
    }

    @Override
    void visitRevolution(Vec from, Vec dir, double angle) {
        def ax1 = new_gp_Ax1__p_dir(from.toGpPnt(), dir.toGpDir())
        def shape = angle == 0 ? new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(face, ax1) : new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(face, ax1, angle)

        MemorySegment trsf = new_gp_Trsf()
        _gp_Trsf__SetTranslation__gp_Vec(trsf, fromVec.toGpVec())
        shape = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, trsf, 0)

        Tr.cur "revol($from, $dir) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"

        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitPrism(Vec dir, boolean cut) {
        def shape = new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(face, dir.toGpVec())
        Tr.cur "prism($dir) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"

        if (this.shape) {
            Tr.cur "cut: $cut"
            if (cut) {
                shape = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(this.shape, shape)
            } else {
                def los = new_TopTools_ListOfShape()
                _TopTools_ListOfShape__Append__TopoDS_Shape(los, shape)
                shape = new_TopoDS_Shape__brep_algoapi_fuse__s1_listrOfShape(this.shape, los)
            }
            boolShapes.peek() << shape
        }

        face = null
        makeWires.clear()
        this.shape = shape
    }

    @Override
    void visitPrism(double high, boolean cut) {

        visitPrism(direction * high, cut)
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


    @Override
    void visitDirection(Vec axis, Vec normal) {
        direction = axis
    }

}
