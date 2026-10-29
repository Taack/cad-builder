//
// C API: STEP import, off-screen rendering, STEP / STL export and shape diagnostics.
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
#include <Quantity_ColorRGBA.hxx>
#include <STEPCAFControl_Writer.hxx>
#include <STEPCAFControl_Reader.hxx>
#include <STEPControl_Reader.hxx>
#include <TDF_Tool.hxx>
#include <TopExp.hxx>
#include <TopTools_IndexedMapOfShape.hxx>
#include <UnitsMethods_LengthUnit.hxx>
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
#include <gp_Ax3.hxx>

#include <algorithm>
#include <cmath>
#include <sstream>
#include <string>
#include <vector>
#include <map>
#include <optional>
#include <stdexcept>

#ifdef _WIN32
#include <WNT_WClass.hxx>
#include <WNT_Window.hxx>
#elif defined(OCCT_HEADLESS)
#include <Aspect_NeutralWindow.hxx>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#else
#include <Xw_Window.hxx>
#endif

#ifdef OCCT_HEADLESS
// OpenGL context of an EGL display without any window system. OCCT's own EGL setup asks for a configuration with
// window surfaces, which such a display (Mesa surfaceless) does not have: this one asks for pbuffers.
class HeadlessEglContext {
public:
    EGLDisplay display;
    EGLConfig config = nullptr;
    EGLContext context = EGL_NO_CONTEXT;

    HeadlessEglContext() {
        display = eglGetPlatformDisplay(EGL_PLATFORM_SURFACELESS_MESA, EGL_DEFAULT_DISPLAY, nullptr);
        // drivers other than Mesa render without a window system on their default display
        if (display == EGL_NO_DISPLAY) display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (display == EGL_NO_DISPLAY || !eglInitialize(display, nullptr, nullptr))
            throw std::runtime_error("no EGL display");

        const EGLint attributes[] = {
            EGL_SURFACE_TYPE, EGL_PBUFFER_BIT, EGL_RENDERABLE_TYPE, EGL_OPENGL_BIT,
            EGL_RED_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_BLUE_SIZE, 8, EGL_DEPTH_SIZE, 24, EGL_STENCIL_SIZE, 8,
            EGL_NONE
        };
        EGLint count = 0;
        if (!eglChooseConfig(display, attributes, &config, 1, &count) || count == 0 || !eglBindAPI(EGL_OPENGL_API) ||
            (context = eglCreateContext(display, config, EGL_NO_CONTEXT, nullptr)) == EGL_NO_CONTEXT) {
            eglTerminate(display);
            throw std::runtime_error("no EGL context with pbuffers for OpenGL");
        }
    }

    ~HeadlessEglContext() {
        eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        eglDestroyContext(display, context);
        eglTerminate(display);
    }
};
#endif

