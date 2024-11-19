package org.taack.cad.dsl.builder

import org.taack.cad.dsl.dump.direct.Vec

/**
 * Parametric Geometry UV Curve
 */
interface IGeom {
    IGeom cylindricalSurface(Vec ax, BigDecimal radius)
}