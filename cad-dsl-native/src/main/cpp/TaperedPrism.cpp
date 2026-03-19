// From
// https://dev.opencascade.org/doc/overview/html/occt_user_guides__modeling_algos.html
// See A tapered prism
#include <iostream>
#include <cmath>

#include <TopoDS_Shape.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS_Face.hxx>
#include <Geom_Surface.hxx>
#include <gp_Circ2d.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepLib.hxx>
#include <BRepFeat_MakeDPrism.hxx>
#include <BRepPrimAPI_MakeBox.hxx>
#include <BRep_Tool.hxx>
#include <Geom2d_Circle.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>

extern "C" int visualize(const TopoDS_Shape& truc);

int main() {
    TopoDS_Shape S = BRepPrimAPI_MakeBox(400.,250.,300.);
    TopExp_Explorer Ex;
    Ex.Init(S,TopAbs_FACE);
    Ex.Next();
    Ex.Next();
    Ex.Next();
    Ex.Next();
    Ex.Next();
    TopoDS_Face F = TopoDS::Face(Ex.Current());
    Handle(Geom_Surface) surf = BRep_Tool::Surface(F);
    gp_Circ2d
    c(gp_Ax2d(gp_Pnt2d(200.,130.),gp_Dir2d(1.,0.)),50.);
    BRepBuilderAPI_MakeWire MW;
    Handle(Geom2d_Curve) aline = new Geom2d_Circle(c);
    MW.Add(BRepBuilderAPI_MakeEdge(aline,surf,0.,M_PI));
    MW.Add(BRepBuilderAPI_MakeEdge(aline,surf,M_PI,2.*M_PI));
    BRepBuilderAPI_MakeFace MKF;
    MKF.Init(surf,Standard_False, 0.01);
    MKF.Add(MW.Wire());
    TopoDS_Face FP = MKF.Face();
    BRepLib::BuildCurves3d(FP);
    BRepFeat_MakeDPrism MKDP (S,FP,F,10*M_PI/180,Standard_True,
                                Standard_True);
    MKDP.Perform(200);
    TopoDS_Shape res1 = MKDP.Shape();

   visualize(res1);
}

/*
g++ -I /usr/include/opencascade/ -lTKBinL -lTKBin -lTKBinTObj -lTKBinXCAF -lTKBool -lTKBO -lTKBRep -lTKCAF -lTKCDF -lTKDCAF -lTKDECascade -lTKDEGLTF -lTKDEIGES -lTKDEOBJ -lTKDEPLY -lTKDE -lTKDESTEP -lTKDESTL -lTKDEVRML -lTKDraw -lTKernel -lTKExpress -lTKFeat -lTKFillet -lTKG2d -lTKG3d -lTKGeomAlgo -lTKGeomBase -lTKHLR -lTKLCAF -lTKMath -lTKMesh -lTKMeshVS -lTKOffset -lTKOpenGl -lTKOpenGlTest -lTKPrim -lTKQADraw -lTKRWMesh -lTKService -lTKShHealing -lTKStdL -lTKStd -lTKTObjDRAW -lTKTObj -lTKTopAlgo -lTKTopTest -lTKV3d  -lTKVCAF -lTKViewerTest -lTKXCAF -lTKXDEDRAW -lTKXMesh -lTKXmlL -lTKXml -lTKXmlTObj -lTKXmlXCAF -lTKXSBase -lTKXSDRAWDE -lTKXSDRAWGLTF -lTKXSDRAWIGES -lTKXSDRAWOBJ -lTKXSDRAWPLY -lTKXSDRAW -lTKXSDRAWSTEP -lTKXSDRAWSTL -lTKXSDRAWVRML -lGL -lGLU -lGLX -lGLEW -lX11 -lxcb -lXau -lXdmcp OcctAisHello.cpp TaperedPrism.cpp -o TaperedPrism
 */