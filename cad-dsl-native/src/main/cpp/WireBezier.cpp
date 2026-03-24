#include<gp_Circ.hxx>
#include<TopoDS_Wire.hxx>
#include<BRepBuilderAPI_MakeWire.hxx>
#include<TColgp_Array1OfPnt.hxx>
#include<Geom_BezierCurve.hxx>
#include<BRepOffsetAPI_MakePipe.hxx>
#include<BRepBuilderAPI_MakeEdge.hxx>
#include<BRepBuilderAPI_MakeFace.hxx>

extern "C" int visualize(const TopoDS_Shape& truc);

int mainBezier() {
// Source - https://stackoverflow.com/a/78768046
// Posted by Thomas, modified by community. See post 'Timeline' for change history
// Retrieved 2026-03-24, License - CC BY-SA 4.0

        gp_Circ sweepCircle(gp_Ax2(gp_Pnt(0, 0, 0), gp_Dir(0, 0, 10)), 10);
        TopoDS_Wire sweepCircleWire = BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeEdge(sweepCircle)).Wire();

        BRepBuilderAPI_MakeWire wireBuilder;

        TColgp_Array1OfPnt pnts1(1, 3);
        pnts1.SetValue(1, gp_Pnt(0.0f, 0.0f, 0.0f));
        pnts1.SetValue(2, gp_Pnt(300.0f, 0.0f, 500.0f));
        pnts1.SetValue(3, gp_Pnt(400.0f, 0.0f, 0.0f));

        Handle(Geom_BezierCurve) curve1 = new Geom_BezierCurve(pnts1);
        wireBuilder.Add(BRepBuilderAPI_MakeEdge(curve1).Edge());

        TColgp_Array1OfPnt pnts2(1, 3);
        pnts2.SetValue(1, pnts1.Last());
        pnts2.SetValue(2, gp_Pnt(700.0f, 0.0f, 200.0f));
        pnts2.SetValue(3, gp_Pnt(400.0f, 0.0f, 600.0f));

        Handle(Geom_BezierCurve) curve2 = new Geom_BezierCurve(pnts1);
        wireBuilder.Add(BRepBuilderAPI_MakeEdge(curve2).Edge());

        const TopoDS_Shape &sweptShape = BRepOffsetAPI_MakePipe(wireBuilder.Wire(), BRepBuilderAPI_MakeFace(sweepCircleWire).Shape());
        visualize(sweptShape);
}

/*
g++ -I /usr/include/opencascade/ -lTKBinL -lTKBin -lTKBinTObj -lTKBinXCAF -lTKBool -lTKBO -lTKBRep -lTKCAF -lTKCDF -lTKDCAF -lTKDECascade -lTKDEGLTF -lTKDEIGES -lTKDEOBJ -lTKDEPLY -lTKDE -lTKDESTEP -lTKDESTL -lTKDEVRML -lTKDraw -lTKernel -lTKExpress -lTKFeat -lTKFillet -lTKG2d -lTKG3d -lTKGeomAlgo -lTKGeomBase -lTKHLR -lTKLCAF -lTKMath -lTKMesh -lTKMeshVS -lTKOffset -lTKOpenGl -lTKOpenGlTest -lTKPrim -lTKQADraw -lTKRWMesh -lTKService -lTKShHealing -lTKStdL -lTKStd -lTKTObjDRAW -lTKTObj -lTKTopAlgo -lTKTopTest -lTKV3d  -lTKVCAF -lTKViewerTest -lTKXCAF -lTKXDEDRAW -lTKXMesh -lTKXmlL -lTKXml -lTKXmlTObj -lTKXmlXCAF -lTKXSBase -lTKXSDRAWDE -lTKXSDRAWGLTF -lTKXSDRAWIGES -lTKXSDRAWOBJ -lTKXSDRAWPLY -lTKXSDRAW -lTKXSDRAWSTEP -lTKXSDRAWSTL -lTKXSDRAWVRML -lGL -lGLU -lGLX -lGLEW -lX11 -lxcb -lXau -lXdmcp OcctAisHello.cpp WireBezier.cpp -o WireBezier
*/