cmake_minimum_required (VERSION 3.8)
set (CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)

project ("ojgl")

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# add_library(ojgl SHARED "library.cpp" linux.cpp)

# Linux
if (UNIX AND NOT APPLE)
    find_package(X11 REQUIRED)
    include_directories(${X11_INCLUDE_DIR})

    add_library(ojgl SHARED linux.cpp)
    target_link_libraries(ojgl -lX11 -lGL)
endif()





