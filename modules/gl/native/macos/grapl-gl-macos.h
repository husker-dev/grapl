#define GL_SILENCE_DEPRECATION

#include "grapl-gl.h"
#import "utils/thread-utils.h"


#import <Cocoa/Cocoa.h>
#import <OpenGL/OpenGL.h>

#include <jni.h>
#include <iostream>

#include <dlfcn.h>
static void* libGL;


#define jni_macos_context(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_CGLContext_##fun
#define jni_macos_nscontext(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_NSGLContext_##fun
#define jni_macos_platform(returnType, fun)	 extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_MacGLPlatform_##fun


static void printError(const char* error){
    std::cout << "CGLError: " << error << std::endl;
}

static CGLError checkError(CGLError error){
    if(error == kCGLNoError) return error;
    else if (error == kCGLBadAttribute) printError("kCGLBadAttribute (invalid pixel format attribute)");
    else if (error == kCGLBadProperty) printError("kCGLBadProperty (invalid renderer property)");
    else if (error == kCGLBadPixelFormat) printError("kCGLBadPixelFormat (invalid pixel format)");
    else if (error == kCGLBadRendererInfo) printError("kCGLBadRendererInfo (invalid renderer info)");
    else if (error == kCGLBadDrawable) printError("kCGLBadDrawable (invalid drawable)");
    else if (error == kCGLBadDisplay) printError("kCGLBadDisplay (invalid graphics device)");
    else if (error == kCGLBadState) printError("kCGLBadState (invalid context state)");
    else if (error == kCGLBadValue) printError("kCGLBadValue (invalid numerical value)");
    else if (error == kCGLBadMatch) printError("kCGLBadMatch (invalid share context)");
    else if (error == kCGLBadEnumeration) printError("kCGLBadEnumeration (invalid enumerant)");
    else if (error == kCGLBadOffScreen) printError("kCGLBadOffScreen (invalid offscreen drawable)");
    else if (error == kCGLBadFullScreen) printError("kCGLBadFullScreen (invalid fullscreen drawable)");
    else if (error == kCGLBadWindow) printError("kCGLBadWindow (invalid window)");
    else if (error == kCGLBadAddress) printError("kCGLBadAddress (invalid pointer)");
    else if (error == kCGLBadCodeModule) printError("kCGLBadCodeModule (invalid code module)");
    else if (error == kCGLBadAlloc) printError("kCGLBadAlloc (invalid memory allocation)");
    else if (error == kCGLBadConnection) printError("kCGLBadState (invalid CoreGraphics connection)");
    return error;
}
