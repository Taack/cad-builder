package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Trsf
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
    // Components, in the order given to the kernel
    private final List<CadDslInstance> instances = []
    private double duration = 0
    private double fps = 25

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
     * @return The instances placed in this assembly, in order
     */
    List<CadDslInstance> getInstances() {
        Collections.unmodifiableList(instances)
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
        CadDslInstance instance = new CadDslInstance(part, this, placement)
        visitor.visitPlace(part.node, instance.location)
        instances << instance
        instance
    }

    /**
     * Make the joint of driven follow the one of driver: driven value = ratio * driver value. With revolute joints,
     * for gears of z1 (driver) and z2 teeth: ratio = -z1 / z2; from a revolute to a slider (rack and pinion) of pitch
     * radius r: ratio = PI * r / 180 per degree.
     */
    CadDslAssembly gear(CadDslInstance driver, CadDslInstance driven, Number ratio) {
        if (!driver.joint || !driven.joint) throw new IllegalArgumentException("A gear links instances placed with revolute or slider joints")
        driven.joint.gear(driver.joint, ratio.toDouble())
        this
    }

    /**
     * Animate the assembly: in the closure, drive the joints with instance.angle = { double t -> ... } and
     * instance.distance = { double t -> ... }, t in seconds. display() then plays it, and display('frames/%03d.png')
     * renders each frame; STEP, STL and single images show it at t = 0.
     * @param options duration (seconds, required), fps (frames per second, 25 by default)
     */
    CadDslAssembly animate(Map<String, ?> options, @DelegatesTo(value = CadDslAssembly, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        if (!options.duration) throw new IllegalArgumentException("animate needs a duration in seconds: animate(duration: 4) { ... }")
        duration = (options.duration as Number).toDouble()
        if (options.fps) fps = (options.fps as Number).toDouble()
        if (c) {
            c.delegate = this
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c.call()
        }
        this
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

    /**
     * @param fileName null to open the viewer (see {@link CadDisplay}), a .step, .stl, .glb or image file, or with
     * an animation, an image file name pattern for String.format (e.g. 'frames/%03d.png') to render each frame
     */
    CadDslAssembly display(String fileName = null) {
        int frameCount = duration > 0 ? Math.max(1, Math.round(duration * fps) as int) : 0
        moveAll(frameCount, Collections.newSetFromMap(new IdentityHashMap<CadDslAssembly, Boolean>()))
        visitor.visitAnimation(frameCount, fps)
        visitor.display(CadDisplay.target(fileName))
        this
    }

    // Gives the kernel the locations of the components of this assembly and of its sub-assemblies, each once
    private void moveAll(int frameCount, Set<CadDslAssembly> done) {
        if (!done.add(this)) return
        instances.eachWithIndex { CadDslInstance instance, int i ->
            List<Trsf> frames = frameCount ? (0..<frameCount).collect { int f -> instance.locationAt(f / fps) } : null
            visitor.visitMove(i, instance.location, frames)
            if (instance.part instanceof CadDslAssembly) ((CadDslAssembly) instance.part).moveAll(frameCount, done)
        }
    }
}
