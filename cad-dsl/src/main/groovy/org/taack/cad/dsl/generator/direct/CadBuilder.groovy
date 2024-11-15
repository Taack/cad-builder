package org.taack.cad.dsl.generator.direct

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl
import org.taack.cad.dsl.Vec

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

/*
 * Todo: realise one move instead of many
 */
@CompileStatic
class CadBuilder extends Face {

    static CadBuilder cb() {
        new CadBuilder()
    }

    private Vec loc = new Vec(0.0, 0.0, 0.0)
    private final MemorySegment trsf = nl.gp_trsf()

    CadBuilder move(Vec p) {
        loc = p
        nl.gp_trsf_set_translation(trsf, loc.toGpVec())
        currentShapeNative = nl.brep_builderapi_transform_shape(currentShapeNative, trsf, 1)
        this
    }

    CadBuilder fuse(CadBuilder other) {
        def res = nl.brep_algoapi_fuse(currentShapeNative, other.currentShapeNative)
        currentShapeNative = res
        this
    }

    CadBuilder cut(List<CadBuilder> others) {
        def tools = nl.top_tools_list_of_shape()
        others.each {
            nl.top_tools_list_of_shape_append(tools, it.currentShapeNative)
        }
        def res = nl.brep_algoapi_cut(currentShapeNative, tools)
        currentShapeNative = res
        this
    }

    CadBuilder sphere(BigDecimal radius, Vec dir = new Vec(1.0), BigDecimal angle1 = null, BigDecimal angle2 = null) {
        def ax2 = nl.gp_ax2(loc.toGpPnt(), dir.toGpDir())
        currentShapeNative = nl.brep_builderapi_make_shere(ax2, radius.toDouble(), angle1?.toDouble(), angle2?.toDouble())
        return this
    }

    CadBuilder cylinder(BigDecimal radius, BigDecimal height, Vec dir = new Vec(1.0)) {
        def ax2 = nl.gp_ax2(loc.toGpPnt(), dir.toGpDir())
        currentShapeNative = nl.brep_builderapi_make_cylinder(ax2, radius.toDouble(), height.toDouble())
        return this
    }

    CadBuilder box(BigDecimal x, BigDecimal y, BigDecimal z) {
        currentShapeNative = nl.brep_builderapi_make_shape(nl.brep_primapi_make_box(x.doubleValue(), y.doubleValue(), z.doubleValue()))
        return this
    }

    CadBuilder torus(BigDecimal outerRadius, BigDecimal innerRadius, Vec dir = new Vec(1.0)) {
        def ax2 = nl.gp_ax2(loc.toGpPnt(), dir.toGpDir())
        currentShapeNative = nl.brep_primapi_make_thorus(ax2, outerRadius.toDouble(), innerRadius.toDouble())
        this
    }


    void display(String fileName = null, int w = 640, int h = 480) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                if (fileName.endsWith(".step")) {
                    nl.write_step(this.currentShapeNative, t)
                } else if (fileName.endsWith(".stl")) {
                    nl.write_stl(this.currentShapeNative, t)
                } else
                nl.dumpShape(currentShapeNative, w, h, t)
            }
        } else nl.visualize(currentShapeNative)
    }

    static Sketch sketch(@DelegatesTo(value = Sketch, strategy = Closure.DELEGATE_ONLY) Closure<Sketch> sketch) {
        sketch.call()
    }

    CadBuilder extrude(Vec vect, Sketch sketch) {
        this
    }

    CadBuilder isValid() {
        nl.analyze(currentShapeNative)
        this
    }

}
