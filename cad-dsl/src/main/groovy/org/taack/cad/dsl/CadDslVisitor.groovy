package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.ShapeEnum
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import static java.lang.Math.*

@CompileStatic
class CadDslVisitor implements ICadDslVisitor {

    MemorySegment shape
    MemorySegment face
    MemorySegment surf
    MemorySegment makeFace
    MemorySegment MW

    Vec fromVec = new Vec()
    @Override
    void visitFrom(Vec pos) {
        fromVec = pos
    }

    @Override
    void visitFromEnd(Vec pos) {

    }

    @Override
    void visitFrom(Vec2d pos) {
    }

    @Override
    void visitFromEnd(Vec2d pos) {

    }

    @Override
    void visitBox(Number length, Number height, Number thickness, Vec direction) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__Ax2_x_y_z(ax2, length.toDouble(), height.toDouble(), thickness.toDouble()))
    }

    @Override
    void visitSphere(Number radius, Vec direction) {

    }

    @Override
    void visitCylinder(Number radius, Number height, Vec direction) {

    }

    @Override
    void visitTorus(Number torusRadius, Number ringRadius, Vec direction) {
        def ax2 = new_gp_Ax2__gp_Pnt_gp_Dir(fromVec.toGpPnt(), direction.toGpDir())
        shape = new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(ax2, torusRadius.toDouble(), ringRadius.toDouble())

    }

    @Override
    void visitCut() {

    }

    @Override
    void visitCutEnd() {

    }

    @Override
    void visitFuse() {

    }

    @Override
    void visitFuseEnd() {

    }

    @Override
    void visitFace(Vec direction) {
        double positionMax = -1

        for (def aFaceExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(shape, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             _TopExp_Explorer__More(aFaceExplorer);
             _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer)
            def aSurface = handle_Geom_Surface__TopoDS_Face(aFace)
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
                fromVec = Vec.fromAPnt(aPnt)
                double aZ = fromVec.cord(direction)
                if (aZ > positionMax) {
                    positionMax = aZ
                    face = aFace
                }
            }
        }
    }

    @Override
    void display(String fileName) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                if (fileName.endsWith(".step")) {
                    write_step(this.shape, t)
                } else if (fileName.endsWith(".stl")) {
                    write_stl(this.shape, t)
                } else
                    dumpShape(shape, 1920, 1080, t)
            }
        } else visualize(shape)

    }

    @Override
    void visitCenter() {
        fromVec = Vec.fromAPnt(new_gp_Pnt__CentreOfMass__TopoDS_Shape(face))
        MW = new_BRepBuilderAPI_MakeWire()
    }

    @Override
    void visitCenterEnd() {
        makeFace = new_BRepBuilderAPI_MakeFace()
        _BRepBuilderAPI_MakeFace__Init(makeFace, surf, 0, 0.01d)
    }

    @Override
    void visiteCircle(Number radius) {
        def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(fromVec.toGpPnt2d(), fromVec.toGpDir2d()), radius.toDouble())
        def aline = handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c)
        surf = handle_Geom_Surface__TopoDS_Face(face)
        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(aline, surf))
    }

    @Override
    void visitHole(Number depth) {
        _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(makeFace, MW)
        def FP = TopoDS_Face__BRepBuilderAPI_MakeFace__Face(makeFace)
        _BRepLib__BuildCurves3d__TopoDS_Shape FP
        def CurvePoles = new_TColgp_Array1OfPnt__Low_Up(1,3)
        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(150,0,150).toGpPnt(), 1)
        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(200,100,150).toGpPnt(), 2)
        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(150,200,150).toGpPnt(), 3)
        def curve = handle_Geom_BezierCurve__TColgp_Array1OfPnt(CurvePoles)
        def E = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve curve
        def W = new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1 E

//        def MKDP = new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(shape,FP,face,W,0,1)

        def MKDP = new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(shape, FP, surf, 0, 0, 1)
        _BRepFeat_MakeDPrism__Perform__Height(MKDP, -depth.toDouble())
//        _BRepFeat_MakePipe__Perform(MKDP)
        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape MKDP
    }
}
