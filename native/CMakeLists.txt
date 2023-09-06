cmake_minimum_required (VERSION 3.8)
set (CMAKE_CXX_STANDARD 20)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)

project ("offscreen-jgl")

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Linux
if (UNIX AND NOT APPLE)

    find_package(X11 REQUIRED)
    include_directories(${X11_INCLUDE_DIR})

    message(Compile x86)
    add_library(offscreen-jgl-x86 SHARED linux.cpp)
    target_link_libraries(offscreen-jgl-x86 -lX11 -lGL)
    set_target_properties(offscreen-jgl-x86 PROPERTIES PREFIX "")
    set_target_properties(offscreen-jgl-x86 PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")

    message(Compile x64)
    add_library(offscreen-jgl-x64 SHARED linux.cpp)
    target_link_libraries(offscreen-jgl-x64 -lX11 -lGL)
    set_target_properties(offscreen-jgl-x64 PROPERTIES PREFIX "")
    set_target_properties(offscreen-jgl-x64 PROPERTIES COMPILE_FLAGS "-m64" LINK_FLAGS "-m64")
endif()

# Windows
if (WIN32)
    if(X64)
        message(Compile x64)
        add_library(offscreen-jgl-x64 SHARED windows.cpp)
        target_link_libraries(offscreen-jgl-x64 opengl32.lib)
    elseif(X86)
        message(Compile x86)
        add_library(offscreen-jgl-x86 SHARED windows.cpp)
        target_link_libraries(offscreen-jgl-x86 opengl32.lib)
    endif()
endif()

# MacOS
if (APPLE)
    add_library(offscreen-jgl SHARED macos.cpp)
    target_link_libraries(offscreen-jgl INTERFACE "-L/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/lib")

    include_directories(/System/Library/Frameworks)
    find_library(OpenGL_LIBRARY OpenGL)

    set_target_properties(offscreen-jgl PROPERTIES PREFIX "")
    target_link_libraries(offscreen-jgl ${OpenGL_LIBRARY})
endif()




