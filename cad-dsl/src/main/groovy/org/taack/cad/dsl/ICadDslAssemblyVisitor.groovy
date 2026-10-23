package org.taack.cad.dsl

import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Builds an assembly; it is itself a node, so that it can be placed in another assembly.
 */
interface ICadDslAssemblyVisitor extends ICadDslNode {
    /**
     * @return A part made of the solid built by solid
     * @param name  null for no name
     * @param color null for the kernel default color
     */
    ICadDslNode visitPart(String name, Color color, ICadDslVisitor solid)

    /**
     * Add an occurrence of node to the assembly
     * @param location From the node coordinates to the assembly ones
     */
    void visitPlace(ICadDslNode node, Trsf location)

    /**
     * @return The frame of the planar face of part whose outward normal is direction: the farthest along it when
     * position is null, else the closest to position, with the origin at position projected on it; null if none.
     * The x direction is left to {@link Frame}.
     */
    Frame visitFaceFrame(ICadDslNode part, Vec direction, Vec position)

    /**
     * @return The axis of the cylindrical face of part: the largest radius when position is null, else the closest to
     * position; z is the axis direction, pointing to +Z (else +Y, else +X), and the origin the start of the face along
     * it, or position projected on the axis; null if none. The x direction is left to {@link Frame}.
     */
    Frame visitAxisFrame(ICadDslNode part, Vec position)

    void display(String fileName)
}
