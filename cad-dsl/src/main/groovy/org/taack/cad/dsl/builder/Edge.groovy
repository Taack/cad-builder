package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class Edge extends Vertice implements Selector {

    private List<Vec> edges = []
    private List<Vec> arcCenter = []
    private List<Integer> arcIndex = []

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

    /**
     * Add an Arc to the current wire
     * @param toPosition
     * @param radius
     * @return
     */
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
        def wireNative = new_BRepBuilderAPI_MakeWire()

        int index = 0
        Iterator<Integer> arcIndexIt = arcIndex.size() > 0 ? arcIndex.iterator() : null
        Iterator<Vec> arcCenterIt = arcCenter.size() > 0 ? arcCenter.iterator() : null
        Integer arcIndexCur = arcIndex.size() > 0 ? arcIndexIt.next() : null

        println "arcIndexCur: $arcIndexCur, ${arcIndex}"

        for (Vec to : edges) {
            if (arcIndexCur != null && index == arcIndexCur) {
                def arcCenter = arcCenterIt.next()
                arcIndexCur = arcIndexIt.hasNext() ? arcIndexIt.next() : 0
                println "Arc from: $fromLocal, to: $to, radius: $arcCenter, arcIndexCur(next): $arcIndexCur"
                def arcNative = handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(fromLocal.toGpPnt(), arcCenter.toGpPnt(), to.toGpPnt())
                def arcEdge = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(arcNative)
                fromLocal = to
                _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(wireNative, arcEdge)
            } else {
                println "Edge from: $fromLocal, to: $to"
                def edgeNative = new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(fromLocal.toGpPnt(), to.toGpPnt())
                fromLocal = to
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(wireNative, edgeNative)
            }
            index++

        }
        wireNatives.add wireNative
        this as CadBuilder
    }

    /**
     * Turns Edges wire into face
     * @return
     */
    CadBuilder toFace(Vec plan = new Vec(1)) {

//        def aFace = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(wireNatives.first)
        def aFace = new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(plan.toGpPln())
        def builder = new_BRep_Builder()

        if (wireNatives.size() > 0) {
//            def fixer = new_ShapeExtend_WireData()
//            wireNatives.eachWithIndex {MemorySegment it, int i ->
//                _ShapeExtend_WireData__Add__TopoDS_Wire(fixer, ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(it), 0)
//            }
//            MemorySegment aWireFixed = util_ShapeFix_Wire__Load__ShapeExtend_WireData(fixer)
//            _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, aFace, aWireFixed)
            wireNatives.eachWithIndex { MemorySegment it, int i ->
//                if (i > 0) {

                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, aFace, ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(it))
//                }
            }
        }
        currentFaceNative = aFace


//        def pXY = plan.toGpPln()
//        def aFace = new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(pXY)
//        def builder = new_BRep_Builder()
//
//        if (wireNatives.size() > 0) {
//            wireNatives.eachWithIndex { MemorySegment it, int i ->
//                _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, aFace, ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(it))
//            }
//        }
//        currentFaceNative = aFace
//        currentFaceNative = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(toShape())
        this as CadBuilder
    }
    /**
     * Center of current face
     * @param operations
     * @return
     */
    CadBuilder center(@DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) Closure operations) {
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
    CadBuilder rect(BigDecimal sx, BigDecimal sy, @DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) Closure operations) {
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
        def toolNative = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape__gp_Trsf_bCopy(other.currentShapeNative, trsf, 1)
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
        def aBRepTrsf = new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(shape, aTrsf)
        def aMirroredShape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aBRepTrsf)
        def mkWire = new_BRepBuilderAPI_MakeWire()
        _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(mkWire, aMirroredShape)
        wireNatives.add(mkWire)
        this as CadBuilder
    }

    CadBuilder mirror2(Vec pt, Vec dir) {
        def ax1 = new_gp_Ax1__p_dir(pt.toGpPnt(), dir.toGpDir())
        def aTrsf = new_gp_Trsf()
        _gp_Trsf__SetMirror__gp_Ax1(aTrsf, ax1)

        List<MemorySegment> toAdd = []
        wireNatives.eachWithIndex { it, i ->
            def shape = ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(it)
            def aBRepTrsf = new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(shape, aTrsf)
            def aMirroredShape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aBRepTrsf)
            def mkWire = new_BRepBuilderAPI_MakeWire()
            _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(mkWire, aMirroredShape)
            toAdd.add(mkWire)
        }
        wireNatives.addAll(toAdd)


        def fixer = new_ShapeExtend_WireData()
        wireNatives.eachWithIndex { MemorySegment it, int i ->
            _ShapeExtend_WireData__Add__TopoDS_Wire(fixer, ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(it), i)
        }
        MemorySegment aWireFixed = util_ShapeFix_Wire__Load__ShapeExtend_WireData(fixer)

        def aFace = new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(new Vec().toGpPln())
        def builder = new_BRep_Builder()

        _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(builder, aFace, aWireFixed)

        this as CadBuilder
    }

}
