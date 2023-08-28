#include <iostream>
#include <jni.h>
#include <OpenGL/OpenGL.h>


extern "C" {


JNIEXPORT jlong JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nCreateContext(JNIEnv* env, jobject, jboolean isCore, jlong shareWith) {
    CGLContextObj context;

    CGLPixelFormatAttribute attributes[4] = {
            kCGLPFAAccelerated,
            kCGLPFAOpenGLProfile,
            (CGLPixelFormatAttribute) (isCore ? kCGLOGLPVersion_3_2_Core : kCGLOGLPVersion_Legacy),
            (CGLPixelFormatAttribute) 0
    };

    CGLPixelFormatObj pix;
    GLint num;

    CGLChoosePixelFormat(attributes, &pix, &num);
    CGLCreateContext(pix, (CGLContextObj)shareWith, &context);
    CGLDestroyPixelFormat(pix);

    return (jlong)context;
}

JNIEXPORT jlong JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nGetCurrentContext(JNIEnv* env, jobject) {
    return (jlong)CGLGetCurrentContext();
}

JNIEXPORT jboolean JNICALL Java_com_huskerdev_ojgl_platforms_MacGLPlatform_nSetCurrentContext(JNIEnv* env, jobject, jlong context) {
    return CGLSetCurrentContext((CGLContextObj)context) == 0;
}
}