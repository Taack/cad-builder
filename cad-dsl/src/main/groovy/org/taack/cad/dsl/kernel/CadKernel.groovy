package org.taack.cad.dsl.kernel

import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec2d

/**
 * Geometric kernel the DSL runs on.
 *
 * The DSL itself only records what is drawn; a kernel turns it into shapes. Kernels are found with
 * {@link java.util.ServiceLoader} (see {@link CadKernels}), so a model only needs the DSL to compile,
 * and a kernel on the classpath to run.
 */
interface CadKernel {
    /**
     * @return A visitor building a new model
     */
    ICadDslVisitor newVisitor()

    /**
     * @return First point of a trimmed 2D curve
     */
    Vec2d startPoint(ITrimmable2d curve)

    /**
     * @return Last point of a trimmed 2D curve
     */
    Vec2d endPoint(ITrimmable2d curve)

    /**
     * @return Intersection points of two closed 2D curves
     */
    List<Vec2d> intersections(IClosedShape2d c1, IClosedShape2d c2)

    /**
     * @return Centers of the two circles of the given radius passing through p1 and p2
     */
    List<Vec2d> circleCenters(Vec2d p1, Vec2d p2, double radius)
}
