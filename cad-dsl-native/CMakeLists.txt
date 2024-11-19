cmake_minimum_required(VERSION 3.29)
project(cad-dsl-native)

add_executable(sprocket src/main/cpp/sprocket.cpp)
add_executable(MakeBottle src/main/cpp/MakeBottle.cpp)

find_package(VTK)
find_package(OpenCASCADE 7.8.1 REQUIRED)

target_include_directories(sprocket PUBLIC "${OpenCASCADE_INCLUDE_DIRS}")
target_link_libraries(sprocket PUBLIC "${OpenCASCADE_LIBRARIES}")
target_include_directories(MakeBottle PUBLIC "${OpenCASCADE_INCLUDE_DIRS}")
target_link_libraries(MakeBottle PUBLIC "${OpenCASCADE_LIBRARIES}")
