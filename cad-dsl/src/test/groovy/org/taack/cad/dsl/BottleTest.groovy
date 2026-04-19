package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static java.lang.Math.PI
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class BottleTest {
    static double myWidth = 50.0
    static double myHeight = 70.0
    static double myThickness = 30.0
    static double myNeckRadius = myThickness / 4
    static double myNeckHeight = myHeight / 10

    @CompileStatic
    static CadDsl mainBottleBody() {

        Vec2d v1 = new Vec2d(-myWidth / 2, 0)
        Vec2d v11 = new Vec2d(0, -myThickness / 4)
        Vec2d v2 = v1 + v11
        Vec2d v3 = v11 * 2
        Vec2d v4 = -v1 + v11
        Vec2d v5 = -v1
        Vec2d v6 = -v2
        Vec2d v7 = -v3
        Vec2d v8 = -v4

        cd().wireFrom(v1) {
            edge(v2)
            arc(v4, v3)
            edge(v5)
            edge(v6)
            arc(v8, v7)
            edge(v1)
        }.toFace().prism(new Vec(myHeight)).topZ().fillet(myThickness / 12d).fuse {
            cylinder(myNeckRadius, myNeckHeight)
        }.topZ().hollowedSolid(-myThickness / 50d).toCadDsl()
    }


    static CadDsl simpleThreading(CadDsl s) {
        s.thruSection {
            double aMajor = 2 * PI
            double aMinor = myNeckHeight / 10
            Vec2d pos = new Vec2d(2 * PI, myNeckHeight / 2)
            Vec2d dir = new Vec2d(2 * PI, myNeckHeight / 4)

            position(new Vec(myHeight)) {
                direction(new Vec(1))
                cylindricalSurface(myNeckRadius * 0.99)
            }

            wireFromSurface {
                to(pos)
                trimmed(ellipse(dir, aMajor, aMinor), 0, PI)
                to(bound(0))
                edge(bound(PI))
            }
            position(new Vec(myHeight)) {
                direction(new Vec(1))
                cylindricalSurface(myNeckRadius * 1.05)
            }

            wireFromSurface {
                to(pos)
                trimmed(ellipse(dir, aMajor, aMinor), 0, PI)
                to(bound(0))
                edge(bound(PI))
            }
        }.toCadDsl()
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
        CadDsl cd = mainBottleBody()
        simpleThreading(cd).display()
    }

}
