package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
/*
 * Todo: realise one move instead of many
 */
@CompileStatic
class CadBuilder extends Face {

    static CadBuilder cb() {
        new CadBuilder()
    }

    private final MemorySegment trsf = new_gp_Trsf()

    CadBuilder move(Vec p) {
        currentLoc = p
        _gp_Trsf__SetTranslation__gp_Vec(trsf, currentLoc.toGpVec())
        currentShapeNative = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape__gp_Trsf_bCopy(currentShapeNative, trsf, 1)
        this
    }

    CadBuilder fuse(CadBuilder other) {

        println "currentLoc = $currentLoc"
        def trsf = new_gp_Trsf()
        _gp_Trsf__SetTranslation__gp_Vec(trsf, currentLoc.toGpVec())
        other.currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(other.currentShapeNative, trsf)
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

    CadBuilder box(BigDecimal x, BigDecimal y, BigDecimal z) {
        currentShapeNative = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__x_y_z(x.doubleValue(), y.doubleValue(), z.doubleValue()))
        return this
    }

    CadBuilder torus(BigDecimal outerRadius, BigDecimal innerRadius, Vec dir = new Vec(1.0)) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(currentLoc.toGpPnt(), dir.toGpDir())
        currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, outerRadius.toDouble(), innerRadius.toDouble())
        this
    }


    void display(String fileName = null, int w = 640, int h = 480) {
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
            addCurrentWireNative()
        }
    }
}
