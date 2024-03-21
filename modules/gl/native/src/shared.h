#include "jni.h"

typedef int GLint;
typedef unsigned int GLenum;

#define GL_FALSE                                    0
#define GL_TRUE                                     1
#define GL_MAJOR_VERSION                            0x821B
#define GL_MINOR_VERSION                            0x821C

typedef void (*glGetIntegervPtr)(GLenum pname, GLint* data);


#define winglfun(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_WinGLPlatform_##fun
#define linuxglfun(returnType, fun) extern "C" __attribute__((unused)) JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_LinuxGLPlatform_##fun
#define macosglfun(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_MacGLPlatform_##fun

jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}