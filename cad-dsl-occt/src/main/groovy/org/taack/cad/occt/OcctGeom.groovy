package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.*

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.occt.Natives.own
import static org.taack.cad.occt.Occt.*

/**
 * OCCT curves and edges of the DSL geometry.
 */
@CompileStatic
final class OcctGeom {
    // Trimmed curves are built once: their end points are queried while drawing, and the curve is reused in the wire
    private static final Map<ITrimmable2d, MemorySegment> TRIMMED = Collections.synchronizedMap(new WeakHashMap<ITrimmable2d, MemorySegment>())

    private OcctGeom() {}

    /**
     * @return The 2D curve (handle_Geom2d_*) of an open shape drawn from fromLocal
     */
    static MemorySegment curve2d(IOpenShape2d s, Vec2d fromLocal) {
        switch (s) {
            case Edge2d:
                CadDslVisitor.Tr.cur("Edge2d from: $fromLocal, to: ${s.to}")
                return own(handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(gpPnt2d(fromLocal), gpPnt2d(s.to)), NativeLib::delete_handle_Geom2d_TrimmedCurve)
            case Arc2d:
                Arc2d arc = (Arc2d) s
                CadDslVisitor.Tr.cur("Arc2d from: $fromLocal, to: ${arc.to}")
                return own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(gpPnt2d(fromLocal), gpPnt2d(arc.center), gpPnt2d(arc.to)), NativeLib::delete_handle_Geom2d_TrimmedCurve)
            case Spline2d:
                return spline2d((Spline2d) s, fromLocal)
            case ITrimmable2d:
                return trimmedCurve2d((ITrimmable2d) s)
            default:
                throw new IllegalArgumentException("Unsupported 2D shape: $s")
        }
    }

    /**
     * @return The 2D curve (handle_Geom2d_*) of a closed shape
     */
    static MemorySegment curve2d(IClosedShape2d s) {
        switch (s) {
            case Circle2d:
                Circle2d circle = (Circle2d) s
                def aline = own(handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(gpCirc2d(circle)), NativeLib::delete_handle_Geom2d_Circle)
                if (circle.reverse) _Geom2d_Curve__Reverse(aline)
                return aline
            case Ellipse2d:
                Ellipse2d ellipse = (Ellipse2d) s
                def anAx2d = own(new_gp_Ax2d__pt_dir(gpPnt2d(ellipse.pos), gpDir2d(ellipse.dir)), NativeLib::delete_gp_Ax2d)
                return own(handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(anAx2d, ellipse.majRadius, ellipse.minRadius, 1), NativeLib::delete_handle_Geom2d_Ellipse)
            default:
                throw new IllegalArgumentException("Unsupported 2D closed shape: $s")
        }
    }

    /**
     * @return The trimmed curve (handle_Geom2d_TrimmedCurve) of an arc or a mirrored arc
     */
    static MemorySegment trimmedCurve2d(ITrimmable2d s) {
        MemorySegment trimmed = TRIMMED.get(s)
        if (trimmed == null) {
            trimmed = makeTrimmedCurve2d(s)
            TRIMMED.put(s, trimmed)
        }
        trimmed
    }

    /**
     * @return The edge (BRepBuilderAPI_MakeEdge) of an open 3D shape drawn from fromLocal
     */
    static MemorySegment edge(IOpenShape s, Vec fromLocal) {
        switch (s) {
            case Edge:
                CadDslVisitor.Tr.cur("Edge from: $fromLocal, to: ${s.to}")
                return own(new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(gpPnt(fromLocal), gpPnt(s.to)), NativeLib::delete_BRepBuilderAPI_MakeEdge)
            case Arc:
                Arc arc = (Arc) s
                def curve = own(handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(gpPnt(fromLocal), gpPnt(arc.center), gpPnt(arc.to)), NativeLib::delete_handle_Geom_TrimmedCurve)
                return own(new_BRepBuilderAPI_MakeEdge__Geom_Curve(curve), NativeLib::delete_BRepBuilderAPI_MakeEdge)
            case Trimmable2dToIOpenShapeAdapter:
                return ((Trimmable2dToIOpenShapeAdapter) s).makeWireAdd(fromLocal)
            default:
                throw new IllegalArgumentException("Unsupported 3D shape: $s")
        }
    }

    private static MemorySegment gpCirc2d(Circle2d circle) {
        own(new_gp_Circ2d__ax2d_r(own(new_gp_Ax2d__pt_dir(gpPnt2d(circle.pos), gpDir2d(new Vec2d(1, 0))), NativeLib::delete_gp_Ax2d), circle.radius), NativeLib::delete_gp_Circ2d)
    }

    private static MemorySegment spline2d(Spline2d s, Vec2d fromLocal) {
        CadDslVisitor.Tr.cur("Spline2d from: $fromLocal, through: ${s.through}")
        List<Vec2d> points = [fromLocal] + s.through
        double[] xy = new double[points.size() * 2]
        points.eachWithIndex { Vec2d p, int i ->
            xy[2 * i] = p.x
            xy[2 * i + 1] = p.y
        }
        try (Arena arena = Arena.ofConfined()) {
            return own(handle_Geom2d_Curve__Geom2dAPI_Interpolate__xy_n(arena.allocateFrom(ValueLayout.JAVA_DOUBLE, xy), points.size()), NativeLib::delete_handle_Geom2d_Curve)
        }
    }

    private static MemorySegment makeTrimmedCurve2d(ITrimmable2d s) {
        MemorySegment trimmed
        switch (s) {
            case ArcOfCircle2d:
                ArcOfCircle2d arc = (ArcOfCircle2d) s
                MemorySegment c = gpCirc2d(arc.circle2d)
                if (arc.angle1 != arc.angle2) trimmed = own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(c, arc.angle1, arc.angle2), NativeLib::delete_handle_Geom2d_TrimmedCurve)
                else if (arc.ptTo == null) trimmed = own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(c, gpPnt2d(arc.ptFrom), arc.angle1), NativeLib::delete_handle_Geom2d_TrimmedCurve)
                else trimmed = own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(c, gpPnt2d(arc.ptFrom), gpPnt2d(arc.ptTo)), NativeLib::delete_handle_Geom2d_TrimmedCurve)
                if (arc.reversed) _Geom2d_TrimmedCurve__Reverse(trimmed)
                return trimmed
            case TrimmedCurve2d:
                TrimmedCurve2d t = (TrimmedCurve2d) s
                trimmed = own(handle_Geom2d_TrimmedCurve__curve_u1_u2(curve2d(t.curve), t.u1, t.u2, 1, 1), NativeLib::delete_handle_Geom2d_TrimmedCurve)
                if (t.reversed) _Geom2d_TrimmedCurve__Reverse(trimmed)
                return trimmed
            case Mirrored2d:
                Mirrored2d mirrored = (Mirrored2d) s
                MemorySegment mirrorAxis = own(new_gp_Ax2d__pt_dir(gpPnt2d(mirrored.pos), gpDir2d(mirrored.dir)), NativeLib::delete_gp_Ax2d)
                trimmed = own(handle_Geom2d_Geometry__Copy(trimmedCurve2d(mirrored.curve)), NativeLib::delete_handle_Geom2d_Geometry)
                _Geom2d_TrimmedCurve__Mirror__ax2(trimmed, mirrorAxis)
                _Geom2d_TrimmedCurve__Reverse(trimmed)
                return trimmed
            default:
                throw new IllegalArgumentException("Unsupported trimmed curve: $s")
        }
    }
}
