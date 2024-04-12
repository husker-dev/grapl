#include "jni.h"

#define jni_win_window(returnType, fun)	    extern "C"                         JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_WinWindowPeer_##fun

#define jni_linux_window(returnType, fun)   extern "C" __attribute__((unused)) JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_LinuxWindowPeer_##fun

#define jni_macos_window(returnType, fun)   extern "C"                         JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_MacWindowPeer_##fun
#define jni_macos_utils(returnType, fun)    extern "C"                         JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_util_MacOSUtils_##fun

static jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}