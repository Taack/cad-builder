package org.taack.cad.builder

import groovy.transform.CompileStatic

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

/*
 * Todo: realise one move instead of many
 */

@CompileStatic
class CadBuilder {
    private List<Vec> edges = []
    private List<Vec> arcCenter = []
    private List<Integer> arcIndex = []
    MemorySegment currentShapeNative
    MemorySegment currentFaceNative
    Stack<MemorySegment> wireNatives = new Stack<>()
    List<Vec> clockwiseLoc = []

    MemorySegment addCurrentWireNative(MemorySegment wireNative = null) {
        MemorySegment ret = wireNative ?: new_BRepBuilderAPI_MakeWire()
        if (wireNatives.size() > 0) {
            wireNatives.eachWithIndex { MemorySegment it, int i ->
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(ret, it)
            }
        }
        ret
    }

    MemorySegment toShape() {
        if (currentShapeNative) currentShapeNative
        else if (currentFaceNative) currentFaceNative
        else ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative())
    }

    Vec currentLoc = new Vec()

    static CadBuilder cb() {
        new CadBuilder()
    }

    private final MemorySegment trsf = new_gp_Trsf()

    CadBuilder move(Vec p) {
        currentLoc = p
        _gp_Trsf__SetTranslation__gp_Vec(trsf, currentLoc.toGpVec())
        currentShapeNative = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(currentShapeNative, trsf, 1)
        this
    }

    CadBuilder fuse(CadBuilder other) {

        println "currentLoc = $currentLoc"
        def trsf = new_gp_Trsf()
        _gp_Trsf__SetTranslation__gp_Vec(trsf, currentLoc.toGpVec())
        other.currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(other.currentShapeNative, trsf, 0, 0)
        currentShapeNative = new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(currentShapeNative, other.currentShapeNative)
        this
    }

    CadBuilder cut(List<CadBuilder> others) {
        def tools = new_TopTools_ListOfShape()
        others.each {
            _TopTools_ListOfShape__Append__TopoDS_Shape(tools, it.currentShapeNative)
        }
        def res = new_TopoDS_Shape__BRepAlgoAPI_Cut__TopoDS_Shape_TopTools_ListOfShape(currentShapeNative, tools)
        currentShapeNative = res
        this
    }

    CadBuilder sphere(BigDecimal radius, Vec dir = new Vec(1.0), BigDecimal angle1 = null, BigDecimal angle2 = null) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(currentLoc.toGpPnt(), dir.toGpDir())
        currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(ax2, radius.toDouble(), angle1?.toDouble(), angle2?.toDouble())
        return this
    }

    CadBuilder cylinder(Number radius, Number height, Vec dir = new Vec(1)) {

        println "cyl : ${currentLoc}"

        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(currentLoc.toGpPnt(), dir.toGpDir())
        def MKCylinder = new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(ax2, radius.toDouble(), height.toDouble())
        currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(MKCylinder)

        return this
    }

    CadBuilder box(Number x, Number y, Number z) {
        currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__x_y_z(x.doubleValue(), y.doubleValue(), z.doubleValue()))
        return this
    }

    CadBuilder torus(BigDecimal outerRadius, BigDecimal innerRadius, Vec dir = new Vec(1.0)) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(currentLoc.toGpPnt(), dir.toGpDir())
        currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, outerRadius.toDouble(), innerRadius.toDouble())
        this
    }


    void display(String fileName = null, int w = 1920, int h = 1080) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                if (fileName.endsWith(".step")) {
                    write_step(this.currentShapeNative, t)
                } else if (fileName.endsWith(".stl")) {
                    write_stl(this.currentShapeNative, t)
                } else
                    dumpShape(currentShapeNative, w, h, t)
            }
        } else visualize(currentNative)
    }

    CadBuilder isValid() {
        analyze(currentShapeNative)
        this
    }

    MemorySegment getCurrentNative() {
        if (currentShapeNative) {
            currentShapeNative
        } else if (currentFaceNative) {
            currentFaceNative
        } else {
            ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative())
        }
    }

    CadBuilder topX(@DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        face(new Vec(1, 0, 0), c) as CadBuilder
    }

    CadBuilder topY(@DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        face(new Vec(0, 1, 0), c) as CadBuilder
    }

    CadBuilder topZ(@DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        face(new Vec(1), c) as CadBuilder
    }


    CadBuilder butZ(@DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        face(new Vec(-1), c) as CadBuilder
    }

    CadBuilder face(Vec axe, @DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_ONLY) operations = null) {
        double positionMax = -1

        for (def aFaceExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(currentShapeNative, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             _TopExp_Explorer__More(aFaceExplorer);
             _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer)
            def aSurface = handle_Geom_Surface__TopoDS_Face(aFace)
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
                currentLoc = Vec.fromAPnt(aPnt)
                double aZ = currentLoc.cord(axe)
                if (aZ > positionMax) {
                    positionMax = aZ
                    currentFaceNative = aFace
                }
            }
        }
        this
    }

    CadBuilder revolution(Vec dir = new Vec(0, 1, 0)) {
        def ax1 = new_gp_Ax1__p_dir(currentLoc.toGpPnt(), dir.toGpDir())

        this.currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(currentFaceNative, ax1)
        this as CadBuilder
    }

    CadBuilder prism(Vec dir = new Vec(1)) {
        this.currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(currentFaceNative, dir.toGpVec())
        this.currentFaceNative = null
        this.wireNatives = null
        this as CadBuilder
    }

    /**
     * Initial position of a new wire
     *
     * @param newOrigin
     * @return
     */
    CadBuilder from(Vec2d newOrigin) {
        from(new Vec(newOrigin))
    }

    CadBuilder from(Vec newOrigin) {
        edges = []
        currentLoc = newOrigin
        this as CadBuilder
    }

    /**
     * Add an Edge to the current wire
     * @param toPosition
     * @return
     */
    CadBuilder edge(Vec toPosition) {
        edges.add toPosition
        this as CadBuilder
    }

    CadBuilder edge(Vec2d toPosition) {
        edge(new Vec(toPosition))
    }

    CadBuilder arc(Vec toPosition, Vec center) {
        edges.add toPosition
        arcCenter.add center
        arcIndex.add(edges.size() - 1)
        this as CadBuilder
    }

    CadBuilder arc(Vec2d toPosition, Vec2d center) {
        arc(new Vec(toPosition), new Vec(center))
    }

    CadBuilder toWire() {
        Vec fromLocal = currentLoc
        MemorySegment wireNative = new_BRepBuilderAPI_MakeWire()

        int index = 0
        Iterator<Integer> arcIndexIt = arcIndex.size() > 0 ? arcIndex.iterator() : null
        Iterator<Vec> arcCenterIt = arcCenter.size() > 0 ? arcCenter.iterator() : null
        Integer arcIndexCur = arcIndex.size() > 0 ? arcIndexIt.next() : null

        for (Vec to : edges) {
            MemorySegment trimmedCurve
            if (arcIndexCur != null && index == arcIndexCur) {
                def arcCenter = arcCenterIt.next()
                arcIndexCur = arcIndexIt.hasNext() ? arcIndexIt.next() : 0
                trimmedCurve = handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(fromLocal.toGpPnt(), arcCenter.toGpPnt(), to.toGpPnt())
            } else {
                trimmedCurve = handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(fromLocal.toGpPnt(), to.toGpPnt())
            }
            def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(trimmedCurve)
            _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(wireNative, arcEdge)
            fromLocal = to
            index++

        }
        wireNatives.add(wireNative)
        this as CadBuilder
    }

    /**
     * Turns Edges wire into face
     * @return
     */
    CadBuilder toFace() {
        println "toFace $wireNatives"
        MemorySegment wire = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(wireNatives.first())
        currentFaceNative = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wire)
        if (wireNatives.size() > 1) {
            def builder = new_BRep_Builder()
            for (MemorySegment w in wireNatives[1..wireNatives.size() - 1]) {
                MemorySegment wire2 = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(w)
                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, currentFaceNative, wire2)
            }
        }
        this as CadBuilder
    }

    /**
     * Center of current face
     * @param operations
     * @return
     */
    CadBuilder center(@DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_FIRST) Closure operations) {
        clockwiseLoc = [Vec.fromAPnt(new_gp_Pnt__CentreOfMass__TopoDS_Shape(currentFaceNative))]
        operations.delegate = this
        operations.call()
        this as CadBuilder
    }

    /**
     * Construction Rect. Center corresponds to currentFace center.
     * @param sx Size along x
     * @param sy Size along y
     * @param operations
     * @return Face
     */
    CadBuilder rect(BigDecimal sx, BigDecimal sy, @DelegatesTo(value = CadBuilder, strategy = Closure.DELEGATE_FIRST) Closure operations) {
        def centerOfFace = Vec.fromAPnt(new_gp_Pnt__CentreOfMass__TopoDS_Shape(currentFaceNative))
        def faceDir = Vec.fromADir(new_gp_Dir__Normal__TopoDS_Face(currentFaceNative))
        println "rect ($sx, $sy), faceDir = ${faceDir}, centerOfFace = ${centerOfFace}"
        def gCoords1 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(-sx / 2.0, -sy / 2.0))
        def gCoords2 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(sx / 2.0, -sy / 2.0))
        def gCoords3 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(sx / 2.0, sy / 2.0))
        def gCoords4 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(-sx / 2.0, sy / 2.0))

        clockwiseLoc = [gCoords1, gCoords2, gCoords3, gCoords4]
        println "rect $clockwiseLoc"
        operations.delegate = this
        operations.call()
        this as CadBuilder
    }

    private void holeHelper(Vec loc, MemorySegment dir, BigDecimal diameter, BigDecimal from, BigDecimal to) {
        currentShapeNative = ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_ptFrom_ptTo(currentShapeNative,
                new_gp_Ax1__p_dir(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue(), from.doubleValue(), to.doubleValue())
        this as CadBuilder
    }

    private void holeHelper(Vec loc, MemorySegment dir, BigDecimal diameter, BigDecimal length) {
        currentShapeNative = ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_l(currentShapeNative,
                new_gp_Ax1__p_dir(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue(), length.doubleValue())
        this as CadBuilder
    }

    void hole(BigDecimal diameter, BigDecimal length) {
        def gpDir = new_gp_Dir__Normal__TopoDS_Face(currentFaceNative)
        println "hole $diameter Dir: ${Vec.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Vec.fromADir(gpDir)}"
            holeHelper(it, gpDir, diameter, length)
        }
    }

    void hole(BigDecimal diameter) {
        def gpDir = new_gp_Dir__Normal__TopoDS_Face(currentFaceNative)
        println "hole $diameter Dir: ${Vec.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Vec.fromADir(gpDir)}"
            holeHelper(it, gpDir, diameter, 0.1, 0.0)
        }
    }

    CadBuilder cut(CadBuilder other) {
        MemorySegment trsf = new_gp_Trsf()
        println "cut currentLoc = $currentLoc, other.currentLoc = ${other.currentLoc}"
        _gp_Trsf__SetTranslation__gp_Vec(trsf, (new Vec(0.0) - currentLoc).toGpVec())
        def toolNative = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(other.currentShapeNative, trsf, 1)
        def cutNative = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(currentShapeNative, toolNative)
        currentShapeNative = cutNative
        this as CadBuilder
    }

    CadBuilder mirror(Vec2d pt, Vec2d dir) {
        mirror(new Vec(pt), new Vec(dir))
    }

    CadBuilder mirror(Vec pt, Vec dir) {
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
        wireNatives.pop()
        wireNatives.push(mkWire)
        this as CadBuilder
    }

    CadBuilder fillet(Number radius) {
        def mkFillet = new_BRepFilletAPI_MakeFillet__TopoDS_Shape(currentShapeNative)
        def anEdgeExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(currentFaceNative ?: currentShapeNative, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index)
        while (_TopExp_Explorer__More(anEdgeExplorer)) {
            def anEdge = ref_TopoDS_Edge__TopoDS_Shape(new_TopoDS_Shape__TopExp_Explorer__Current(anEdgeExplorer))
            //Add edge to fillet algorithm
            _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(mkFillet, radius.toDouble(), anEdge)
            _TopExp_Explorer__Next(anEdgeExplorer)
        }

        currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(mkFillet)
        this as CadBuilder
    }

    CadBuilder hollowedSolid(Number thickness) {
        def facesToRemove = new_TopTools_ListOfShape()
        if (currentFaceNative) {
            _TopTools_ListOfShape__Append__TopoDS_Shape(facesToRemove, currentFaceNative)
        }
        def aSolidMaker = new_BRepOffsetAPI_MakeThickSolid()
        _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(aSolidMaker, currentShapeNative, facesToRemove, thickness.toDouble(), 0.001d)

        currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aSolidMaker)
        this as CadBuilder
    }

    static MemorySegment cylindricalSurface(Vec pos, Vec direction, Number radius) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(pos.toGpPnt(), direction.toGpDir())
        handle_Geom_CylindricalSurface__ax2_radius(ax2, radius.toDouble())
    }

    static MemorySegment linearExtrusionSurface(MemorySegment curve, Vec direction) {
        handle_Geom_SurfaceOfLinearExtrusion__Geom_Curve_gp_Dir(curve, direction.toGpDir())
    }

    static MemorySegment revolutionSurface(MemorySegment curve, Vec pos, Vec direction) {
        def ax1 = new_gp_Ax1__p_dir(pos.toGpPnt(), direction.toGpDir())
        handle_Geom_SurfaceOfRevolution__Geom_Curve_gp_Ax1(curve, ax1)
    }

    static SurfaceBounds surfaceGetBounds(MemorySegment surface) {
        new SurfaceBounds(R4_Geom_Surface__Bounds(surface))
    }

    static MemorySegment ellipseCurve(Vec pos, Vec direction, Number majorRadius, Number minorRadius) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(pos.toGpPnt(), direction.toGpDir())
        handle_Geom_Ellipse__gp_Ax2_rM_rm(ax2, majorRadius.toDouble(), minorRadius.toDouble())
    }

    static MemorySegment ellipse2dCurve(Vec2d pos, Vec2d direction, Number majorRadius, Number minorRadius) {
        def anAx2d = new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), direction.toGpDir2d())
        handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(anAx2d, majorRadius.toDouble(), minorRadius.toDouble(), 1)
    }

    static MemorySegment trimmedCurve(MemorySegment geomCurve, Number from, Number to) {
        handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(geomCurve, from.toDouble(), to.doubleValue())
    }

    static MemorySegment trimmedCurve2d(MemorySegment geom2dCurve, Number from, Number to) {
        handle_Geom2d_TrimmedCurve__curve_u1_u2(geom2dCurve, from.toDouble(), to.doubleValue(), 1, 1)
    }

    static MemorySegment trimmedCurveSegment2d(MemorySegment geom2dCurve, Number from, Number to) {
        def p1 = new_gp_Pnt2d__Geom2d_Curve__Value__u(geom2dCurve, 0.0d)
        def p2 = new_gp_Pnt2d__Geom2d_Curve__Value__u(geom2dCurve, Math.PI)
        handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(p1, p2)
    }

    static MemorySegment edgeFrom(MemorySegment curve2d, MemorySegment surface) {
        new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(curve2d, surface)
    }

    List<MemorySegment> threadingWires = []

    CadBuilder threadingWireFrom(MemorySegment... edges) {
        def threadingWire = new_BRepBuilderAPI_MakeWire()
        for (MemorySegment it : edges) {
            _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(threadingWire, it)
        }
        threadingWire = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(threadingWire)
        _BRepLib__BuildCurves3d__TopoDS_Shape(threadingWire)
        threadingWires.add threadingWire
        this as CadBuilder
    }

    CadBuilder applyThreading() {
        if (!threadingWires.empty) {
            def aTool = new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(1, 0, 1.0e-06d)
            threadingWires.each {
                _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(aTool, it)
            }
            _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(aTool, 0)

            def myThreading = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aTool)
            def aRes = new_TopoDS_Compound()
            def aBuilder = new_BRep_Builder()
            _TopoDS_Builder__MakeCompound__TopoDS_Compound(aBuilder, aRes)
            _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(aBuilder, aRes, currentShapeNative)
            _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(aBuilder, aRes, myThreading)
            currentShapeNative = aRes
        }
        this as CadBuilder
    }
}
