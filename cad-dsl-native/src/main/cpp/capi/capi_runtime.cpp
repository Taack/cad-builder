//
// Error reporting and memory release for the C API.
//

#include "capi_internal.hxx"

#include <BRepAlgoAPI_Cut.hxx>
#include <BRepAdaptor_Curve.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_MakePolygon.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_Transform.hxx>
#include <BRepFeat_MakeDPrism.hxx>
#include <BRepFeat_MakePipe.hxx>
#include <BRepFill_CurveConstraint.hxx>
#include <BRepFilletAPI_MakeFillet.hxx>
#include <BRepOffsetAPI_MakeThickSolid.hxx>
#include <BRepOffsetAPI_ThruSections.hxx>
#include <BRepPrimAPI_MakeBox.hxx>
#include <BRepPrimAPI_MakeCylinder.hxx>
#include <BRepTools_WireExplorer.hxx>
#include <BRep_Builder.hxx>
#include <GccAna_Circ2d2TanRad.hxx>
#include <Geom2dAPI_InterCurveCurve.hxx>
#include <Geom2d_Circle.hxx>
#include <Geom2d_Ellipse.hxx>
#include <Geom2d_Line.hxx>
#include <Geom2d_TrimmedCurve.hxx>
#include <GeomAPI_ExtremaCurveSurface.hxx>
#include <GeomPlate_BuildPlateSurface.hxx>
#include <GeomPlate_MakeApprox.hxx>
#include <GeomPlate_PointConstraint.hxx>
#include <GeomPlate_Surface.hxx>
#include <Geom_BSplineSurface.hxx>
#include <Geom_BezierCurve.hxx>
#include <Geom_CylindricalSurface.hxx>
#include <Geom_Ellipse.hxx>
#include <Geom_Line.hxx>
#include <Geom_Plane.hxx>
#include <Geom_SurfaceOfLinearExtrusion.hxx>
#include <Geom_SurfaceOfRevolution.hxx>
#include <Geom_TrimmedCurve.hxx>
#include <ShapeExtend_WireData.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS_Compound.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Face.hxx>
#include <TopoDS_Wire.hxx>
#include <gp_Ax1.hxx>
#include <gp_Ax22d.hxx>
#include <gp_Ax2d.hxx>
#include <gp_Ax3.hxx>
#include <gp_Circ2d.hxx>
#include <gp_Pln.hxx>
#include <gp_Trsf.hxx>

#include <string>

namespace {
thread_local std::string lastError;
thread_local bool hasLastError = false;
}

void capi_set_last_error(const char *function, const char *message) {
    lastError = std::string(function) + ": " + (message ? message : "");
    hasLastError = true;
    TRACE(lastError.c_str());
}

extern "C" const char *occt_last_error(void) {
    return hasLastError ? lastError.c_str() : nullptr;
}

extern "C" void occt_clear_last_error(void) {
    lastError.clear();
    hasLastError = false;
}

extern "C" void occt_free_doubles(double *values) {
    delete[] values;
}

#define CAPI_DEFINE_DELETE(T) \
    extern "C" void delete_##T(T *ptr) { delete ptr; }
#define CAPI_DEFINE_DELETE_HANDLE(T) \
    extern "C" void delete_handle_##T(occ::handle<T> *ptr) { delete ptr; }

OCCT_CAPI_OBJECT_TYPES(CAPI_DEFINE_DELETE)
OCCT_CAPI_HANDLE_TYPES(CAPI_DEFINE_DELETE_HANDLE)
CAPI_DEFINE_DELETE(TopoDS_Shape)
CAPI_DEFINE_DELETE(TopTools_ListOfShape)
CAPI_DEFINE_DELETE(TColgp_Array1OfPnt)

extern "C" int TopAbs_ShapeEnumFromOrdinal(int ordinal) {
    return ordinal >= TopAbs_COMPOUND && ordinal <= TopAbs_SHAPE ? ordinal : TopAbs_SHAPE;
}
