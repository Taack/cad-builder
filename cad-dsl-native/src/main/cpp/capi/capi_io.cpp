//
// C API: off-screen rendering, STEP / STL export and shape diagnostics.
//

#ifdef _WIN32
// Before any OCCT header: Standard_Macro.hxx defines NOUSER and NOGDI, which hide the window API used below.
#include <windows.h>
#endif

#include "capi_internal.hxx"

#include <AIS_InteractiveContext.hxx>
#include <AIS_Shape.hxx>
#include <Aspect_DisplayConnection.hxx>
#include <BRep_Builder.hxx>
#include <BRepBndLib.hxx>
#include <BRepCheck_Analyzer.hxx>
#include <BRepCheck_Result.hxx>
#include <Bnd_Box.hxx>
#include <Image_AlienPixMap.hxx>
#include <OpenGl_GraphicDriver.hxx>
#include <Prs3d_Drawer.hxx>
#include <Prs3d_LineAspect.hxx>
#include <STEPCAFControl_Writer.hxx>
#include <StlAPI_Writer.hxx>
#include <TCollection_ExtendedString.hxx>
#include <TDataStd_Name.hxx>
#include <TDocStd_Document.hxx>
#include <TopLoc_Location.hxx>
#include <TopoDS_Compound.hxx>
#include <V3d_View.hxx>
#include <V3d_Viewer.hxx>
#include <XCAFApp_Application.hxx>
#include <XCAFDoc_ColorTool.hxx>
#include <XCAFDoc_DocumentTool.hxx>
#include <XCAFDoc_ShapeTool.hxx>

#include <cmath>
#include <sstream>
#include <string>
#include <vector>
#include <map>
#include <stdexcept>

#ifdef _WIN32
#include <WNT_WClass.hxx>
#include <WNT_Window.hxx>
#else
#include <Xw_Window.hxx>
#endif

// Renders one image per file name: frame i into fileNames[i], or the static locations when there is one file and frameCount is 0
static int dump(const char *function, const CadNode &assembly, int frameCount, int width, int height, const std::vector<std::string> &fileNames) {
    return guard(function, [&] {
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

        std::vector<occ::handle<AIS_Shape>> presentations = capi_display_assembly(_context, assembly);
        // the same view for every frame, showing the whole motion
        Bnd_Box box = capi_assembly_box(assembly, frameCount);
        if (box.IsVoid()) _view->FitAll(0.1, true);
        else _view->FitAll(box, 0.1, true);

        int saved = 1;
        for (size_t i = 0; i < fileNames.size(); i++) {
            if (frameCount > 0) capi_move_assembly(_context, assembly, presentations, (int) i);
            _view->ToPixMap(*pixmap, width, height);
            if (!pixmap->Save(fileNames[i].c_str())) saved = 0;
        }
        return saved;
    });
}

extern "C" int dumpShape(const TopoDS_Shape *shape, int width, int height, const char *fileName) {
    TRACE("");
    CadNode part;
    part.shape = *shape;
    return dump(__func__, part, 0, width, height, {fileName});
}

extern "C" int dumpAssembly(const occ::handle<CadNode> *assembly, int width, int height, const char *fileName) {
    TRACE("");
    return dump(__func__, **assembly, 0, width, height, {fileName});
}

extern "C" int dumpAssemblyFrames(const occ::handle<CadNode> *assembly, int frameCount, int width, int height, const char *fileNames) {
    TRACE("");
    std::vector<std::string> names;
    std::istringstream lines(fileNames);
    for (std::string line; std::getline(lines, line);) names.push_back(line);
    return dump(__func__, **assembly, frameCount, width, height, names);
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

// StlAPI_Writer only writes the existing triangulation: mesh the shape first.
static void write_meshed_stl(const TopoDS_Shape &shape, const char *fileName) {
    capi_mesh_shape(shape);
    StlAPI_Writer stl_writer;
    if (!stl_writer.Write(shape, fileName)) throw std::runtime_error("cannot write STL file");
}

extern "C" void write_stl(const TopoDS_Shape *shape, const char *fileName) {
    TRACE("");
    guard(__func__, [&] { write_meshed_stl(*shape, fileName); });
}

extern "C" occ::handle<CadNode> *handle_CadNode__part__TopoDS_Shape_name_hasColor_r_g_b(const TopoDS_Shape *shape, const char *name, int hasColor, double r, double g, double b) {
    TRACE("");
    return guard(__func__, [&] {
        occ::handle<CadNode> part = new CadNode();
        if (name) part->name = name;
        part->shape = *shape;
        part->hasColor = to_bool(hasColor);
        part->color = Quantity_Color(r, g, b, Quantity_TOC_sRGB);
        return new occ::handle<CadNode>(part);
    });
}

extern "C" occ::handle<CadNode> *handle_CadNode__assembly__name(const char *name) {
    TRACE("");
    return guard(__func__, [&] {
        occ::handle<CadNode> assembly = new CadNode();
        if (name) assembly->name = name;
        assembly->isAssembly = true;
        return new occ::handle<CadNode>(assembly);
    });
}

extern "C" void _CadNode__AddComponent__node_location(occ::handle<CadNode> *assembly, const occ::handle<CadNode> *node, const gp_Trsf *location) {
    TRACE("");
    guard(__func__, [&] {
        if (!(*assembly)->isAssembly) throw std::invalid_argument("not an assembly");
        (*assembly)->components.push_back({*node, *location});
    });
}

extern "C" void _CadNode__SetLocation__index_location(occ::handle<CadNode> *assembly, int index, const gp_Trsf *location) {
    TRACE("");
    guard(__func__, [&] { (*assembly)->components.at(index).location = *location; });
}

extern "C" void _CadNode__SetFrames__index_count_values(occ::handle<CadNode> *assembly, int index, int count, const double *values) {
    TRACE("");
    guard(__func__, [&] {
        std::vector<gp_Trsf> &frames = (*assembly)->components.at(index).frames;
        frames.assign(count, gp_Trsf());
        for (int i = 0; i < count; i++) {
            const double *v = values + 12 * i;
            frames[i].SetValues(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11]);
        }
    });
}

