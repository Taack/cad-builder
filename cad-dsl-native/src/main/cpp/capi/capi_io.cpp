//
// C API: off-screen rendering, STEP / STL export and shape diagnostics.
//

#include "capi_internal.hxx"

#include <AIS_InteractiveContext.hxx>
#include <AIS_Shape.hxx>
#include <Aspect_DisplayConnection.hxx>
#include <BRepCheck_Analyzer.hxx>
#include <BRepCheck_Result.hxx>
#include <Image_AlienPixMap.hxx>
#include <OpenGl_GraphicDriver.hxx>
#include <Prs3d_Drawer.hxx>
#include <Prs3d_LineAspect.hxx>
#include <STEPCAFControl_Writer.hxx>
#include <StlAPI_Writer.hxx>
#include <TDocStd_Document.hxx>
#include <V3d_View.hxx>
#include <V3d_Viewer.hxx>
#include <XCAFApp_Application.hxx>
#include <XCAFDoc_DocumentTool.hxx>
#include <XCAFDoc_ShapeTool.hxx>

#ifdef _WIN32
#include <WNT_WClass.hxx>
#include <WNT_Window.hxx>
#else
#include <Xw_Window.hxx>
#endif

extern "C" int dumpShape(const TopoDS_Shape *shape, int width, int height, const char *fileName) {
    TRACE("");
    return guard(__func__, [&] {
        occ::handle<Aspect_DisplayConnection> _displayConnection = new Aspect_DisplayConnection();

        // don't waste the time waiting for VSync when window is not displayed on the screen
        OpenGl_Caps _caps;
        _caps.buffersNoSwap = true;

        occ::handle<OpenGl_GraphicDriver> _graphicDriver = new OpenGl_GraphicDriver(_displayConnection, false);
        _graphicDriver->ChangeOptions() = _caps;
        _graphicDriver->InitContext();

        //* Viewer setup
        occ::handle<V3d_Viewer> _viewer = new V3d_Viewer(_graphicDriver);
        // same look as the interactive viewer (OcctAisHello.cpp)
        _viewer->SetDefaultBackgroundColor(Quantity_NOC_GRAY50);
        _viewer->SetDefaultLights();
        _viewer->SetLightOn();

        occ::handle<V3d_View> _view = new V3d_View(_viewer, _viewer->DefaultTypeOfView());

#ifdef _WIN32
        /* Window - create a so called "virtual" WNT window that is a pure WNT window
           redefined to be never shown. */
        occ::handle<WNT_WClass> _wClass = new WNT_WClass("GW3D_Class", (void *) DefWindowProcW,
                                                         CS_VREDRAW | CS_HREDRAW, 0, 0,
                                                         ::LoadCursor(NULL, IDC_ARROW));
        occ::handle<WNT_Window> _win = new WNT_Window("", _wClass, WS_POPUP, 0, 0, width, height, Quantity_NOC_BLACK);
#else // Linux
        occ::handle<Xw_Window> _win = new Xw_Window(_graphicDriver->GetDisplayConnection(), "", 0, 0, width, height);
#endif
        _win->SetVirtual(true);

        //* View setup
        _view->SetWindow(_win);
        _view->SetComputedMode(false);
        _view->SetProj(V3d_XposYnegZpos);
        _view->AutoZFit();
        _view->TriedronDisplay(Aspect_TOTP_LEFT_LOWER, Quantity_NOC_WHITE, 0.1);
        _view->ChangeRenderingParams().NbMsaaSamples = 8;

        //* AIS context
        occ::handle<AIS_InteractiveContext> _context = new AIS_InteractiveContext(_viewer);
        _context->SetDisplayMode(AIS_Shaded, false);
        _context->DefaultDrawer()->SetFaceBoundaryDraw(true);
        _context->DefaultDrawer()->FaceBoundaryAspect()->SetColor(Quantity_NOC_CYAN);

        // Render immediate structures into back buffer rather than front.
        _view->View()->SetImmediateModeDrawToFront(false);

        //* Dump
        occ::handle<Image_AlienPixMap> pixmap = new Image_AlienPixMap;

        occ::handle<AIS_Shape> _shapePrs = new AIS_Shape(*shape);
        _context->Display(_shapePrs, false);
        _view->FitAll(0.1, true);

        _view->ToPixMap(*pixmap, width, height);
        return pixmap->Save(fileName) ? 1 : 0;
    });
}

extern "C" void write_step(const TopoDS_Shape *shape, const char *fileName) {
    TRACE("");
    guard(__func__, [&] {
        // Create document
        occ::handle<TDocStd_Document> aDoc;
        occ::handle<XCAFApp_Application> anApp = XCAFApp_Application::GetApplication();
        anApp->NewDocument("MDTV-XCAF", aDoc);

        // Create label and add our shape
        occ::handle<XCAFDoc_ShapeTool> myShapeTool = XCAFDoc_DocumentTool::ShapeTool(aDoc->Main());
        TDF_Label aLabel = myShapeTool->NewShape();
        myShapeTool->SetShape(aLabel, *shape);

        // Write as STEP file
        STEPCAFControl_Writer myWriter;
        myWriter.Perform(aDoc, fileName);
    });
}

extern "C" void write_stl(const TopoDS_Shape *shape, const char *fileName) {
    TRACE("");
    guard(__func__, [&] {
        StlAPI_Writer stl_writer;
        stl_writer.Write(*shape, fileName);
    });
}

extern "C" void analyze(const TopoDS_Shape *myShape) {
    TRACE("");
    guard(__func__, [&] {
        BRepCheck_Analyzer checker(*myShape);
        if (checker.IsValid()) {
            std::cout << "Valid Shape " << std::endl << std::flush;
            return;
        }
        std::cout << "Not valid Shape " << std::endl << std::flush;
        occ::handle<BRepCheck_Result> r = checker.Result(*myShape);
        if (r->IsBlind())
            std::cout << "Shape is Blind" << std::endl;
        if (r->IsMinimum())
            std::cout << "Shape is Min" << std::endl;
    });
}
