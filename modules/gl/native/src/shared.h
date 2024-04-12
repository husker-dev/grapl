#include "jni.h"
#include <iostream>

typedef int GLint;
typedef unsigned int GLenum;

#define GL_FALSE                                    0
#define GL_TRUE                                     1
#define GL_MAJOR_VERSION                            0x821B
#define GL_MINOR_VERSION                            0x821C

typedef void (*glGetIntegervPtr)(GLenum pname, GLint* data);
typedef void (*glFlushPtr)();
static glGetIntegervPtr glGetIntegerv;
static glFlushPtr glFlush;

#define jni_win_context(returnType, fun)     extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_win_WGLContext_##fun
#define jni_win_platform(returnType, fun)	 extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_win_WinGLPlatform_##fun

#define jni_macos_context(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_CGLContext_##fun
#define jni_macos_nscontext(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_NSGLContext_##fun
#define jni_macos_platform(returnType, fun)	 extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_macos_MacGLPlatform_##fun

#define linuxglfun(returnType, fun)     extern "C" __attribute__((unused)) JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_LinuxGLPlatform_##fun

static jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}