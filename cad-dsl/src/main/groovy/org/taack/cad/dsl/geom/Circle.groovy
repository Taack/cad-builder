package org.taack.cad.dsl.geom


import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib._BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge
import static org.taack.occt.NativeLib.handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d
import static org.taack.occt.NativeLib.new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface
import static org.taack.occt.NativeLib.new_BRepBuilderAPI_MakeWire
import static org.taack.occt.NativeLib.new_gp_Ax2d__pt_dir
import static org.taack.occt.NativeLib.new_gp_Circ2d__ax2d_r
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

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
        def MW = own(new_BRepBuilderAPI_MakeWire(), NativeLib::delete_BRepBuilderAPI_MakeWire)
        def c = own(new_gp_Circ2d__ax2d_r(own(new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), pos.toGpDir2d()), NativeLib::delete_gp_Ax2d), radius), NativeLib::delete_gp_Circ2d)
        def aline = own(handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c), NativeLib::delete_handle_Geom2d_Circle)
        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, own(new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(aline, surf), NativeLib::delete_BRepBuilderAPI_MakeEdge))
        return MW
    }
}
