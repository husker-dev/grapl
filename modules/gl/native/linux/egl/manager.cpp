#include "grapl-gl-linux-egl.h"

extern eglSwapBuffersPtr eglSwapBuffers;
extern eglSwapIntervalPtr eglSwapInterval;


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
