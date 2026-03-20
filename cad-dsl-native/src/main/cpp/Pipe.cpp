// From
// https://dev.opencascade.org/doc/overview/html/occt_user_guides__modeling_algos.html
// See The class BRepFeat_MakePipe

#include <TopoDS.hxx>
#include <TopoDS_Shape.hxx>
#include <BRepPrimAPI_MakeBox.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS_Face.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <Geom2d_Line.hxx>
#include <GCE2d_MakeLine.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <Geom_BezierCurve.hxx>
#include <BRepFeat_MakePipe.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepLib.hxx>

extern "C" int visualize(const TopoDS_Shape& truc);

int main() {
    TopoDS_Shape S = BRepPrimAPI_MakeBox(400.,250.,300.);
    TopExp_Explorer Ex;
    Ex.Init(S,TopAbs_FACE);
    Ex.Next();
    Ex.Next();
    TopoDS_Face F1 = TopoDS::Face(Ex.Current());
    Handle(Geom_Surface) surf = BRep_Tool::Surface(F1);
    BRepBuilderAPI_MakeWire MW1;
    gp_Pnt2d p1,p2;
    p1 = gp_Pnt2d(100.,100.);
    p2 = gp_Pnt2d(200.,100.);
    Handle(Geom2d_Line) aline = GCE2d_MakeLine(p1,p2).Value();

    MW1.Add(BRepBuilderAPI_MakeEdge(aline,surf,0.,p1.Distance(p2)));
    p1 = p2;
    p2 = gp_Pnt2d(150.,200.);
    aline = GCE2d_MakeLine(p1,p2).Value();

    MW1.Add(BRepBuilderAPI_MakeEdge(aline,surf,0.,p1.Distance(p2)));
    p1 = p2;
    p2 = gp_Pnt2d(100.,100.);
    aline = GCE2d_MakeLine(p1,p2).Value();

    MW1.Add(BRepBuilderAPI_MakeEdge(aline,surf,0.,p1.Distance(p2)));
    BRepBuilderAPI_MakeFace MKF1;
    MKF1.Init(surf,Standard_False, 0.01);
    MKF1.Add(MW1.Wire());
    TopoDS_Face FP = MKF1.Face();
    BRepLib::BuildCurves3d(FP);
    TColgp_Array1OfPnt CurvePoles(1,3);
    gp_Pnt pt = gp_Pnt(150.,0.,150.);
    CurvePoles(1) = pt;
    pt = gp_Pnt(200.,100.,150.);
    CurvePoles(2) = pt;
    pt = gp_Pnt(150.,200.,150.);
    CurvePoles(3) = pt;
    Handle(Geom_BezierCurve) curve = new Geom_BezierCurve(CurvePoles);
    TopoDS_Edge E = BRepBuilderAPI_MakeEdge(curve);
    TopoDS_Wire W = BRepBuilderAPI_MakeWire(E);
    BRepFeat_MakePipe MKPipe (S,FP,F1,W,Standard_False,
    Standard_True);
    MKPipe.Perform();
    TopoDS_Shape res1 = MKPipe.Shape();
    visualize(res1);
}

/*
g++ -I /usr/include/opencascade/ -lTKBinL -lTKBin -lTKBinTObj -lTKBinXCAF -lTKBool -lTKBO -lTKBRep -lTKCAF -lTKCDF -lTKDCAF -lTKDECascade -lTKDEGLTF -lTKDEIGES -lTKDEOBJ -lTKDEPLY -lTKDE -lTKDESTEP -lTKDESTL -lTKDEVRML -lTKDraw -lTKernel -lTKExpress -lTKFeat -lTKFillet -lTKG2d -lTKG3d -lTKGeomAlgo -lTKGeomBase -lTKHLR -lTKLCAF -lTKMath -lTKMesh -lTKMeshVS -lTKOffset -lTKOpenGl -lTKOpenGlTest -lTKPrim -lTKQADraw -lTKRWMesh -lTKService -lTKShHealing -lTKStdL -lTKStd -lTKTObjDRAW -lTKTObj -lTKTopAlgo -lTKTopTest -lTKV3d  -lTKVCAF -lTKViewerTest -lTKXCAF -lTKXDEDRAW -lTKXMesh -lTKXmlL -lTKXml -lTKXmlTObj -lTKXmlXCAF -lTKXSBase -lTKXSDRAWDE -lTKXSDRAWGLTF -lTKXSDRAWIGES -lTKXSDRAWOBJ -lTKXSDRAWPLY -lTKXSDRAW -lTKXSDRAWSTEP -lTKXSDRAWSTL -lTKXSDRAWVRML -lGL -lGLU -lGLX -lGLEW -lX11 -lxcb -lXau -lXdmcp OcctAisHello.cpp Pipe.cpp -o Pipe
*/