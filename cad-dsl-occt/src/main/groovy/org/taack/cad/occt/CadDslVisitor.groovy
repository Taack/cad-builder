package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.builder.*
import org.taack.cad.dsl.CadDisplay
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.PlateOptions
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.TextOptions
import org.taack.cad.dsl.geom.*
import org.taack.cad.dsl.svg.SvgContour

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

import static org.taack.occt.NativeLib.*
import static org.taack.cad.occt.Occt.*
import org.taack.occt.NativeLib
import static org.taack.cad.occt.Natives.lastError
import static org.taack.cad.occt.Natives.own

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
    private static final double THRU_SECTIONS_PRECISION = 1.0e-06d

    // Owns every native object created for this model (see Natives)
    private final Arena nativeScope = Natives.openScope()

    MemorySegment shape
    MemorySegment shapeCutBase = null
    MemorySegment face
    final List<MemorySegment> makeWires = []
    // Texts (faces) and SVG drawings (wires) of the current sketch, joined to its wires by toFace()
    final List<MemorySegment> drawnShapes = []
    MemorySegment currentSurface
    // Surface the wires of a thru section are drawn on; kept apart since leaving a position resets currentSurface
    MemorySegment sectionSurface
    final List<MemorySegment> sectionWires = []
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
        if (!face) face = own(new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gpPln(direction, 0)), NativeLib::delete_TopoDS_Face)
        currentSurface = own(handle_Geom_Surface__TopoDS_Face(face), NativeLib::delete_handle_Geom_Surface)
        if (!openShapeList.empty) {
            makeWires << makeWire3d()
        }
        openShapeList.clear()
        fromVecStack.pop()
        Tr.dec("visitFromEnd $fromVec")
    }

    /**
     * @return The wire (BRepBuilderAPI_MakeWire) of the open 3D shapes drawn from the current position, which are consumed
     */
    private MemorySegment makeWire3d() {
        Vec pos = fromVec ?: new Vec()
        def makeWire = own(new_BRepBuilderAPI_MakeWire(), NativeLib::delete_BRepBuilderAPI_MakeWire)
        for (IOpenShape s3d in openShapeList) {
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(makeWire, OcctGeom.edge(s3d, pos))
            pos = s3d.to
        }
        openShapeList.clear()
        makeWire
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
        if (!face) face = own(new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gpPln(direction, 0)), NativeLib::delete_TopoDS_Face)
        currentSurface = own(handle_Geom_Surface__TopoDS_Face(face), NativeLib::delete_handle_Geom_Surface)

        if (!openShape2dList.empty) {
            makeWires << makeWire2d(currentSurface)
        }
        Tr.cur("visitFromEnd closedShape2dList $closedShape2dList")
        if (!closedShape2dList.empty) {
            for (IClosedShape2d s2d in closedShape2dList) {
                def c = OcctGeom.curve2d(s2d)
                def makeWire = own(new_BRepBuilderAPI_MakeWire(), NativeLib::delete_BRepBuilderAPI_MakeWire)
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(makeWire, own(new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(c, currentSurface), NativeLib::delete_BRepBuilderAPI_MakeEdge))
                makeWires << makeWire
            }
            closedShape2dList.clear()
        }
        Tr.dec("visitFromEnd $fromVec2d")
