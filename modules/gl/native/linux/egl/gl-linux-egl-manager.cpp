
#include "gl-linux-egl.h"


jni_linux_egl_manager(void, nSwapBuffers)(JNIEnv* env, jobject, jlong display, jlong window) {
    eglSwapBuffers(
        (EGLDisplay) display,
        (EGLSurface) window
    );
}

jni_linux_egl_manager(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong display, jint swapInterval) {
    eglSwapInterval(
        (EGLDisplay) display,
        swapInterval
    );
}
