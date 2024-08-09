#include "grapl-wayland.h"



jni_wayland_platform(jlong, nConnectDisplay)(JNIEnv* env, jobject) {
    return (jlong)wl_display_connect(NULL);
}

jni_wayland_platform(void, nPeekMessage)(JNIEnv* env, jobject) {

}

jni_wayland_platform(void, nWaitMessage)(JNIEnv* env, jobject, jint timeout) {

}

jni_wayland_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject) {

}