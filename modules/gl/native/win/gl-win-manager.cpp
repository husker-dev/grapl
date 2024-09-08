#include "gl-win.h"


jni_win_manager(void, nSwapBuffers)(JNIEnv* env, jobject, jlong _dc) {
    SwapBuffers((HDC)_dc);
}

jni_win_manager(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong hwnd, jint value) {
    wglSwapIntervalEXT(value);
}