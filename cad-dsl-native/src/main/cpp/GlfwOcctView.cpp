// MIT License
// 
// Copyright(c) 2023 Shing Liu
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

#include "GlfwOcctView.h"

#include <AIS_Shape.hxx>
#include <BRepPrimAPI_MakeBox.hxx>
#include <BRepPrimAPI_MakeCone.hxx>
#include <Message.hxx>
#include <Message_Messenger.hxx>
#include <OpenGl_GraphicDriver.hxx>

// #define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>

namespace {
    //! Convert GLFW mouse button into Aspect_VKeyMouse.
    static Aspect_VKeyMouse mouseButtonFromGlfw(int theButton) {
        switch (theButton) {
            case GLFW_MOUSE_BUTTON_LEFT: return Aspect_VKeyMouse_LeftButton;
            case GLFW_MOUSE_BUTTON_RIGHT: return Aspect_VKeyMouse_RightButton;
            case GLFW_MOUSE_BUTTON_MIDDLE: return Aspect_VKeyMouse_MiddleButton;
        }
        return Aspect_VKeyMouse_NONE;
    }

    //! Convert GLFW key modifiers into Aspect_VKeyFlags.
    static Aspect_VKeyFlags keyFlagsFromGlfw(int theFlags) {
        Aspect_VKeyFlags aFlags = Aspect_VKeyFlags_NONE;
        if ((theFlags & GLFW_MOD_SHIFT) != 0) {
            aFlags |= Aspect_VKeyFlags_SHIFT;
        }
        if ((theFlags & GLFW_MOD_CONTROL) != 0) {
            aFlags |= Aspect_VKeyFlags_CTRL;
        }
        if ((theFlags & GLFW_MOD_ALT) != 0) {
            aFlags |= Aspect_VKeyFlags_ALT;
        }
        if ((theFlags & GLFW_MOD_SUPER) != 0) {
            aFlags |= Aspect_VKeyFlags_META;
        }
        return aFlags;
    }
}

// ================================================================
// Function : GlfwOcctView
// Purpose  :
// ================================================================
GlfwOcctView::GlfwOcctView() {
}

// ================================================================
// Function : ~GlfwOcctView
// Purpose  :
// ================================================================
GlfwOcctView::~GlfwOcctView() {
}

// ================================================================
// Function : toView
// Purpose  :
// ================================================================
GlfwOcctView *GlfwOcctView::toView(GLFWwindow *theWin) {
    return static_cast<GlfwOcctView *>(glfwGetWindowUserPointer(theWin));
}

// ================================================================
// Function : errorCallback
// Purpose  :
// ================================================================
void GlfwOcctView::errorCallback(int theError, const char *theDescription) {
    Message::DefaultMessenger()->Send(TCollection_AsciiString("Error") + theError + ": " + theDescription,
                                      Message_Fail);
}

// ================================================================
// Function : run
// Purpose  :
// ================================================================
void GlfwOcctView::run() {
    initWindow(800, 600, "OCCT IMGUI");
    initViewer();
    if (myView.IsNull()) {
        return;
    }

    myView->MustBeResized();
    myOcctWindow->Map();
    mainloop();
    cleanup();
}

// ================================================================
// Function : initWindow
// Purpose  :
// ================================================================
void GlfwOcctView::initWindow(int theWidth, int theHeight, const char *theTitle) {
    glfwSetErrorCallback(GlfwOcctView::errorCallback);
    glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);
    // glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WAYLAND);
    glfwInit();
    if (glfwVulkanSupported()) {
        std::cout << "GLFW Vulkan supported" << std::endl;
    }
    const bool toAskCoreProfile = true;
    const bool glEs = false;
    if (toAskCoreProfile) {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        //glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, true);
        //glfwWindowHint(GLFW_DECORATED, GL_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_FALSE);
    } else if (glEs) {
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
        // glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        // glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        // glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
    }

    myOcctWindow = new GlfwOcctWindow(theWidth, theHeight, theTitle);
    glfwSetWindowUserPointer(myOcctWindow->getGlfwWindow(), this);

    // window callback
    glfwSetWindowSizeCallback(myOcctWindow->getGlfwWindow(), GlfwOcctView::onResizeCallback);
    glfwSetFramebufferSizeCallback(myOcctWindow->getGlfwWindow(), GlfwOcctView::onFBResizeCallback);

    // mouse callback
    glfwSetScrollCallback(myOcctWindow->getGlfwWindow(), GlfwOcctView::onMouseScrollCallback);
    glfwSetMouseButtonCallback(myOcctWindow->getGlfwWindow(), GlfwOcctView::onMouseButtonCallback);
    glfwSetCursorPosCallback(myOcctWindow->getGlfwWindow(), GlfwOcctView::onMouseMoveCallback);
}

