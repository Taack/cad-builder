package org.taack.cad.dsl.dump.tcltk

import groovy.transform.CompileStatic
import org.taack.cad.dsl.dump.direct.Vec
import org.taack.cad.dsl.dump.direct.Vec2d
import org.taack.cad.dsl.builder.ICad
import org.taack.cad.dsl.builder.IEdge
import org.taack.cad.dsl.builder.IFace
import org.taack.cad.dsl.builder.IProfile
import org.taack.cad.dsl.builder.ISolid
import org.taack.cad.dsl.builder.IWire

@CompileStatic
class CadBuilder implements ICad {

    private List<String> instructions = []
    private Stack<String> names = new Stack<>()

    static CadBuilder cb() {
        new CadBuilder()
    }

    private String pushName() {
        final n = "this${instructions.size()}"
        names.push(n)
        return n
    }

    @Override
    IEdge[] getEdges() {
        return null
    }

    @Override
    IFace[] getFaces() {
        return null
    }

    @Override
    IFace topZ() {
        return null
    }

    @Override
    ISolid fuse(ISolid... solid) {
        return null
    }

    @Override
    ISolid cut(ISolid... solid) {
        return null
    }

    @Override
    ISolid fillet(BigDecimal radius) {
        return null
    }

    @Override
    ISolid fillet(IEdge edge, BigDecimal radius) {
        return null
    }

    @Override
    ISolid makeThickSolidByJoin(BigDecimal thickness, BigDecimal tol, IFace... faceToRemove) {
        return null
    }

    @Override
    ICad cyl(Vec ax, BigDecimal radius, BigDecimal height) {
        return null
    }

    @Override
    ICad box(BigDecimal sx, BigDecimal sy, BigDecimal sz) {
        final n = pushName()
        instructions.add "box $n ${sx} ${sy} ${sz}"
        this
    }

    @Override
    ICad box(Vec s = new Vec(1.0, 1.0, 1.0)) {
        box(s.x, s.y, s.z)
        this
    }

    ICad display() {
        final n = names.join('-')
        instructions.add "vinit $n"
        instructions.add "vdisplay -dispMode 1 ${n.replace('-', ' ')}"
        instructions.add "vfit"

        File out = new File("test.tcl")
        out.delete()
        out.createNewFile()
        out << "pload ALL\n"
        out << instructions.join("\n")
        "./test.sh".execute()
        this
    }

    ICad profile(@DelegatesTo(value = IProfile, strategy = Closure.DELEGATE_ONLY) Closure p) {
        p.delegate = this
        p.call()
        this
    }

    private Vec2d sketchPos
    private String namePos = null

    @Override
    IProfile origin(Vec2d sketchPos = new Vec2d(0.0, 0.0)) {
        this.sketchPos = sketchPos
        if (namePos == null) namePos = names.peek()
        else throw new Exception("Must have called toWire at the end of previous profile")
        this
    }

    @Override
    IProfile lineTo(Vec2d to) {
        final n = pushName()
        instructions.add "vertex p1$n ${sketchPos.x} ${sketchPos.y} 0"
        instructions.add "vertex p2$n ${to.x} ${to.y} 0"
        instructions.add "edge $n p1$n p2$n"
        sketchPos = to
        return null
    }

    @Override
    IProfile threePointArc(Vec2d p2, Vec2d p3) {
        final n = pushName()

        instructions.add "point p1$n ${sketchPos.x} ${sketchPos.y} 0"
        instructions.add "point p2$n ${p2.x} ${p2.y} 0"
        instructions.add "point p3$n ${p3.x} ${p3.y} 0"
        instructions.add "catch {gcarc arc$n cir p1$n p2$n p3$n}"
        instructions.add "mkedge $n arc$n"
        sketchPos = p3
        this
    }

    @Override
    IProfile radiusArc(Vec2d sx, BigDecimal radius) {
        return null
    }

    @Override
    IProfile close() {
        return null
    }

    @Override
    IProfile rect(BigDecimal sx, BigDecimal sy) {
        return null
    }

    @Override
    IProfile circle(BigDecimal radius) {
        return null
    }

    @Override
    IProfile pos(Vec2d pos) {
        return null
    }

    @Override
    IWire toWire() {
        if (namePos) {
            Stack<String> edges = new Stack<>()
            while(names.peek() != namePos) {
                edges.push(names.pop())
            }
            final n = pushName()
            instructions.add "wire $n ${edges.join(' ')}"
        }
        this
    }

    @Override
    IWire mirror(Vec2d pos = new Vec2d(.0, .0), Vec2d dir) {
        final previousWire = names.peek()
        final n = pushName()
        instructions.add "copy $previousWire $n"
        instructions.add "tmirror $n ${pos.x} ${pos.y} ${dir.x} ${dir.y} 0"
        this
    }
}