std::vector<occ::handle<AIS_Shape>> capi_display_assembly(const occ::handle<AIS_InteractiveContext> &context, const CadNode &assembly) {
    std::vector<occ::handle<AIS_Shape>> presentations;
    assembly.forEachPart(gp_Trsf(), [&](const CadNode &part, const gp_Trsf &location) {
        occ::handle<AIS_Shape> prs = new AIS_Shape(part.shape);
        prs->SetLocalTransformation(location);
        if (part.hasColor) prs->SetColor(part.color);
        context->Display(prs, AIS_Shaded, 0, false);
        presentations.push_back(prs);
    });
    return presentations;
}

void capi_move_assembly(const occ::handle<AIS_InteractiveContext> &context, const CadNode &assembly,
                        const std::vector<occ::handle<AIS_Shape>> &presentations, int frame) {
    size_t i = 0;
    assembly.forEachPart(gp_Trsf(), [&](const CadNode &, const gp_Trsf &location) {
        context->SetLocation(presentations[i++], TopLoc_Location(location));
    }, frame);
}

Bnd_Box capi_assembly_box(const CadNode &assembly, int frameCount) {
    // the box of each part once, moved to each of its locations
    std::map<const CadNode *, Bnd_Box> partBoxes;
    Bnd_Box box;
    for (int frame = frameCount > 0 ? 0 : -1; frame < frameCount; frame++) {
        assembly.forEachPart(gp_Trsf(), [&](const CadNode &part, const gp_Trsf &location) {
            auto found = partBoxes.find(&part);
            if (found == partBoxes.end()) {
                Bnd_Box b;
                BRepBndLib::Add(part.shape, b);
                found = partBoxes.emplace(&part, b).first;
            }
            if (!found->second.IsVoid()) box.Add(found->second.Transformed(location));
        }, frame);
    }
    return box;
}

namespace {
// Writes an assembly tree to an XCAF document, each node once, however many times it is placed
struct StepAssemblyWriter {
    occ::handle<XCAFDoc_ShapeTool> shapeTool;
    occ::handle<XCAFDoc_ColorTool> colorTool;
    std::map<const CadNode *, TDF_Label> labels;

    TDF_Label label(const CadNode &node) {
        auto found = labels.find(&node);
        if (found != labels.end()) return found->second;
        TDF_Label l;
        if (node.isAssembly) {
            l = shapeTool->NewShape();
            for (const CadNode::Component &c : node.components)
                shapeTool->AddComponent(l, label(*c.node), TopLoc_Location(c.location));
        } else {
            l = shapeTool->AddShape(node.shape, false);
            if (node.hasColor) colorTool->SetColor(l, node.color, XCAFDoc_ColorGen);
        }
        if (!node.name.empty()) TDataStd_Name::Set(l, TCollection_ExtendedString(node.name.c_str(), true));
        labels[&node] = l;
        return l;
    }
};
}

extern "C" void write_step_assembly(const occ::handle<CadNode> *assembly, const char *fileName) {
    TRACE("");
    guard(__func__, [&] {
        occ::handle<TDocStd_Document> aDoc;
        occ::handle<XCAFApp_Application> anApp = XCAFApp_Application::GetApplication();
        anApp->NewDocument("MDTV-XCAF", aDoc);

        StepAssemblyWriter writer{XCAFDoc_DocumentTool::ShapeTool(aDoc->Main()), XCAFDoc_DocumentTool::ColorTool(aDoc->Main())};
        writer.label(**assembly);
        writer.shapeTool->UpdateAssemblies();

        STEPCAFControl_Writer myWriter;
        myWriter.SetColorMode(true);
        myWriter.SetNameMode(true);
        myWriter.Perform(aDoc, fileName);
    });
}

extern "C" void write_stl_assembly(const occ::handle<CadNode> *assembly, const char *fileName) {
    TRACE("");
    guard(__func__, [&] {
        // STL has neither parts nor colors: all the parts go, moved, into one compound
        BRep_Builder builder;
        TopoDS_Compound compound;
        builder.MakeCompound(compound);
        (*assembly)->forEachPart(gp_Trsf(), [&](const CadNode &part, const gp_Trsf &location) {
            builder.Add(compound, part.shape.Moved(TopLoc_Location(location)));
        });
        write_meshed_stl(compound, fileName);
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
