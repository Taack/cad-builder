package org.taack.cad.dsl.builder

import org.taack.cad.dsl.dump.direct.Vec

/**
 * Parametric Geometry UV Curve
 */
interface IGeom2d {
    IGeom2d ellipse(Vec ax, BigDecimal maj, BigDecimal min)
}