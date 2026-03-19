// From
// https://dev.opencascade.org/doc/overview/html/occt_user_guides__modeling_algos.html
// See Approximating a Plate surface to a BSpline
#include <iostream>
#include <cmath>

#include <TopoDS_Shape.hxx>
#include <BRepBuilderAPI_MakePolygon.hxx>
#include <GeomPlate_BuildPlateSurface.hxx>
#include <BRepTools_WireExplorer.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Wire.hxx>
#include <BRepFill_CurveConstraint.hxx>
#include <BRepAdaptor_Curve.hxx>
#include <GeomPlate_MakeApprox.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <Geom_BSplineSurface.hxx>
#include <GeomPlate_Surface.hxx>


using namespace std;

extern "C" int visualize(const TopoDS_Shape& truc);

int main() {
    Standard_Integer NbCurFront=4,
    NbPointConstraint=1;
    gp_Pnt P1(0.,0.,0.);
    gp_Pnt P2(0.,10.,0.);
    gp_Pnt P3(0.,10.,10.);
    gp_Pnt P4(0.,0.,10.);
    gp_Pnt P5(5.,5.,5.);
    BRepBuilderAPI_MakePolygon W;
    W.Add(P1);
    W.Add(P2);
    W.Add(P3);
    W.Add(P4);
    W.Add(P1);
    // Initialize a BuildPlateSurface
    GeomPlate_BuildPlateSurface BPSurf(3,15,2);
    // Create the curve constraints
    BRepTools_WireExplorer anExp;
    for(anExp.Init(W); anExp.More(); anExp.Next())
    {
    TopoDS_Edge E = anExp.Current();
    Handle(BRepAdaptor_Curve) C = new BRepAdaptor_Curve();
    C->Initialize(E);
    Handle(BRepFill_CurveConstraint) Cont= new BRepFill_CurveConstraint(C,0);
    BPSurf.Add(Cont);
    }
    // Point constraint
    Handle(GeomPlate_PointConstraint) PCont= new
    GeomPlate_PointConstraint(P5,0);
    BPSurf.Add(PCont);
    // Compute the Plate surface
    BPSurf.Perform();
    // Approximation of the Plate surface
    Standard_Integer MaxSeg=9;
    Standard_Integer MaxDegree=8;
    Standard_Integer CritOrder=0;
    Standard_Real dmax,Tol;
    Handle(GeomPlate_Surface) PSurf = BPSurf.Surface();
    dmax = Max(0.0001,10*BPSurf.G0Error());
    Tol=0.0001;
    GeomPlate_MakeApprox
    Mapp(PSurf,Tol,MaxSeg,MaxDegree,dmax,CritOrder);
    Handle (Geom_Surface) Surf (Mapp.Surface());
    // create a face corresponding to the approximated Plate Surface
    Standard_Real Umin, Umax, Vmin, Vmax;
    PSurf->Bounds( Umin, Umax, Vmin, Vmax);
    BRepBuilderAPI_MakeFace MF(Surf,Umin, Umax, Vmin, Vmax, 0.01);
    visualize(MF.Face());
}

/*
g++ -I /usr/include/opencascade/ -lTKBinL -lTKBin -lTKBinTObj -lTKBinXCAF -lTKBool -lTKBO -lTKBRep -lTKCAF -lTKCDF -lTKDCAF -lTKDECascade -lTKDEGLTF -lTKDEIGES -lTKDEOBJ -lTKDEPLY -lTKDE -lTKDESTEP -lTKDESTL -lTKDEVRML -lTKDraw -lTKernel -lTKExpress -lTKFeat -lTKFillet -lTKG2d -lTKG3d -lTKGeomAlgo -lTKGeomBase -lTKHLR -lTKLCAF -lTKMath -lTKMesh -lTKMeshVS -lTKOffset -lTKOpenGl -lTKOpenGlTest -lTKPrim -lTKQADraw -lTKRWMesh -lTKService -lTKShHealing -lTKStdL -lTKStd -lTKTObjDRAW -lTKTObj -lTKTopAlgo -lTKTopTest -lTKV3d  -lTKVCAF -lTKViewerTest -lTKXCAF -lTKXDEDRAW -lTKXMesh -lTKXmlL -lTKXml -lTKXmlTObj -lTKXmlXCAF -lTKXSBase -lTKXSDRAWDE -lTKXSDRAWGLTF -lTKXSDRAWIGES -lTKXSDRAWOBJ -lTKXSDRAWPLY -lTKXSDRAW -lTKXSDRAWSTEP -lTKXSDRAWSTL -lTKXSDRAWVRML -lGL -lGLU -lGLX -lGLEW -lX11 -lxcb -lXau -lXdmcp OcctAisHello.cpp PlateSurface.cpp -o PlateSurface
 */