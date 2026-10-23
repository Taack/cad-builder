package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.kernel.CadKernels

/**
 * Assembly of parts and sub-assemblies: {@link #part} declares a part, {@link #place} puts an occurrence of it in the
 * assembly, and {@link #assembly} declares a sub-assembly, placed like a part.
 * Start one with {@link CadDsl#assembly}.
 */
@CompileStatic
class CadDslAssembly extends CadDslPart {
    final ICadDslAssemblyVisitor visitor
    // Once built, an assembly can be placed but no longer changed, so that it cannot contain itself
    private boolean built = false

    private CadDslAssembly(String name, ICadDslAssemblyVisitor visitor) {
        super(name, visitor, visitor)
        this.visitor = visitor
    }

    static CadDslAssembly build(String name, @DelegatesTo(value = CadDslAssembly, strategy = Closure.DELEGATE_FIRST) Closure c) {
        CadDslAssembly assembly = new CadDslAssembly(name, CadKernels.get().newAssemblyVisitor(name))
        c.delegate = assembly
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.call()
        assembly.built = true
        assembly
    }

    /**
     * Declare a part made of the solid built in the closure, as a new model. It is not in the assembly until placed.
     * @param name  Part name, kept in STEP files
     * @param color Part color, null for the default one
     */
    CadDslPart part(String name = null, Color color = null, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c) {
        CadDsl solid = CadDsl.cd()
        c.delegate = solid
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.call()
        part(solid, name, color)
    }

    CadDslPart part(Color color, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c) {
        part(null, color, c)
    }

    /**
     * Declare a part made of a solid already built
     */
    CadDslPart part(CadDslSolid solid, String name = null, Color color = null) {
        new CadDslPart(name, visitor.visitPart(name, color, solid.visitor), visitor)
    }

    /**
     * Declare a sub-assembly. It is not in this assembly until placed.
     */
    CadDslAssembly assembly(String name = null, @DelegatesTo(value = CadDslAssembly, strategy = Closure.DELEGATE_FIRST) Closure c) {
        build(name, c)
    }

    /**
     * Put an occurrence of part (or of an assembly) in this assembly
     * @param c Position from the part coordinates: mate and moves, see {@link CadDslPlacement}
     */
    CadDslInstance place(CadDslPart part, @DelegatesTo(value = CadDslPlacement, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        if (built) throw new IllegalStateException("Assembly ${name ?: ''} is built, place parts inside its closure")
        if (part instanceof CadDslAssembly && !((CadDslAssembly) part).built)
            throw new IllegalArgumentException("Assembly ${part.name ?: ''} is still being built, it cannot be placed")
        CadDslPlacement placement = new CadDslPlacement(part, this)
        if (c) {
            c.delegate = placement
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c.call()
        }
        visitor.visitPlace(part.node, placement.location)
        new CadDslInstance(part, this, placement.location)
    }

    /**
     * Shortcut for place(part(color, c))
     */
    CadDslInstance body(Color color = null, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c) {
        place(part(null, color, c))
    }

    /**
     * Shortcut for place(part(solid, null, color))
     */
    CadDslInstance body(CadDslSolid solid, Color color = null) {
        place(part(solid, null, color))
    }

    CadDslAssembly display(String fileName = null) {
        visitor.display(fileName)
        this
    }
}
