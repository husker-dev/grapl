#include "../shared.h"
#include "shared-gl.h"

#include <OpenGL/OpenGL.h>
#include <dlfcn.h>

void printError(const char* error){
    std::cout << "CGLError: " << error << std::endl;
}

CGLError checkError(CGLError error){
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

macosglfun(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion) {
    CGLContextObj context;

    CGLPixelFormatObj pix;
    GLint num;
    CGLPixelFormatAttribute attributes[4] = {
            kCGLPFAAccelerated,
            kCGLPFAOpenGLProfile,
            (CGLPixelFormatAttribute) (isCore ?
                ((majorVersion >= 4 || majorVersion == -1) ? kCGLOGLPVersion_GL4_Core : kCGLOGLPVersion_GL3_Core)
                : kCGLOGLPVersion_Legacy
            ),
            (CGLPixelFormatAttribute) 0
    };

    checkError(CGLChoosePixelFormat(attributes, &pix, &num));
    checkError(CGLCreateContext(pix, (CGLContextObj)shareWith, &context));
    checkError(CGLDestroyPixelFormat(pix));

    CGLContextObj oldContext = CGLGetCurrentContext();
    GLint major, minor;

    CGLSetCurrentContext(context);
    auto glGetIntegerv = (glGetIntegervPtr) getProcAddress("glGetIntegerv");
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    CGLSetCurrentContext(oldContext);

    jlong array[] = { (jlong)context, (jlong)major, (jlong)minor };
    return createLongArray(env, 3, array);
}

macosglfun(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    GLint major, minor;
    auto glGetIntegerv = (glGetIntegervPtr) getProcAddress("glGetIntegerv");
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);

    jlong array[] = { (jlong)CGLGetCurrentContext(), (jlong)major, (jlong)minor };
    return createLongArray(env, 3, array);
}

macosglfun(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong context) {
    return checkError(CGLSetCurrentContext((CGLContextObj)context)) == kCGLNoError;
}

macosglfun(void, nDeleteContext)(JNIEnv* env, jobject, jlong context) {
    checkError(CGLDestroyContext((CGLContextObj)context));
}