#define UNICODE

#include "grapl-gl-win.h"

jni_win_platform(jlongArray, nCreateGLWindow)(JNIEnv* env, jobject, jboolean isCore, jlong shareRc, jint majorVersion, jint minorVersion, jboolean debug) {
    checkBasicFunctions();

    HWND hwnd = CreateWindow(
                    L"grapl-gl", L"",
                    WS_OVERLAPPEDWINDOW,
                    CW_USEDEFAULT, CW_USEDEFAULT,
                    10, 10,
                    NULL, NULL,
                    GetModuleHandle(NULL),
                    NULL);
    HDC dc = GetDC(hwnd);

    int pixel_format_arb;
    UINT pixel_formats_count;

    PIXELFORMATDESCRIPTOR pfd = createPFD();
    int* pixel_attributes = createPixelAttributes();
    if (!wglChoosePixelFormatARB(dc, pixel_attributes, NULL, 1, &pixel_format_arb, &pixel_formats_count))
        checkError("wglChoosePixelFormatARB (nCreateWindow)");
    if (!SetPixelFormat(dc, pixel_format_arb, &pfd))
        checkError("SetPixelFormat (nCreateWindow, wgl)");

    GLint context_attributes[] = {
            WGL_CONTEXT_PROFILE_MASK_ARB, isCore ? WGL_CONTEXT_CORE_PROFILE_BIT_ARB : WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            WGL_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            WGL_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            WGL_CONTEXT_FLAGS_ARB, debug ? WGL_CONTEXT_DEBUG_BIT_ARB : 0,
            0
    };

    HGLRC rc;
    if (!(rc = wglCreateContextAttribsARB(dc, (HGLRC)shareRc, context_attributes)))
        checkError("wglCreateContextAttribsARB");

    HGLRC oldRC = _wglGetCurrentContext();
    HDC oldDC = _wglGetCurrentDC();
    GLint major, minor, flags;

    _wglMakeCurrent(dc, rc);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    glGetIntegerv(GL_CONTEXT_FLAGS, &flags);
    _wglMakeCurrent(oldDC, oldRC);

    return createLongArray(env, {
        (jlong)hwnd, (jlong)rc, (jlong)dc, (jlong)major, (jlong)minor, (flags & GL_CONTEXT_FLAG_DEBUG_BIT) != 0
    });
}

jni_win_platform(void, nSwapBuffers)(JNIEnv* env, jobject, jlong _dc) {
    glFlush();
    SwapBuffers((HDC)_dc);
}

jni_win_platform(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong hwnd, jint value) {
    wglSwapIntervalEXT(value);
}