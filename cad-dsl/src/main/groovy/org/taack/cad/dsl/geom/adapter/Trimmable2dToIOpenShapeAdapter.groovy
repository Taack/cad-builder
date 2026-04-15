package org.taack.cad.dsl.geom.adapter

import org.taack.cad.dsl.CadDslVisitor
import org.taack.cad.dsl.geom.IOpenShape
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec

import java.lang.foreign.MemorySegment
import static org.taack.occt.NativeLib.*

class Trimmable2dToIOpenShapeAdapter implements IOpenShape {
    private final ITrimmable2d trimmable2d
    private final Vec pos
    private final Vec dirX
    private final Vec dirY

    Trimmable2dToIOpenShapeAdapter(ITrimmable2d trimmable2d, Vec pos, Vec dirX, Vec dirY) {
        this.trimmable2d = trimmable2d
        this.pos = pos
        this.dirX = dirX
        this.dirY = dirY
    }

    @Override
    MemorySegment makeWireAdd(Vec fromLocal) {
        CadDslVisitor.Tr.cur("Trimmable2dToIOpenShapeAdapter from: $fromLocal, to: $to")
//        def plane = new_gp_Pln__gp_Ax3(new_gp_Ax3__p_dN_dX(pos.toGpPnt(), dirX.toGpDir(), dirY.toGpDir()))
        def plane = new_gp_Pln__gp_Ax3(new_gp_Ax3__p_dN_dX(new Vec().toGpPnt(), dirX.toGpDir(), dirY.toGpDir()))
        handle_Geom_Curve__GeomAPI_To3d__Geom2d_Curve_gp_Pln(trimmable2d.trimmedCurve2d(), plane)
    }

    @Override
    Vec getTo() {
        pos + dirX * trimmable2d.to.x + dirY * trimmable2d.to.y
    }


    @Override
    String toString() {
        return "Trimmable2dToIOpenShapeAdapter{" +
                "trimmable2d=" + trimmable2d +
                ", to=" + to +
                '}'
    }
}
