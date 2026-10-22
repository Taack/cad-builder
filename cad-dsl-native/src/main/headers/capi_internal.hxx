//
// Shared helpers for the C API implementation (src/main/cpp/capi/*.cpp). Not exported.
//

#ifndef CAPI_INTERNAL_HXX
#define CAPI_INTERNAL_HXX

#include "occt_capi.h"

#include <exception>
#include <iostream>
#include <sstream>
#include <type_traits>

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

#endif // CAPI_INTERNAL_HXX
