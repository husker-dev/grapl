#define GL_SILENCE_DEPRECATION

#import "../shared/gl-shared.h"
#import "utils/thread-utils.h"

#import <Cocoa/Cocoa.h>
#import <OpenGL/OpenGL.h>

#import <dlfcn.h>

static void* libGL;

#define jni_macos_context(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_CGLContext_##fun
#define jni_macos_nscontext(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_NSGLContext_##fun
#define jni_macos_manager(returnType, fun)	 extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_MacGLManager_##fun


static void* a_GetProcAddress(const char* name) {
    if(libGL == NULL){
        static const char *NAMES[] = {
            "../Frameworks/OpenGL.framework/OpenGL",
            "/Library/Frameworks/OpenGL.framework/OpenGL",
            "/System/Library/Frameworks/OpenGL.framework/OpenGL",
            "/System/Library/Frameworks/OpenGL.framework/Versions/Current/OpenGL"
        };
        for(int i = 0; i < 4; i++)
            if((libGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
                break;
    }
    return dlsym(libGL, name);
}


static void printError(const char* error){
    printf("CGLError: %s", error);
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
