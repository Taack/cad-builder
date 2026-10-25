//
// Shared helpers for the C API implementation (src/main/cpp/capi/*.cpp). Not exported.
//

#ifndef CAPI_INTERNAL_HXX
#define CAPI_INTERNAL_HXX

#include "occt_capi.h"

#include <Quantity_Color.hxx>
#include <Standard_Transient.hxx>
#include <TopoDS_Shape.hxx>
#include <gp_Trsf.hxx>

#include <exception>
#include <iostream>
#include <sstream>
#include <string>
#include <algorithm>
#include <type_traits>
#include <vector>

#define TRACE2(message) TRACE_IMPL2(__FILE__, __LINE__, __PRETTY_FUNCTION__, message)
inline void TRACE_IMPL2(const char *file, int line, const char *function, std::ostringstream message) {
    std::cout << /*file << " : " <<*/ line << " : " << function << " : " << message.str() << "\n";
    std::cout.flush();
}

#define TRACE(message) TRACE_IMPL(__FILE__, __LINE__, __PRETTY_FUNCTION__, message)
inline void TRACE_IMPL(const char *file, int line, const char *function, const char *message) {
    std::cout << /*file << " : " <<*/ line << " : " << function << " : " << message << "\n";
    std::cout.flush();
}

// Records the failure for occt_last_error() (capi_runtime.cpp).
void capi_set_last_error(const char *function, const char *message);

// Runs body and keeps C++ exceptions from unwinding into the C caller (the JVM): on failure the
// error is recorded and a zero value (nullptr, 0) is returned instead.
// Since OCCT 8.0, Standard_Failure derives from std::exception.
template <typename F>
auto guard(const char *function, F &&body) noexcept -> decltype(body()) {
    using R = decltype(body());
    try {
        return body();
    } catch (const std::exception &e) {
        capi_set_last_error(function, e.what());
    } catch (...) {
        capi_set_last_error(function, "unknown C++ exception");
    }
    if constexpr (!std::is_void_v<R>) {
        return R{};
    }
}

inline bool to_bool(int flag) {
    return flag != 0;
}

class CadNode : public Standard_Transient {
public:
    struct Component {
        occ::handle<CadNode> node;
        gp_Trsf location;
        // Locations for each frame of an animation, empty when the component does not move
        std::vector<gp_Trsf> frames;

        // Location at frame, the static one when frame < 0
        const gp_Trsf &at(int frame) const {
            if (frame < 0 || frames.empty()) return location;
            return frames[std::min<size_t>(frame, frames.size() - 1)];
        }
    };

    std::string name;
    bool isAssembly = false;
    // Part
    TopoDS_Shape shape;
    bool hasColor = false;
    Quantity_Color color;
    // Assembly
    std::vector<Component> components;

    // Calls f(part, location) for each part of the tree, always in the same order, with its location in the root
    // coordinates at frame (the static locations when frame < 0)
    template <typename F>
    void forEachPart(const gp_Trsf &location, F &&f, int frame = -1) const {
        if (!isAssembly) {
            f(*this, location);
            return;
        }
        for (const Component &c : components) c.node->forEachPart(location * c.at(frame), f, frame);
    }
};

class AIS_InteractiveContext;
class AIS_Shape;
class Bnd_Box;

// Displays every part of the tree, shaded, with its color, at its static location (capi_io.cpp).
// Returns the presentations, in the forEachPart order.
std::vector<occ::handle<AIS_Shape>> capi_display_assembly(const occ::handle<AIS_InteractiveContext> &context, const CadNode &assembly);

// Moves the presentations returned by capi_display_assembly to their locations at frame.
void capi_move_assembly(const occ::handle<AIS_InteractiveContext> &context, const CadNode &assembly,
                        const std::vector<occ::handle<AIS_Shape>> &presentations, int frame);

// Bounding box of the assembly over the frameCount frames of its animation (the static locations when 0).
Bnd_Box capi_assembly_box(const CadNode &assembly, int frameCount);

// Triangulates the faces and discretizes the edges of shape, with a deflection relative to its size (capi_gltf.cpp).
void capi_mesh_shape(const TopoDS_Shape &shape);

#endif // CAPI_INTERNAL_HXX
