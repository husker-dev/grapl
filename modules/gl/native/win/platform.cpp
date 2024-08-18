#define UNICODE

#include "grapl-gl-win.h"

jni_win_platform(void, nSwapBuffers)(JNIEnv* env, jobject, jlong _dc) {
    glFlush();
    SwapBuffers((HDC)_dc);
}

jni_win_platform(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong hwnd, jint value) {
    wglSwapIntervalEXT(value);
}