//        fromVec2d = null
    }

    /**
     * @return The wire (BRepBuilderAPI_MakeWire) of the open 2D shapes drawn on surface, which are consumed
     */
    private MemorySegment makeWire2d(MemorySegment surface) {
        Vec2d pos = fromVec2d ?: new Vec2d()

        def makeWire = own(new_BRepBuilderAPI_MakeWire(), NativeLib::delete_BRepBuilderAPI_MakeWire)
        for (IOpenShape2d s2d in openShape2dList) {
            def trimmedCurve = OcctGeom.curve2d(s2d, pos)
            pos = s2d.to
            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(makeWire, own(new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(trimmedCurve, surface), NativeLib::delete_BRepBuilderAPI_MakeEdge))
        }
        openShape2dList.clear()
        makeWire
    }

    @Override
    void visitBox(double length, double height, double thickness) {
        def ax2 = directionNormal ? own(new_gp_Ax2__gp_Pnt_gp_Dir_Normal(gpPnt(fromVec), gpDir(direction), gpDir(directionNormal)), NativeLib::delete_gp_Ax2) : own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(fromVec), gpDir(direction)), NativeLib::delete_gp_Ax2)
        def shape = own(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(own(new_BRepPrimAPI_MakeBox__Ax2_x_y_z(ax2, length, height, thickness), NativeLib::delete_BRepPrimAPI_MakeBox)), NativeLib::delete_TopoDS_Shape)

        Tr.cur "box($length, $height, $thickness) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitSphere(double radius, double radian1, double radian2) {
        Tr.cur "sphere($radius, $radian1, $radian2) from: $fromVec, direction: $direction, directionNormal: $directionNormal"

        def ax2 = own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(fromVec), gpDir(direction)), NativeLib::delete_gp_Ax2)
        def shape = own(new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(ax2, radius, radian1, radian2), NativeLib::delete_TopoDS_Shape)
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitCylinder(double radius, double height) {

        def ax2 = own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(fromVec), gpDir(direction)), NativeLib::delete_gp_Ax2)
        def shape = own(new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(ax2, radius, height), NativeLib::delete_TopoDS_Shape)

        Tr.cur "cylinder($radius, $height) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitTorus(double torusRadius, double ringRadius) {
        def ax2 = own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(fromVec), gpDir(direction)), NativeLib::delete_gp_Ax2)
        def shape = own(new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, torusRadius, ringRadius), NativeLib::delete_TopoDS_Shape)

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
            firstShape = own(new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(firstShape, otherShape), NativeLib::delete_TopoDS_Shape)
        }
        shape = firstShape
        boolShapes.pop()
        // the result is the last solid, as after a fuse or a prism
        boolShapes.peek() << shape
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
            firstShape = own(new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(firstShape, otherShape), NativeLib::delete_TopoDS_Shape)
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
            firstShape = own(new_TopoDS_Shape__brep_algoapi_common__s1_s2(firstShape, otherShape), NativeLib::delete_TopoDS_Shape)
        }
        shape = firstShape
        popShape()
        Tr.dec("visitCommonEnd")
    }

    @Override
    void visitFillet(double radius) {
        def mkFillet = own(new_BRepFilletAPI_MakeFillet__TopoDS_Shape(shape), NativeLib::delete_BRepFilletAPI_MakeFillet)
        def anEdgeExplorer = own(new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(face ?: shape, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index), NativeLib::delete_TopExp_Explorer)
        while (_TopExp_Explorer__More(anEdgeExplorer)) {
            def anEdge = ref_TopoDS_Edge__TopoDS_Shape(own(new_TopoDS_Shape__TopExp_Explorer__Current(anEdgeExplorer), NativeLib::delete_TopoDS_Shape))
            //Add edge to fillet algorithm
            _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(mkFillet, radius, anEdge)
            _TopExp_Explorer__Next(anEdgeExplorer)
        }

        def filleted = own(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(mkFillet), NativeLib::delete_TopoDS_Shape)
        if (filleted == MemorySegment.NULL) throw new IllegalArgumentException("fillet($radius): ${lastError()}")
        replaceShape(filleted)
    }

    // The solid made from the current one replaces it, as the last solid
    private void replaceShape(MemorySegment made) {
        boolShapes.peek().remove(shape)
        boolShapes.peek() << made
        shape = made
    }

    @Override
    void visitHollowedSolid(double thickness) {
        def facesToRemove = own(new_TopTools_ListOfShape(), NativeLib::delete_TopTools_ListOfShape)
        if (face) {
            Tr.cur("visitHollowedSolid $face")
            _TopTools_ListOfShape__Append__TopoDS_Shape(facesToRemove, face)
        }
        def aSolidMaker = own(new_BRepOffsetAPI_MakeThickSolid(), NativeLib::delete_BRepOffsetAPI_MakeThickSolid)
        _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(aSolidMaker, shape, facesToRemove, thickness, 0.001d)

        replaceShape(own(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aSolidMaker), NativeLib::delete_TopoDS_Shape))
    }

    @Override
    Ellipse2d visitEllipse2d(Vec2d dir, double majDia, double minDia) {
        Ellipse2d e = new Ellipse2d(fromVec2d, dir, majDia, minDia)
        closedShape2dList << e
        e
    }

    @Override
    void visitWireFromSurface() {
        Tr.ind("visitWireFromSurface")
    }

    @Override
    void visitWireFromSurfaceEnd() {
        MemorySegment wire = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(makeWire2d(sectionSurface))
        _BRepLib__BuildCurves3d__TopoDS_Shape(wire)
        sectionWires << wire
        // Only open shapes make the wire; closed ones were constructions for them
        closedShape2dList.clear()
        Tr.dec("visitWireFromSurfaceEnd sectionWires: $sectionWires")
    }

    @Override
    ITrimmable2d visitTrimmed(IClosedShape2d curve, double from, double to, boolean reverse) {
        TrimmedCurve2d trimmed = new TrimmedCurve2d(curve, from, to, reverse)
        openShape2dList << trimmed
        trimmed
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
        def ax2 = own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(fromVec), gpDir(direction)), NativeLib::delete_gp_Ax2)
        sectionSurface = own(handle_Geom_CylindricalSurface__ax2_radius(ax2, radius), NativeLib::delete_handle_Geom_CylindricalSurface)
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
            def mirror = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
            _gp_Trsf__SetMirror__gp_Ax2(mirror, own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(pos), gpDir(dir)), NativeLib::delete_gp_Ax2))
            def mirrored_cut_1 = own(new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, mirror, 0), NativeLib::delete_TopoDS_Shape)
            boolShapes.peek() << mirrored_cut_1
            if (!this.shape) this.shape = mirrored_cut_1
        }
    }

    @Override
    void visitSolidTranslate(Vec distance) {
        Tr.cur("visitSolidTranslate ${boolShapes.peek()}")
        def shape = boolShapes.peek().empty ? shape : boolShapes.peek().last()
        if (shape) {
            def translate = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
            _gp_Trsf__SetTranslation__gp_Vec(translate, gpVec(distance))
            def moved = own(new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, translate, 0), NativeLib::delete_TopoDS_Shape)
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
            def translate = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
            _gp_Trsf__SetRotation__gp_Vec(translate, own(new_gp_Ax1__p_dir(gpPnt(pos), gpDir(dir)), NativeLib::delete_gp_Ax1), angle)
            def moved = own(new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, translate, clone ? 1 : 0), NativeLib::delete_TopoDS_Shape)
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
        def ax2 = directionNormal ? own(new_gp_Ax2__gp_Pnt_gp_Dir_Normal(gpPnt(fromVec), gpDir(direction), gpDir(directionNormal)), NativeLib::delete_gp_Ax2) : own(new_gp_Ax2__gp_Pnt_gp_Dir(gpPnt(fromVec), gpDir(direction)), NativeLib::delete_gp_Ax2)
        def shape = own(new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(ax2, r1, r2, height), NativeLib::delete_TopoDS_Shape)

        Tr.cur "cone($r1, $r2, $height) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape

    }

    // Helix being built by visitHelix, swept by visitHelixProfileEnd
    MemorySegment helixSpine
    Vec helixStart
    Vec helixRadial
    Vec helixAxis

    @Override
    void visitHelix(double radius, double pitch, double height, double endRadius) {
        helixAxis = unit(direction)
        helixRadial = directionNormal ? unit(directionNormal) : unit(Math.abs(helixAxis.y) < 0.9 ? Vec.vY / helixAxis : Vec.vZ / helixAxis)
        helixStart = fromVec + helixRadial * radius
        def ax2 = own(new_gp_Ax2__gp_Pnt_gp_Dir_Normal(gpPnt(fromVec), gpDir(helixAxis), gpDir(helixRadial)), NativeLib::delete_gp_Ax2)
        helixSpine = own(new_TopoDS_Wire__HelixBRep_BuilderHelix__ax2_diam1_diam2_height_pitch(ax2, 2 * radius, 2 * endRadius, height, pitch), NativeLib::delete_TopoDS_Wire)
        if (helixSpine == MemorySegment.NULL) throw new IllegalArgumentException("helix($radius, $pitch, $height, $endRadius): ${lastError()}")
        Tr.cur "helix($radius, $pitch, $height, $endRadius) from: $fromVec, axis: $helixAxis, radial: $helixRadial"
    }

    // Drawing state hidden while a profile is drawn on its own plane
    private MemorySegment savedFace
    private MemorySegment savedSurface
    private List<MemorySegment> savedMakeWires
    private Vec2d savedFromVec2d

    /**
     * Start drawing a profile on the plane of origin "origin", whose 2D x and y go along dirX and dirY.
     */
    private void beginProfile(Vec origin, Vec dirX, Vec dirY, Vec2d from) {
        savedFace = face
        savedSurface = currentSurface
        savedMakeWires = new ArrayList<>(makeWires)
        savedFromVec2d = fromVec2d
        makeWires.clear()

        def ax3 = own(new_gp_Ax3__p_dN_dX(gpPnt(origin), gpDir(dirX / dirY), gpDir(dirX)), NativeLib::delete_gp_Ax3)
        def pln = own(new_gp_Pln__gp_Ax3(ax3), NativeLib::delete_gp_Pln)
        face = own(new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(pln), NativeLib::delete_TopoDS_Face)
        visitFrom(from)
    }

    /**
     * End the profile started by beginProfile, restore the drawing state and return the profile wire (with 3D curves).
     */
    private MemorySegment endProfile(String what, Vec2d from) {
        visitFromEnd(from)
        if (makeWires.empty) throw new IllegalStateException("$what: nothing drawn")
        def profile = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(makeWires.first())
        _BRepLib__BuildCurves3d__TopoDS_Shape(profile)

        makeWires.clear()
        makeWires.addAll(savedMakeWires)
        face = savedFace
        currentSurface = savedSurface
        fromVec2d = savedFromVec2d
        profile
    }

    @Override
    void visitHelixProfile(Vec2d from) {
        Tr.ind "visitHelixProfile $from"
        // Axial section plane through the start of the helix: x radial, y along the axis
        beginProfile(helixStart, helixRadial, helixAxis, from)
    }

    @Override
    void visitHelixProfileEnd(Vec2d from) {
        def profile = endProfile("helix profile", from)
        def shape = own(new_TopoDS_Shape__BRepOffsetAPI_MakePipeShell__spine_profile_binormal(helixSpine, profile, gpDir(helixAxis)), NativeLib::delete_TopoDS_Shape)
        if (shape == MemorySegment.NULL) throw new IllegalArgumentException("helix profile: ${lastError()}")

        Tr.dec "visitHelixProfileEnd shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitSpline(List<Vec2d> through) {
        openShape2dList << new Spline2d(through)
    }

    // Section wires of the wing being built, in span order
    private final List<MemorySegment> wingSections = []
    private Vec wingSectionLeadingEdge
    private double wingSectionChord
    private double wingSectionTwist

    @Override
    void visitWing() {
        Tr.ind "visitWing from: $fromVec"
        wingSections.clear()
    }

    @Override
    void visitWingSection(Vec leadingEdge, double chord, double twist, Vec2d from) {
        Tr.cur "visitWingSection $leadingEdge, chord: $chord, twist: $twist"
        wingSectionLeadingEdge = leadingEdge
        wingSectionChord = chord
        wingSectionTwist = twist
        // Unit chord profile on the XZ plane, placed afterward
        beginProfile(new Vec(), Vec.vX, Vec.vZ, from)
    }

    @Override
    void visitWingSectionEnd(Vec2d from) {
        def profile = endProfile("wing section", from)

        // Scale to the chord, twist around the quarter chord (nose up), then move to the leading edge
        def scale = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
        _gp_Trsf__SetScale__gp_Pnt_s(scale, gpPnt(new Vec()), wingSectionChord)
        def twistAxis = own(new_gp_Ax1__p_dir(gpPnt(new Vec(wingSectionChord / 4, 0, 0)), gpDir(Vec.vY)), NativeLib::delete_gp_Ax1)
        def twist = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
        _gp_Trsf__SetRotation__gp_Vec(twist, twistAxis, Math.toRadians(wingSectionTwist))
        def placement = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
        _gp_Trsf__SetTranslation__gp_Vec(placement, gpVec(fromVec + wingSectionLeadingEdge))
        _gp_Trsf__Multiply__gp_Trsf(placement, twist)
        _gp_Trsf__Multiply__gp_Trsf(placement, scale)

        def section = own(new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(profile, placement, 1), NativeLib::delete_TopoDS_Shape)
        if (section == MemorySegment.NULL) throw new IllegalArgumentException("wing section: ${lastError()}")
        wingSections << section
    }

    @Override
    void visitWingEnd() {
        if (wingSections.size() < 2) throw new IllegalStateException("wing: at least 2 sections expected, got ${wingSections.size()}")
        def loft = own(new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(1, 0, 1.0e-06d), NativeLib::delete_BRepOffsetAPI_ThruSections)
        for (MemorySegment section in wingSections) {
            _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(loft, ref_TopoDS__Wire__TopoDS_Shape(section))
        }
        def shape = own(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(loft), NativeLib::delete_TopoDS_Shape)
        if (shape == MemorySegment.NULL) throw new IllegalArgumentException("wing: ${lastError()}")
        wingSections.clear()

        Tr.dec "visitWingEnd shape: $shape"
        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    private static final int PLATE_G0 = 0
    // The approximation may deviate from the plate surface by this many times its G0 error
    private static final double PLATE_DEVIATION_PER_G0_ERROR = 10

    // Plate being built: its constraints are drawn relative to plateOrigin, its face is trimmed by plateBoundary
    private MemorySegment plateBuilder
    private PlateOptions plateOptions
    private Vec plateOrigin
    private MemorySegment plateBoundary
    private boolean plateDrawingBoundary

    @Override
    void visitPlate(PlateOptions options) {
        Tr.ind "visitPlate from: $fromVec, $options"
        plateOptions = options
        plateOrigin = fromVec
        plateBoundary = null
        plateBuilder = own(new_GeomPlate_BuildPlateSurface__degree_NbPt_NbIter(options.degree, options.pointsOnCurve, options.iterations), NativeLib::delete_GeomPlate_BuildPlateSurface)
    }

    @Override
    void visitPlateCurve(Vec from, boolean boundary) {
        Tr.ind "visitPlateCurve from: $from, boundary: $boundary"
        if (boundary && plateBoundary) throw new IllegalStateException("plate: a single boundary expected")
        plateDrawingBoundary = boundary
        fromVecStack << from + plateOrigin
    }

    @Override
    void visitPlateCurveEnd(Vec from) {
        if (openShapeList.empty) throw new IllegalStateException("plate curve from $from: nothing drawn")
        def wire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(makeWire3d())
        fromVecStack.pop()
        if (plateDrawingBoundary) plateBoundary = wire

        def explorer = own(new_BRepTools_WireExplorer(), NativeLib::delete_BRepTools_WireExplorer)
        for (_BRepTools_WireExplorer__Init__TopoDS_Wire(explorer, wire); b_BRepTools_WireExplorer__More(explorer) != 0; _BRepTools_WireExplorer__Next(explorer)) {
            def edge = own(_BRepTools_WireExplorer__Current(explorer), NativeLib::delete_TopoDS_Edge)
            def curve = own(handle_BRepAdaptor_Curve(), NativeLib::delete_handle_BRepAdaptor_Curve)
            _BRepAdaptor_Curve__Initialize(curve, edge)
            def constraint = own(handle_BRepFill_CurveConstraint__Adaptor3d_Curve_Tang(curve, PLATE_G0), NativeLib::delete_handle_BRepFill_CurveConstraint)
            _GeomPlate_BuildPlateSurface__Add__BRepFill_CurveConstraint(plateBuilder, constraint)
        }
        Tr.dec "visitPlateCurveEnd"
    }

    @Override
    void visitPlateThrough(Vec point) {
        Tr.cur "visitPlateThrough $point"
        def constraint = own(handle_GeomPlate_PointConstraint__gp_Pnt_order(gpPnt(point + plateOrigin), PLATE_G0), NativeLib::delete_handle_GeomPlate_PointConstraint)
        _GeomPlate_BuildPlateSurface__Add__Cont(plateBuilder, constraint)
    }

    @Override
    void visitPlateEnd() {
        if (!plateBoundary) throw new IllegalStateException("plate: boundary expected")
        _GeomPlate_BuildPlateSurface__Perform(plateBuilder)
        def plateSurface = own(handle_GeomPlate_Surface__GeomPlate_BuildPlateSurface__Surface(plateBuilder), NativeLib::delete_handle_GeomPlate_Surface)
        if (plateSurface == MemorySegment.NULL) throw new IllegalArgumentException("plate: ${lastError()}")

        double maxDeviation = Math.max(plateOptions.tolerance, PLATE_DEVIATION_PER_G0_ERROR * r_GeomPlate_BuildPlateSurface__G0Error(plateBuilder))
        def approx = own(new_GeomPlate_MakeApprox__SurfPlate_Tol3d_Nbmax_dgmax_dmax_CritOrder(plateSurface, plateOptions.tolerance, plateOptions.maxSegments, plateOptions.maxDegree, maxDeviation, PLATE_G0), NativeLib::delete_GeomPlate_MakeApprox)
        def surface = own(handle_Geom_Surface__GeomPlate_MakeApprox__Surface(approx), NativeLib::delete_handle_Geom_BSplineSurface)
        if (surface == MemorySegment.NULL) throw new IllegalArgumentException("plate approximation: ${lastError()}")

        // The boundary lies on the approximation within its deviation
        face = own(new_TopoDS_Face__ShapeFix_Face__surface_wire_precision_maxTol(surface, plateBoundary, plateOptions.tolerance, maxDeviation), NativeLib::delete_TopoDS_Face)
        if (face == MemorySegment.NULL) throw new IllegalArgumentException("plate face: ${lastError()}")
        makeWires.clear()
        plateBuilder = null
        plateBoundary = null
        Tr.dec "visitPlateEnd face: $face"
    }

    // Index in boolShapes.peek() of the first shape of each pattern being built
    private final Stack<Integer> patternStarts = new Stack<>()

    @Override
    void visitCircularPattern() {
        Tr.ind "visitCircularPattern"
        patternStarts.push(boolShapes.peek().size())
    }

    @Override
    void visitCircularPatternEnd(int count, Vec axis) {
        List<MemorySegment> shapes = boolShapes.peek()
        List<MemorySegment> pattern = new ArrayList<>(shapes.subList(patternStarts.pop(), shapes.size()))
        def ax1 = own(new_gp_Ax1__p_dir(gpPnt(fromVec), gpDir(axis)), NativeLib::delete_gp_Ax1)
        for (int i = 1; i < count; i++) {
            def rotation = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
            _gp_Trsf__SetRotation__gp_Vec(rotation, ax1, 2 * Math.PI * i / count)
            for (MemorySegment s in pattern) {
                shapes << own(new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(s, rotation, 1), NativeLib::delete_TopoDS_Shape)
            }
        }
        Tr.dec "visitCircularPatternEnd $count x ${pattern.size()} shape(s) around $axis"
    }

    private static Vec unit(Vec v) {
        v * (1 / Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z))
    }

    @Override
    void visitThruSection() {
        Tr.ind("visitThruSection")
        sectionWires.clear()
    }

    @Override
    void visitThruSectionEnd() {
        Tr.dec("visitThruSectionEnd sectionWires: $sectionWires")
        if (sectionWires.empty) return
        def thruSections = own(new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(1, 0, THRU_SECTIONS_PRECISION), NativeLib::delete_BRepOffsetAPI_ThruSections)
        for (MemorySegment wire in sectionWires) {
            _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(thruSections, wire)
        }
        _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(thruSections, 0)
        def shape = own(new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(thruSections), NativeLib::delete_TopoDS_Shape)
        sectionWires.clear()

        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
    }

    @Override
    void visitFace(Vec direction, Vec position) {
        Tr.cur("visitFace $direction $position")
        double positionMax = position == null ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY
        this.direction = direction
        Vec fromVecToPush = null

        for (def aFaceExplorer = own(new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(shape, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal()), NativeLib::delete_TopExp_Explorer);
             _TopExp_Explorer__More(aFaceExplorer);
             _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = own(new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer), NativeLib::delete_TopoDS_Face)
            def aSurface = own(handle_Geom_Surface__TopoDS_Face(aFace), NativeLib::delete_handle_Geom_Surface)

            SurfaceBounds sBounds = new SurfaceBounds(aSurface)
            Tr.cur("sBounds $sBounds")
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
                double aZ
                if (position == null)  {
//                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
//                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
                    MemorySegment aPnt = own(new_gp_Pnt__CentreOfMass__TopoDS_Shape(aFace), NativeLib::delete_gp_Pnt)
                    fromVecToPush = vec(aPnt)
                    aZ = fromVecToPush.cord(direction)
//                fromVecStack.pop()

                } else {
                    def ax1 = own(new_gp_Ax1__p_dir(gpPnt(position), gpDir(direction)), NativeLib::delete_gp_Ax1)
                    def line = own(handle_Geom_Line__ax1(ax1), NativeLib::delete_handle_Geom_Line)
                    def segment = own(new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(own(handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(line, 0d, 0.2d), NativeLib::delete_handle_Geom_TrimmedCurve)), NativeLib::delete_TopoDS_Edge)
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
//                                fromVecToPush = vec(aPnt)
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
                    def pt = own(gp_Pnt__Geom_Surface__Value(aSurface, 1d, 1d), NativeLib::delete_gp_Pnt)
                    ptParam11 = vec(pt)
                    pt = own(gp_Pnt__Geom_Surface__Value(aSurface, 0d, 0d), NativeLib::delete_gp_Pnt)
                    ptParam00 = vec(pt)
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
                MemorySegment s = shape ?: face
                if (fileName.endsWith(".step")) {
                    write_step(s, t)
                } else if (fileName.endsWith(".stl")) {
                    write_stl(s, t)
                } else if (fileName.endsWith(CadDisplay.GLTF_EXTENSION)) {
                    if (!write_glb(s, t)) throw new IllegalStateException("display($fileName): ${lastError()}")
                } else
                    dumpShape(s, 1920, 1080, t)
            }
        } else {
            visualize(resultShape() ?: ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative()))
        }

    }

    /**
     * @return The shape built so far (the last solid, or the current face), null if none
     */
    MemorySegment resultShape() {
        MemorySegment s = !boolShapes.empty() && !boolShapes.peek().empty ? boolShapes.peek().last() : shape
        s ?: face
    }

    @Override
    void visitCenter() {
        fromVecStack << vec(own(new_gp_Pnt__CentreOfMass__TopoDS_Shape(face), NativeLib::delete_gp_Pnt))
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
    void visitText(String content, double size, TextOptions options) {
        if (!face) face = own(new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gpPln(direction, 0)), NativeLib::delete_TopoDS_Face)
        Vec2d pos = fromVec2d ?: new Vec2d()
        addText(content, size, options, "at $pos") { MemorySegment text, MemorySegment font ->
            new_TopoDS_Shape__StdPrs_BRepTextBuilder__Perform__face_u_v(face, pos.x, pos.y, Math.toRadians(options.angle),
                    text, font, size, options.bold ? 1 : 0, options.italic ? 1 : 0, options.align.ordinal(), options.valign.ordinal())
        }
    }

    @Override
    void visitText3d(String content, double size, TextOptions options) {
        Vec pos = openShapeList ? openShapeList.last().to : fromVec
        addText(content, size, options, "at $pos") { MemorySegment text, MemorySegment font ->
            new_TopoDS_Shape__StdPrs_BRepTextBuilder__Perform__pen(gpPnt(pos), gpDir(options.normal), gpDir(options.direction),
                    text, font, size, options.bold ? 1 : 0, options.italic ? 1 : 0, options.align.ordinal(), options.valign.ordinal())
        }
    }

    @Override
    void visitSvg(List<SvgContour> contours, SvgOptions options) {
        if (!face) face = own(new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gpPln(direction, 0)), NativeLib::delete_TopoDS_Face)
        Vec2d pos = fromVec2d ?: new Vec2d()
        addSvg(contours, "at $pos") { MemorySegment data, int size ->
            new_TopoDS_Shape__BRepBuilderAPI_MakeWire__contours_face_u_v(face, pos.x, pos.y, Math.toRadians(options.angle), data, size)
        }
    }

    @Override
    void visitSvg3d(List<SvgContour> contours, SvgOptions options) {
        Vec pos = openShapeList ? openShapeList.last().to : fromVec
        addSvg(contours, "at $pos") { MemorySegment data, int size ->
            new_TopoDS_Shape__BRepBuilderAPI_MakeWire__contours_pen(gpPnt(pos), gpDir(options.normal), gpDir(options.direction), data, size)
        }
    }

    private void addSvg(List<SvgContour> contours, String where, Closure<MemorySegment> build) {
        double[] data = encode(contours)
        MemorySegment wires
        try (Arena arena = Arena.ofConfined()) {
            wires = own(build(arena.allocateFrom(ValueLayout.JAVA_DOUBLE, data), data.length), NativeLib::delete_TopoDS_Shape)
        }
        if (wires == MemorySegment.NULL) throw new IllegalArgumentException("svg: ${lastError()}")
        Tr.cur "svg, ${contours.size()} contours $where"
        drawnShapes << wires
    }

    // see new_TopoDS_Shape__BRepBuilderAPI_MakeWire__contours_pen
    private static double[] encode(List<SvgContour> contours) {
        List<Double> data = [contours.size() as double]
        for (SvgContour c in contours) {
            data.addAll([c.segments.size() as double, c.start.x, c.start.y])
            for (SvgContour.Segment s in c.segments) {
                if (s instanceof SvgContour.Line) data.addAll([1d, s.to.x, s.to.y])
                else if (s instanceof SvgContour.Arc) data.addAll([2d, s.via.x, s.via.y, s.to.x, s.to.y])
                else if (s instanceof SvgContour.Cubic) data.addAll([3d, s.control1.x, s.control1.y, s.control2.x, s.control2.y, s.to.x, s.to.y])
            }
        }
        data as double[]
    }

    private void addText(String content, double size, TextOptions options, String where, Closure<MemorySegment> build) {
        MemorySegment text
        try (Arena arena = Arena.ofConfined()) {
            text = own(build(arena.allocateFrom(content), arena.allocateFrom(options.font)), NativeLib::delete_TopoDS_Shape)
        }
        if (text == MemorySegment.NULL) throw new IllegalArgumentException("text('$content', $size, $options): ${lastError()}")
        Tr.cur "text('$content', $size) $where, $options"
        drawnShapes << text
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
        contoursToFace()
    }

    @Override
    void visitToFaceFrom2d() {
        contoursToFace()
    }

    /**
     * The face bounded by the wires, texts and SVG drawings of the sketch, a face per outer contour if they are several
     */
    private void contoursToFace() {
        Tr.cur("toFace: makeWires: $makeWires, drawnShapes: $drawnShapes")
        if (makeWires) {
            MemorySegment wire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(makeWires.first())
            face = own(new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wire), NativeLib::delete_TopoDS_Face)
            def builder = own(new_BRep_Builder(), NativeLib::delete_BRep_Builder)
            for (MemorySegment w in makeWires.drop(1)) {
                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, face, ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(w))
            }
        }
        if (makeWires.size() > 1 || drawnShapes) {
            // the face has all the wires as holes of the first one, the texts and drawings are apart: a face per
            // outer wire instead, with the wires inside it as holes
            MemorySegment contours = makeWires ? face : MemorySegment.NULL
            if (drawnShapes) {
                def builder = own(new_BRep_Builder(), NativeLib::delete_BRep_Builder)
                contours = own(new_TopoDS_Compound(), NativeLib::delete_TopoDS_Compound)
                _TopoDS_Builder__MakeCompound__TopoDS_Compound(builder, contours)
                if (makeWires) _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, contours, face)
                for (MemorySegment t in drawnShapes) _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, contours, t)
                drawnShapes.clear()
            }
            face = own(new_TopoDS_Shape__BRepBuilderAPI_MakeFace__nested_wires(contours), NativeLib::delete_TopoDS_Shape)
            if (face == MemorySegment.NULL) throw new IllegalStateException("toFace: ${lastError()}")
        }
    }

    @Override
    void visitRevolution(Vec from, Vec dir, double angle) {
        def ax1 = own(new_gp_Ax1__p_dir(gpPnt(from), gpDir(dir)), NativeLib::delete_gp_Ax1)
        def shape = angle == 0 ? own(new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(face, ax1), NativeLib::delete_TopoDS_Shape) : own(new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(face, ax1, angle), NativeLib::delete_TopoDS_Shape)

        MemorySegment trsf = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
        _gp_Trsf__SetTranslation__gp_Vec(trsf, gpVec(fromVec))
        shape = own(new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(shape, trsf, 0), NativeLib::delete_TopoDS_Shape)

        Tr.cur "revol($from, $dir) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"

        boolShapes.peek() << shape
        if (!this.shape) this.shape = shape
        // the face and its wires are consumed, as by a prism
        face = null
        makeWires.clear()
    }

    @Override
    void visitPrism(Vec dir, boolean cut) {
        def shape = own(new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(face, gpVec(dir)), NativeLib::delete_TopoDS_Shape)
        Tr.cur "prism($dir) from: $fromVec, direction: $direction, directionNormal: $directionNormal, shape: $shape"
        addPrism(shape, cut)
    }

    @Override
    void visitPrism(Vec dir, boolean cut, double draft) {
        def shape = own(new_TopoDS_Shape__LocOpe_DPrism__TopoDS_Shape_gp_Vec_draft(face, gpVec(dir), draft), NativeLib::delete_TopoDS_Shape)
        if (shape == MemorySegment.NULL) throw new IllegalArgumentException("prism($dir, draft: $draft): ${lastError()}")
        Tr.cur "prism($dir, draft: $draft) from: $fromVec, shape: $shape"
        addPrism(shape, cut)
    }

    @Override
    void visitPrism(double high, boolean cut, double draft) {
        visitPrism(direction * high, cut, draft)
    }

    // Fuses the prism to the current solid, or cuts it
    private void addPrism(MemorySegment shape, boolean cut) {
        if (this.shape) {
            Tr.cur "cut: $cut"
            if (cut) {
                shape = own(new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(this.shape, shape), NativeLib::delete_TopoDS_Shape)
            } else {
                def los = own(new_TopTools_ListOfShape(), NativeLib::delete_TopTools_ListOfShape)
                _TopTools_ListOfShape__Append__TopoDS_Shape(los, shape)
                shape = own(new_TopoDS_Shape__brep_algoapi_fuse__s1_listrOfShape(this.shape, los), NativeLib::delete_TopoDS_Shape)
            }
            // the result replaces the solid it is made from, which stays the last solid
            boolShapes.peek().remove(this.shape)
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
        MemorySegment ret = wireNative ?: own(new_BRepBuilderAPI_MakeWire(), NativeLib::delete_BRepBuilderAPI_MakeWire)
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
