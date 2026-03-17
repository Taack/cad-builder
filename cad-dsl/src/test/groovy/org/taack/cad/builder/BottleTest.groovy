package org.taack.cad.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import java.lang.foreign.MemorySegment

import static org.taack.cad.builder.CadBuilder.*

@CompileStatic
class BottleTest {
    static double myWidth = 50.0
    static double myHeight = 70.0
    static double myThickness = 30.0
    static double myNeckRadius = myThickness / 4
    static double myNeckHeight = myHeight / 10

    @CompileStatic
    static CadBuilder mainBottleBody() {

        Vec v1 = new Vec(-myWidth / 2, 0, 0)
        Vec v11 = new Vec(0, -myThickness / 4, 0)
        Vec v2 = v1 + v11
        Vec v3 = v11 * 2
        Vec v4 = -v1 + v11
        Vec v5 = -v1

        cb().from(v1)
                .edge(v2)
                .arc(v4, v3)
                .edge(v5).toWire().mirror(new Vec(), new Vec(1, 0, 0)).toFace().prism(new Vec(myHeight))
                .topZ().fillet(myThickness / 12d).fuse(cb().cylinder(myNeckRadius, myNeckHeight))
                .topZ().hollowedSolid(-myThickness / 50d)
    }

    static void simpleThreading(CadBuilder mb) {
        MemorySegment innerCyl = cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 0.99)
        MemorySegment outerCyl = cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 1.05)

        double aMajor = 2.0 * Math.PI
        double aMinor = myNeckHeight / 10
        Vec2d pos = new Vec2d(2.0 * Math.PI, myNeckHeight / 2.0d)
        Vec2d dir = new Vec2d(2.0 * Math.PI, myNeckHeight / 4.0d)

        MemorySegment ellipse1 = ellipse2dCurve(pos, dir, aMajor, aMinor)
        MemorySegment ellipse2 = ellipse2dCurve(pos, dir, aMajor, aMinor / 4)

        MemorySegment arc1 = trimmedCurve2d(ellipse1, 0, Math.PI)
        MemorySegment arc2 = trimmedCurve2d(ellipse2, 0, Math.PI)
        MemorySegment seg  = trimmedCurveSegment2d(ellipse1, 0, Math.PI)

        mb
                .threadingWireFrom(edgeFrom(arc1, innerCyl), edgeFrom(seg, innerCyl))
                .threadingWireFrom(edgeFrom(arc2, outerCyl), edgeFrom(seg, outerCyl))
                .applyThreading().display()
    }

    static void otherThreading(CadBuilder mb) {
        MemorySegment innerCyl = cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 0.99)
        Vec dir3d = new Vec(1)
        Vec pos3d = new Vec(myHeight + myNeckHeight / 2)
        MemorySegment curve3d = ellipseCurve(pos3d, dir3d, myNeckRadius * 2.15, myNeckRadius * 1.05)
        MemorySegment outerCyl = linearExtrusionSurface(curve3d, dir3d)
        double aMajor = 4.0 * Math.PI
        double aMinor = myNeckHeight / 10
        Vec2d pos = new Vec2d(2.0 * Math.PI, myNeckHeight / 2.0d)
        Vec2d dir = new Vec2d(2.0 * Math.PI, myNeckHeight / 4.0d)

        MemorySegment ellipse1 = ellipse2dCurve(pos, dir, aMajor, aMinor)
        MemorySegment ellipse2 = ellipse2dCurve(pos, dir, aMajor, aMinor / 4)

        MemorySegment arc1 = trimmedCurve2d(ellipse1, 0, Math.PI)
        MemorySegment seg  = trimmedCurveSegment2d(ellipse1, 0, Math.PI)
        MemorySegment arc2 = trimmedCurve2d(ellipse2, 0, Math.PI)
        mb
                .threadingWireFrom(edgeFrom(arc1, innerCyl), edgeFrom(seg, innerCyl))
                .threadingWireFrom(edgeFrom(arc2, outerCyl), edgeFrom(seg, outerCyl))
                .applyThreading().display()
    }

    static void anotherThreading(CadBuilder mb) {

        double ellipseCurveMajRadius = myNeckRadius * 3.45
        double ellipseCurveMinRadius = myNeckRadius * 2.15

        MemorySegment innerCyl = cylindricalSurface(new Vec(myHeight), new Vec(1), myNeckRadius * 0.99)
        Vec dir3d = new Vec(5)
        Vec pos3d = new Vec(myHeight + myNeckHeight / 2)
//        MemorySegment curve3d = ellipseCurve(pos3d, dir3d/* new Vec(0,1,0)*/, ellipseCurveMajRadius, ellipseCurveMinRadius)
        MemorySegment curve3d = ellipseCurve(pos3d, new Vec(1,0,0), ellipseCurveMajRadius, ellipseCurveMinRadius)
//        MemorySegment curve3d = ellipseCurve(pos3d, new Vec(0,1,0), ellipseCurveMajRadius, ellipseCurveMinRadius)
//        def curve3dTrimmed = trimmedCurve(curve3d, Math.PI / 4, 2 * Math.PI / 4)
        MemorySegment outerCyl = revolutionSurface(curve3d, pos3d, dir3d)

        SurfaceBounds sb = surfaceGetBounds(innerCyl)
        println sb
        sb = surfaceGetBounds(outerCyl)
        println sb

        double aMajor = 2 * Math.PI
        double aMinor = myNeckHeight / 10
        Vec2d pos = new Vec2d(2.0 * Math.PI, myNeckHeight / 2.0d)
        Vec2d dir = new Vec2d(2.0 * Math.PI, myNeckHeight / 4.0d)

        MemorySegment ellipse1 = ellipse2dCurve(pos, dir, aMajor, aMinor)

        MemorySegment arc1 = trimmedCurve2d(ellipse1, 0, Math.PI)
        MemorySegment seg  = trimmedCurveSegment2d(ellipse1, 0, Math.PI)

        double trimmedCurve2Min = 0//Math.PI / 4
        double trimmedCurve2Max = 1.0 * Math.PI - trimmedCurve2Min
        double aMajor2 = 8 * Math.PI
        double aMinor2 = Math.PI / 2
        Vec2d pos2 = new Vec2d(2.0 * Math.PI, aMinor2 / 2.0d)
        Vec2d dir2 = new Vec2d(2.0 * Math.PI, aMinor2 / 4.0d)

        MemorySegment ellipse2 = ellipse2dCurve(pos2, dir2, aMajor2, aMinor2 / 4)
        MemorySegment seg2  = trimmedCurveSegment2d(ellipse2, trimmedCurve2Min, trimmedCurve2Max)
        MemorySegment arc2 = trimmedCurve2d(ellipse2, trimmedCurve2Min, trimmedCurve2Max)
        mb
//                .threadingWireFrom(edgeFrom(arc1, innerCyl), edgeFrom(seg, innerCyl))
//                .threadingWireFrom(edgeFrom(arc2, outerCyl), edgeFrom(seg2, outerCyl))
                .threadingWireFrom(edgeFrom(arc1, innerCyl), edgeFrom(seg, innerCyl))
                .threadingWireFrom(edgeFrom(arc2, outerCyl), edgeFrom(seg2, outerCyl))
//                .applyThreading().display("tutu-${aMajor}-${aMinor}-${ellipseCurveMajRadius}-${ellipseCurveMinRadius}-${trimmedCurve2Min}-${trimmedCurve2Max}.png")
                .applyThreading().display()
    }

    @Test
    void "Make Bottle Using Builder API"() {
        def cb = mainBottleBody()
        simpleThreading(cb)
    }

    @Test
    void "Make Bottle Using Builder API Other Threading"() {
        def cb = mainBottleBody()
        otherThreading(cb)
    }

    @Test
    void "Make Bottle Using Builder API Another Threading"() {
        def cb = mainBottleBody()
        anotherThreading(cb)
    }
}
