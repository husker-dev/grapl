#include <jni.h>
#include <iostream>
#include <OpenGL/OpenGL.h>


extern "C" {

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

JNIEXPORT jlong JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nCreateContext(JNIEnv* env, jobject, jboolean isCore, jlong shareWith) {
    CGLContextObj context;

    CGLPixelFormatObj pix;
    GLint num;
    CGLPixelFormatAttribute attributes[4] = {
            kCGLPFAAccelerated,
            kCGLPFAOpenGLProfile,
            (CGLPixelFormatAttribute) (isCore ? kCGLOGLPVersion_GL4_Core : kCGLOGLPVersion_Legacy),
            (CGLPixelFormatAttribute) 0
    };

    checkError(CGLChoosePixelFormat(attributes, &pix, &num));
    checkError(CGLCreateContext(pix, (CGLContextObj)shareWith, &context));
    checkError(CGLDestroyPixelFormat(pix));

    return (jlong)context;
}

JNIEXPORT jlong JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nGetCurrentContext(JNIEnv* env, jobject) {
    return (jlong)CGLGetCurrentContext();
}

JNIEXPORT jboolean JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nSetCurrentContext(JNIEnv* env, jobject, jlong context) {
    return checkError(CGLSetCurrentContext((CGLContextObj)context)) == kCGLNoError;
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nDeleteContext(JNIEnv* env, jobject, jlong context) {
    checkError(CGLDestroyContext((CGLContextObj)context));
}
}