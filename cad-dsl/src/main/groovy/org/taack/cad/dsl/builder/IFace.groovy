package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec

interface IFace {
    IFace make(IWire wire)
    ISolid prism(IPrismOp<Vec> prismOp)

    /**
     * Handle(Geom_Surface) aSurface = BRep_Tool::Surface(aFace);
     *
     * @return aSurface->DynamicType() == STANDARD_TYPE(Geom_Plane)
     */
    boolean isPlane()

    /**
     *
     * @return Handle(Geom_Plane) aPlane = Handle(Geom_Plane)::DownCast(aSurface);
     */
    IPlane toPlane()
}