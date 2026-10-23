package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Vec

/**
 * Part, or assembly, that can be placed in assemblies, as many times as needed.
 * Make one with {@link CadDslAssembly#part} or {@link CadDslAssembly#assembly}.
 * Its face selectors return connectors in its own coordinates.
 */
@CompileStatic
class CadDslPart implements CadDslFaceSelectors {
    final String name
    final ICadDslNode node
    // Kernel assembly the part was declared in, which answers the face queries
    protected final ICadDslAssemblyVisitor declaredIn

    CadDslPart(String name, ICadDslNode node, ICadDslAssemblyVisitor declaredIn) {
        this.name = name
        this.node = node
        this.declaredIn = declaredIn
    }

    @Override
    CadDslConnector face(Vec direction, Vec position) {
        checkPart()
        Frame f = declaredIn.visitFaceFrame(node, direction, position)
        if (!f) throw new IllegalArgumentException("Part ${name ?: ''} has no planar face facing $direction")
        new CadDslConnector(f, this)
    }

    @Override
    CadDslConnector cylinderAxis(Vec position) {
        checkPart()
        Frame f = declaredIn.visitAxisFrame(node, position)
        if (!f) throw new IllegalArgumentException("Part ${name ?: ''} has no cylindrical face")
        new CadDslConnector(f, this)
    }

    private void checkPart() {
        if (node instanceof ICadDslAssemblyVisitor)
            throw new UnsupportedOperationException("Select the faces of an instance inside assembly ${name ?: ''}, then bring the connector out with at(): axle.at(wheel.topY())")
    }

    @Override
    String toString() {
        "${getClass().simpleName}{name=$name}"
    }
}
