package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.CadDslSurface.*
import static java.lang.Math.*
@CompileStatic
class BottleTest {
    static double myWidth = 50.0
    static double myHeight = 70.0
    static double myThickness = 30.0
    static double myNeckRadius = myThickness / 4
    static double myNeckHeight = myHeight / 10

    @CompileStatic
    static CadDslSolid mainBottleBody() {

        Vec v1 = new Vec(-myWidth / 2, 0, 0)
        Vec v11 = new Vec(0, -myThickness / 4, 0)
        Vec v2 = v1 + v11
        Vec v3 = v11 * 2
        Vec v4 = -v1 + v11
        Vec v5 = -v1

        cd().wireFrom(v1) {
            edge(v2)
            arc(v4, v3)
            edge(v5)
        }.mirror(new Vec(), new Vec(1, 0, 0))
                .toFace()
                .prism(new Vec(myHeight)).topZ().fillet(myThickness / 12d).fuse {
                    cylinder(myNeckRadius, myNeckHeight)
                }.topZ().hollowedSolid(-myThickness / 50d)
    }


    static CadDslSolid simpleThreading(CadDslSolid s) {
        cd().thruSection {
            CadDslEdge2d commonSegment
            wireFromSurface(cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 0.99)) {
                double aMajor = 2 * PI
                double aMinor = myNeckHeight / 10
                Vec2d pos = new Vec2d(2 * PI, myNeckHeight / 2)
                Vec2d dir = new Vec2d(2 * PI, myNeckHeight / 4)
                to(pos)
                def bounds = trimmed(ellipse(dir, aMajor, aMinor), 0, PI)
                commonSegment = edge(bounds.bound(0, PI))
            }
            wireFromSurface(cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 1.05)) {
                double aMajor = 2 * PI
                double aMinor = myNeckHeight / 10
                Vec2d pos = new Vec2d(2 * PI, myNeckHeight / 2)
                Vec2d dir = new Vec2d(2 * PI, myNeckHeight / 4)
                to(pos)
                def bounds = trimmed(ellipse(dir, aMajor, aMinor), 0, PI)
                commonSegment = edge(bounds.bound(0, PI))
            }
        }
    }
//    static void simpleThreading(CadBuilder mb) {
//        MemorySegment innerCyl = cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 0.99)
//        MemorySegment outerCyl = cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 1.05)
//
//        double aMajor = 2.0 * Math.PI
//        double aMinor = myNeckHeight / 10
//        Vec2d pos = new Vec2d(2.0 * Math.PI, myNeckHeight / 2.0d)
//        Vec2d dir = new Vec2d(2.0 * Math.PI, myNeckHeight / 4.0d)
//
//        MemorySegment ellipse1 = ellipse2dCurve(pos, dir, aMajor, aMinor)
//        MemorySegment ellipse2 = ellipse2dCurve(pos, dir, aMajor, aMinor / 4)
//
//        MemorySegment arc1 = trimmedCurve2d(ellipse1, 0, Math.PI)
//        MemorySegment arc2 = trimmedCurve2d(ellipse2, 0, Math.PI)
//        MemorySegment seg  = trimmedCurveSegment2d(ellipse1, 0, Math.PI)
//
//        mb
//                .threadingWireFrom(edgeFrom(arc1, innerCyl), edgeFrom(seg, innerCyl))
//                .threadingWireFrom(edgeFrom(arc2, outerCyl), edgeFrom(seg, outerCyl))
//                .applyThreading().display()
//    }


    @Test
    void "Make Bottle Using Builder API"() {
        def cd = mainBottleBody()
//        cd = simpleThreading(cd)
        cd.display()
    }

}