// Renders one image per file name: frame i into fileNames[i], or the static locations when there is one file and frameCount is 0
static int dump(const char *function, const CadNode &assembly, int frameCount, int width, int height, const std::vector<std::string> &fileNames) {
    return guard(function, [&] {
#ifdef OCCT_HEADLESS
        // destroyed after the views and the driver that use it
        HeadlessEglContext egl;
#endif
        occ::handle<Aspect_DisplayConnection> _displayConnection = new Aspect_DisplayConnection();

        // don't waste the time waiting for VSync when window is not displayed on the screen
        OpenGl_Caps _caps;
        _caps.buffersNoSwap = true;

        occ::handle<OpenGl_GraphicDriver> _graphicDriver = new OpenGl_GraphicDriver(_displayConnection, false);
        _graphicDriver->ChangeOptions() = _caps;
#ifdef OCCT_HEADLESS
        if (!_graphicDriver->InitEglContext(egl.display, egl.context, egl.config))
            throw std::runtime_error("OCCT rejected the EGL context");
#else
        _graphicDriver->InitContext();
#endif

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
#elif defined(OCCT_HEADLESS)
        // Without a native window, OCCT renders into an EGL pbuffer
        occ::handle<Aspect_NeutralWindow> _win = new Aspect_NeutralWindow();
        _win->SetSize(width, height);
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

extern "C" TopoDS_Shape *new_TopoDS_Shape__STEPControl_Reader__fileName_gp_Ax2(const char *fileName, const gp_Ax2 *position) {
    TRACE("");
    return guard(__func__, [&] {
        STEPControl_Reader reader;
        if (reader.ReadFile(fileName) != IFSelect_RetDone) throw std::runtime_error(std::string("cannot read STEP file ") + fileName);
        reader.TransferRoots();
        TopoDS_Shape shape = reader.OneShape();
        if (shape.IsNull()) throw std::runtime_error(std::string("no shape in STEP file ") + fileName);
        gp_Trsf trsf;
        trsf.SetDisplacement(gp_Ax3(), gp_Ax3(*position));
        return new TopoDS_Shape(shape.Moved(TopLoc_Location(trsf)));
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

extern "C" occ::handle<CadNode> *handle_CadNode__part__TopoDS_Shape_name_hasColor_r_g_b_a(const TopoDS_Shape *shape, const char *name, int hasColor, double r, double g, double b, double a) {
    TRACE("");
    return guard(__func__, [&] {
        occ::handle<CadNode> part = new CadNode();
        if (name) part->name = name;
        part->shape = *shape;
        part->hasColor = to_bool(hasColor);
        part->color = Quantity_Color(r, g, b, Quantity_TOC_sRGB);
        part->alpha = a;
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
        if (part.hasColor && part.alpha < 1) prs->SetTransparency(1 - part.alpha);
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
// Writes an assembly tree to an XCAF document, each node once, however many times it is placed. A part is added without
// the location of its shape, which goes into the components placing it: XCAF would add a located shape, a revolution
// for instance, as a reference to the shape without location, and its product would lose its name.
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
            for (const CadNode::Component &c : node.components) {
                TopLoc_Location location(c.location);
                if (!c.node->isAssembly) location = location * c.node->shape.Location();
                shapeTool->AddComponent(l, label(*c.node), location);
            }
        } else {
            l = shapeTool->AddShape(node.shape.Located(TopLoc_Location()), false);
            if (node.hasColor) colorTool->SetColor(l, Quantity_ColorRGBA(node.color, static_cast<float>(node.alpha)), XCAFDoc_ColorGen);
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

namespace {
// Builds the CadNode tree of an XCAF document: a node per label and color, each one once
struct StepAssemblyReader {
    occ::handle<XCAFDoc_ShapeTool> shapeTool;
    std::map<std::string, occ::handle<CadNode>> nodes;

    static std::optional<Quantity_ColorRGBA> color(const TDF_Label &l) {
        Quantity_ColorRGBA c;
        if (XCAFDoc_ColorTool::GetColor(l, XCAFDoc_ColorSurf, c) || XCAFDoc_ColorTool::GetColor(l, XCAFDoc_ColorGen, c)) return c;
        return std::nullopt;
    }

    static std::string name(const TDF_Label &l) {
        occ::handle<TDataStd_Name> n;
        if (!l.FindAttribute(TDataStd_Name::GetID(), n)) return "";
        return TCollection_AsciiString(n->Get()).ToCString();
    }

    static occ::handle<CadNode> part(const TopoDS_Shape &shape, const std::string &name, const std::optional<Quantity_ColorRGBA> &c) {
        occ::handle<CadNode> part = new CadNode();
        part->name = name;
        part->shape = shape;
        if (c) {
            part->hasColor = true;
            part->color = c->GetRGB();
            part->alpha = c->Alpha();
        }
        return part;
    }

    static void keyOf(std::ostringstream &key, const std::optional<Quantity_ColorRGBA> &c) {
        if (c) key << ' ' << c->GetRGB().Red() << ' ' << c->GetRGB().Green() << ' ' << c->GetRGB().Blue() << ' ' << c->Alpha();
        else key << " -";
    }

    // The node of label, its color the one of its instance, else its own, else the one of an assembly above it
    occ::handle<CadNode> node(const TDF_Label &label, const std::optional<Quantity_ColorRGBA> &instance, const std::optional<Quantity_ColorRGBA> &above) {
        TCollection_AsciiString entry;
        TDF_Tool::Entry(label, entry);
        std::ostringstream key;
        key << entry.ToCString();
        keyOf(key, instance);
        keyOf(key, above);
        auto found = nodes.find(key.str());
        if (found != nodes.end()) return found->second;

        std::optional<Quantity_ColorRGBA> c = instance ? instance : color(label);
        if (!c) c = above;
        occ::handle<CadNode> n;
        if (XCAFDoc_ShapeTool::IsAssembly(label)) {
            n = new CadNode();
            n->name = name(label);
            n->isAssembly = true;
            NCollection_Sequence<TDF_Label> components;
            XCAFDoc_ShapeTool::GetComponents(label, components);
            for (const TDF_Label &component : components) {
                TDF_Label referred;
                if (!XCAFDoc_ShapeTool::GetReferredShape(component, referred)) continue;
                n->components.push_back({node(referred, color(component), c), XCAFDoc_ShapeTool::GetLocation(component).Transformation()});
            }
        } else {
            n = faceColors(label, c);
        }
        nodes[key.str()] = n;
        return n;
    }

    // The part of label, or when faces of it have their own colors, an assembly of a part per color
    occ::handle<CadNode> faceColors(const TDF_Label &label, const std::optional<Quantity_ColorRGBA> &partColor) {
        TopoDS_Shape shape = shapeTool->GetShape(label);
        TopTools_IndexedMapOfShape faces;
        TopExp::MapShapes(shape, TopAbs_FACE, faces);
        std::vector<std::optional<Quantity_ColorRGBA>> faceColor(faces.Extent() + 1);
        bool colored = false;
        NCollection_Sequence<TDF_Label> subShapes;
        XCAFDoc_ShapeTool::GetSubShapes(label, subShapes);
        for (const TDF_Label &sub : subShapes) {
            std::optional<Quantity_ColorRGBA> c = color(sub);
            if (!c) continue;
            TopTools_IndexedMapOfShape subFaces;
            TopExp::MapShapes(shapeTool->GetShape(sub), TopAbs_FACE, subFaces);
            for (int i = 1; i <= subFaces.Extent(); i++) {
                int index = faces.FindIndex(subFaces(i));
                if (index > 0) {
                    faceColor[index] = c;
                    colored = true;
                }
            }
        }
        if (!colored) return part(shape, name(label), partColor);

        // the faces of each color in a compound, in the order the colors are found
        std::vector<std::pair<std::optional<Quantity_ColorRGBA>, TopoDS_Compound>> groups;
        BRep_Builder builder;
        for (int i = 1; i <= faces.Extent(); i++) {
            std::optional<Quantity_ColorRGBA> c = faceColor[i] ? faceColor[i] : partColor;
            auto group = std::find_if(groups.begin(), groups.end(), [&](const auto &g) {
                return g.first.has_value() == c.has_value() && (!c || g.first->GetRGB().IsEqual(c->GetRGB()) && g.first->Alpha() == c->Alpha());
            });
            if (group == groups.end()) {
                groups.push_back({c, TopoDS_Compound()});
                builder.MakeCompound(groups.back().second);
                group = groups.end() - 1;
            }
            builder.Add(group->second, faces(i));
        }
        occ::handle<CadNode> n = new CadNode();
        n->name = name(label);
        n->isAssembly = true;
        for (const auto &group : groups) n->components.push_back({part(group.second, n->name, group.first), gp_Trsf()});
        return n;
    }
};
}

extern "C" occ::handle<CadNode> *handle_CadNode__STEPCAFControl_Reader__fileName_name(const char *fileName, const char *name) {
    TRACE("");
    return guard(__func__, [&] {
        occ::handle<TDocStd_Document> doc;
        XCAFApp_Application::GetApplication()->NewDocument("MDTV-XCAF", doc);
        XCAFDoc_DocumentTool::SetLengthUnit(doc, 1, UnitsMethods_LengthUnit_Millimeter);
        STEPCAFControl_Reader reader;
        reader.SetColorMode(true);
        reader.SetNameMode(true);
        if (reader.ReadFile(fileName) != IFSelect_RetDone) throw std::runtime_error(std::string("cannot read STEP file ") + fileName);
        if (!reader.Transfer(doc)) throw std::runtime_error(std::string("cannot transfer STEP file ") + fileName);

        StepAssemblyReader tree{XCAFDoc_DocumentTool::ShapeTool(doc->Main())};
        NCollection_Sequence<TDF_Label> roots;
        tree.shapeTool->GetFreeShapes(roots);
        if (roots.IsEmpty()) throw std::runtime_error(std::string("no shape in STEP file ") + fileName);
        occ::handle<CadNode> root;
        if (roots.Length() == 1) {
            root = tree.node(roots.First(), std::nullopt, std::nullopt);
        } else {
            root = new CadNode();
            root->isAssembly = true;
            for (const TDF_Label &l : roots) root->components.push_back({tree.node(l, std::nullopt, std::nullopt), gp_Trsf()});
        }
        if (name) root->name = name;
        XCAFApp_Application::GetApplication()->Close(doc);
        return new occ::handle<CadNode>(root);
    });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__CadNode__compound(const occ::handle<CadNode> *node) {
    TRACE("");
    return guard(__func__, [&] {
        BRep_Builder builder;
        TopoDS_Compound compound;
        builder.MakeCompound(compound);
        (*node)->forEachPart(gp_Trsf(), [&](const CadNode &part, const gp_Trsf &location) {
            builder.Add(compound, part.shape.Moved(TopLoc_Location(location)));
        });
        return new TopoDS_Shape(compound);
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
