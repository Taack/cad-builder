package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

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
        currentShape = nl.brep_builderapi_transform_shape(currentShape, trsf, 0)
        this
    }

    CadBuilder cut(CadBuilder other) {
        def res = nl.brep_algoapi_cut_ds_shape(currentShape, other.currentShape)
        println "other.currentShape == res = ${other.currentShape == res}"
        currentShape = res
        this
    }

    CadBuilder cut(List<CadBuilder> others) {
        def tools = nl.top_tools_list_of_shape()
        others.each {
            nl.top_tools_list_of_shape_append(tools, it.currentShape)
        }
        def res = nl.brep_algoapi_cut(currentShape, tools)
        currentShape = res
        this
    }

    CadBuilder sphere(BigDecimal radius, Vec dir = new Vec(1.0), BigDecimal angle1 = null, BigDecimal angle2 = null) {
        def ax2 = nl.gp_ax2(loc.toGpPnt(), dir.toGpDir())
        currentShape = nl.brep_builderapi_make_shere(ax2, radius.toDouble(), angle1?.toDouble(), angle2?.toDouble())
        return this
    }

    CadBuilder cylinder(BigDecimal radius, BigDecimal height, Vec dir = new Vec(1.0)) {
        def ax2 = nl.gp_ax2(loc.toGpPnt(), dir.toGpDir())
        currentShape = nl.brep_builderapi_make_cylinder(ax2, radius.toDouble(), height.toDouble())
        return this
    }

    CadBuilder box(BigDecimal x, BigDecimal y, BigDecimal z) {
        currentShape = nl.brep_builderapi_make_shape(nl.brep_primapi_make_box(x.doubleValue(), y.doubleValue(), z.doubleValue()))
        return this
    }

    void display(String fileName = null, int w = 640, int h = 480) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                nl.dumpShape(currentShape, w, h, t)
            }
        } else nl.visualize(currentShape)
    }

    CadBuilder sketch(@DelegatesTo(value = Sketch, strategy = Closure.DELEGATE_FIRST) sketch) {
        this
    }

    CadBuilder isValid() {
        nl.analyze(currentShape)
        this
    }

}
