package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense
import static org.taack.occt.NativeLib.new_gp_Ax2d__pt_dir

@CompileStatic
class Ellipse2d implements IClosedShape2d {
    final Vec2d pos
    final Vec2d dir
    final double majRadius
    final double minRadius
    final boolean reverse = false

    Ellipse2d(Vec2d pos, Vec2d dir, double majRadius, double minRadius) {
        this.pos = pos
        this.dir = dir
        this.majRadius = majRadius
        this.minRadius = minRadius
    }

    @Override
    MemorySegment make2dCurve() {
//            def MW = new_BRepBuilderAPI_MakeWire()
        def anAx2d = new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), dir.toGpDir2d())
        def ellipse = handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(anAx2d, majRadius.toDouble(), minRadius.toDouble(), 1)
//            _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(ellipse, surf))
        return ellipse
    }
}

