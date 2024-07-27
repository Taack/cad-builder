cmake_minimum_required(VERSION 3.29)
project(cad_dsl_native)

set(CMAKE_CXX_STANDARD 14)

#INCLUDE(cmake/CreateLibrary.cmake)

add_library(cad_dsl_native SHARED
        src/main/cpp/GlfwOcctView.cpp
        src/main/cpp/GlfwOcctView.h
        src/main/cpp/GlfwOcctWindow.cpp
        src/main/cpp/GlfwOcctWindow.h
        src/main/cpp/HelloWorld.cpp
        src/main/cpp/MakeBottle.cpp)

find_package(OpenCASCADE COMPONENTS FoundationClasses REQUIRED)
find_package(OpenCASCADE COMPONENTS ApplicationFramework REQUIRED)
find_package(OpenCASCADE COMPONENTS Config REQUIRED)
find_package(OpenCASCADE COMPONENTS ModelingAlgorithms REQUIRED)
find_package(OpenCASCADE COMPONENTS ModelingData REQUIRED)
find_package(OpenCASCADE COMPONENTS Tools REQUIRED)
find_package(OpenCASCADE COMPONENTS Draw REQUIRED)
find_package(OpenCASCADE COMPONENTS DataExchange REQUIRED)
find_package(OpenCASCADE COMPONENTS Visualization REQUIRED)
include_directories(${OpenCASCADE_INCLUDE_DIR})
MESSAGE("OCC-Include-Dir: ${OpenCASCADE_INCLUDE_DIR}")
#link_directories(${OpenCASCADE_LIBRARY_DIR})
MESSAGE("OCC-Lib-Dir: ${OpenCASCADE_LIBRARY_DIR}")
link_libraries(${OpenCASCADE_LIBRARIES})
MESSAGE("OCC-Libs: ${OpenCASCADE_LIBRARIES}")

set (LIBS ${LIBS} ${OpenCASCADE_LIBRARY_DIR})
#target_link_libraries(cad_dsl_native ${LIBS})

# Find freetype libraries on Linux (don't know why the one provided by the static occt dep doesn't work on Linux)
IF(UNIX AND NOT APPLE)
#    find_library(FreeType REQUIRED)

#    find_path (FONTCONFIG_INCLUDE_DIRS "fontconfig/fontconfig.h")
#    find_library(FontConfig REQUIRED)


    #    include_directories(${FREETYPE_INCLUDE_DIRS})
    #    link_directories(${FREETYPE_LIBRARY_DIR})
ENDIF()