// ================================================================
// Function : initViewer
// Purpose  :
// ================================================================
void GlfwOcctView::initViewer() {
    if (myOcctWindow.IsNull()
        || myOcctWindow->getGlfwWindow() == nullptr) {
        return;
    }

    Handle(OpenGl_GraphicDriver) aGraphicDriver
            = new OpenGl_GraphicDriver(myOcctWindow->GetDisplay(), Standard_False);
    aGraphicDriver->SetBuffersNoSwap(Standard_True);

    Handle(V3d_Viewer) aViewer = new V3d_Viewer(aGraphicDriver);
    aViewer->SetDefaultLights();
    aViewer->SetLightOn();
    aViewer->SetDefaultTypeOfView(V3d_PERSPECTIVE);
    aViewer->ActivateGrid(Aspect_GT_Rectangular, Aspect_GDM_Lines);
    myView = aViewer->CreateView();
    myView->SetImmediateUpdate(Standard_False);
    myView->SetWindow(myOcctWindow, myOcctWindow->NativeGlContext());
    myView->ChangeRenderingParams().ToShowStats = Standard_True;

    myContext = new AIS_InteractiveContext(aViewer);
}


// ================================================================
// Function : mainloop
// Purpose  :
// ================================================================
void GlfwOcctView::mainloop() {
    while (!glfwWindowShouldClose(myOcctWindow->getGlfwWindow())) {
        // glfwPollEvents() for continuous rendering (immediate return if there are no new events)
        // and glfwWaitEvents() for rendering on demand (something actually happened in the viewer)
        glfwPollEvents();
        //glfwWaitEvents();
        if (!myView.IsNull()) {
            FlushViewEvents(myContext, myView, Standard_True);
        }
    }
}

void GlfwOcctView::cleanup() const {
    if (!myView.IsNull()) {
        myView->Remove();
    }
    if (!myOcctWindow.IsNull()) {
        myOcctWindow->Close();
    }

    glfwTerminate();
}

void GlfwOcctView::onResize(int theWidth, int theHeight) {
    if (theWidth != 0
        && theHeight != 0
        && !myView.IsNull()) {
        myView->Window()->DoResize();
        myView->MustBeResized();
        myView->Invalidate();
        myView->Redraw();
    }
}

// ================================================================
// Function : onMouseScroll
// Purpose  :
// ================================================================
void GlfwOcctView::onMouseScroll(double theOffsetX, double theOffsetY) {
}

// ================================================================
// Function : onMouseButton
// Purpose  :
// ================================================================
void GlfwOcctView::onMouseButton(int theButton, int theAction, int theMods) {
    const Graphic3d_Vec2i aPos = myOcctWindow->CursorPosition();
    if (theAction == GLFW_PRESS) {
        PressMouseButton(aPos, mouseButtonFromGlfw(theButton), keyFlagsFromGlfw(theMods), false);
    } else {
        ReleaseMouseButton(aPos, mouseButtonFromGlfw(theButton), keyFlagsFromGlfw(theMods), false);
    }
}

// ================================================================
// Function : onMouseMove
// Purpose  :
// ================================================================
void GlfwOcctView::onMouseMove(int thePosX, int thePosY) {
    const Graphic3d_Vec2i aNewPos(thePosX, thePosY);
    UpdateMousePosition(aNewPos, PressedMouseButtons(), LastMouseFlags(), Standard_False);
}

extern "C" void *GlfwOcctView_initWindow(const int theWidth, const int theHeight, const char *theTitle) {
    std::cout << "GlfwOcctView_initWindow" << std::endl;
    auto anOcctView = new GlfwOcctView();

    anOcctView->initWindow(theWidth, theHeight, theTitle);

    return anOcctView;
}

extern "C" void GlfwOcctView_initViewer(void *occtView) {
    std::cout << "GlfwOcctView_initViewer" << std::endl;
    static_cast<GlfwOcctView *>(occtView)->initViewer();
}

extern "C" void GlfwOcctView_initGui(void *occtView) {
    std::cout << "GlfwOcctView_initGui" << std::endl;
    static_cast<GlfwOcctView *>(occtView)->myView->MustBeResized();
    static_cast<GlfwOcctView *>(occtView)->myOcctWindow->Map();
}

extern "C" void GlfwOcctView_mainloop(void *occtView) {
    std::cout << "GlfwOcctView_mainloop" << std::endl;
    std::cout << std::flush;

    static_cast<GlfwOcctView *>(occtView)->mainloop();
}

extern "C" void GlfwOcctView_displaySomething(void *occtView) {
    std::cout << "GlfwOcctView_displayInContext 1" << std::endl;
    TopoDS_Shape aShape = BRepPrimAPI_MakeBox (100, 100, 100).Solid();
    Handle(AIS_Shape) aShapePrs = new AIS_Shape(aShape);
    std::cout << "GlfwOcctView_displayInContext 2" << std::endl;
    static_cast<GlfwOcctView *>(occtView)->myContext->Display(aShapePrs, AIS_Shaded, 0, true);
    std::cout << "GlfwOcctView_displayInContext 3" << std::endl;
}

extern "C" void GlfwOcctView_displayInContext(void *occtView, void *aShape) {
    std::cout << "GlfwOcctView_displayInContext 1" << std::endl;
    Handle(AIS_Shape) aShapePrs = new AIS_Shape((TopoDS_Shape &) aShape);
    std::cout << "GlfwOcctView_displayInContext 2" << std::endl;
    static_cast<GlfwOcctView *>(occtView)->myContext->Display(aShapePrs, AIS_Shaded, 0, true);
    std::cout << "GlfwOcctView_displayInContext 3" << std::endl;
}

extern "C" void GlfwOcctView_cleanup(void *occtView) {
    std::cout << "GlfwOcctView_cleanup" << std::endl;
    static_cast<GlfwOcctView *>(occtView)->cleanup();
}
