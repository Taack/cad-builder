package org.taack.cad.dsl.geom

import org.taack.cad.builder.Vec

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib._BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge
import static org.taack.occt.NativeLib.handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d
import static org.taack.occt.NativeLib.new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface
import static org.taack.occt.NativeLib.new_BRepBuilderAPI_MakeWire
import static org.taack.occt.NativeLib.new_gp_Ax2d__pt_dir
import static org.taack.occt.NativeLib.new_gp_Circ2d__ax2d_r

class Circle implements IClosedShape {
    final Vec pos
    final double radius
    final boolean reverse = false

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
