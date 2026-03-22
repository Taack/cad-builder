package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.new_BRepPrimAPI_MakeBox__x_y_z
import static org.taack.occt.NativeLib.new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2
import static org.taack.occt.NativeLib.new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape
import static org.taack.occt.NativeLib.new_gp_Ax2__gp_Pnt_gp_Dir

@CompileStatic
class CadDslVisitor implements ICadDslVisitor {

    MemorySegment shape
    Vec fromVec
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
        shape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__x_y_z(length.toDouble(), height.toDouble(), thickness.toDouble()))
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
}
