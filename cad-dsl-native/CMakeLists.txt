cmake_minimum_required(VERSION 3.31)
project(occtbins)

add_executable(sprocket src/main/cpp/sprocket.cpp)
add_executable(MakeBottle src/main/cpp/MakeBottle.cpp)

#set(OpenCASCADE_DIR /usr/lib/cmake/opencascade/)

find_package(VTK)
find_package(OpenCASCADE 7.8.1 REQUIRED)

if (OpenCASCADE_FOUND)
    message("OK")
    target_include_directories(sprocket PUBLIC "${OpenCASCADE_INCLUDE_DIRS}")
    target_link_libraries(sprocket PUBLIC "${OpenCASCADE_LIBRARIES}")
    target_include_directories(MakeBottle PUBLIC "${OpenCASCADE_INCLUDE_DIRS}")
    target_link_libraries(MakeBottle PUBLIC "${OpenCASCADE_LIBRARIES}")
else ()
    message("NOK")
endif ()