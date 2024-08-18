#define UNICODE

#include "grapl-gl-win.h"

static void getContextDetailsWGL(GLDetails* details, HGLRC rc, HDC dc){
    HGLRC oldRC = _wglGetCurrentContext();
    HDC oldDC = _wglGetCurrentDC();

    _wglMakeCurrent(dc, rc);
    getContextDetails(details);
    _wglMakeCurrent(oldDC, oldRC);
}

jni_win_context(void, nInitFunctions)(JNIEnv* env, jobject) {
    // Load standard WGL functions
    _wglCreateContext = (wglCreateContextPtr)_GetProcAddress("wglCreateContext");
    _wglDeleteContext = (wglDeleteContextPtr)_GetProcAddress("wglDeleteContext");
    _wglGetCurrentContext = (wglGetCurrentContextPtr)_GetProcAddress("wglGetCurrentContext");
    _wglGetCurrentDC = (wglGetCurrentDCPtr)_GetProcAddress("wglGetCurrentDC");
    _wglMakeCurrent = (wglMakeCurrentPtr)_GetProcAddress("wglMakeCurrent");

    HDC oldDC = _wglGetCurrentDC();
    HGLRC oldRC = _wglGetCurrentContext();

    PIXELFORMATDESCRIPTOR pfd = createPFD();

    WNDCLASS wc = {};
    wc.lpfnWndProc = DefWindowProc;
    wc.hInstance = GetModuleHandle(NULL);
    wc.lpszClassName = L"grapl-gl";
    RegisterClass(&wc);

    // Create dummy window to initialize function
    {
        HWND hwnd = CreateWindow(
                L"grapl-gl", L"",
                WS_OVERLAPPEDWINDOW,
                0, 0,
                100, 100,
                NULL, NULL,
                GetModuleHandle(NULL),
                NULL);
        HDC dc = GetDC(hwnd);

        int pixel_format;
        if (!(pixel_format = ChoosePixelFormat(dc, &pfd)))
            checkError("ChoosePixelFormat");
        if (!SetPixelFormat(dc, pixel_format, &pfd))
            checkError("SetPixelFormat");

        HGLRC rc = _wglCreateContext(dc);
        _wglMakeCurrent(dc, rc);

        // Load functions
        wglChoosePixelFormatARB = (wglChoosePixelFormatARBPtr) _GetProcAddress("wglChoosePixelFormatARB");
        wglCreateContextAttribsARB = (wglCreateContextAttribsARBPtr) _GetProcAddress("wglCreateContextAttribsARB");
        wglSwapIntervalEXT = (wglSwapIntervalEXTPtr)_GetProcAddress("wglSwapIntervalEXT");
        glGetIntegerv = (glGetIntegervPtr) _GetProcAddress("glGetIntegerv");
        glDebugMessageCallbackARB = (glDebugMessageCallbackARBPtr) _GetProcAddress("glDebugMessageCallbackARB");

        // Destroy dummy context
        _wglMakeCurrent(oldDC, oldRC);
        _wglDeleteContext(rc);
        ReleaseDC(hwnd, dc);
        DestroyWindow(hwnd);
    }

    // Create window with ARB pixel attributes
    HWND hwnd = CreateWindow(
            L"grapl-gl", L"",
            WS_OVERLAPPEDWINDOW,
            0, 0,
            100, 100,
            NULL, NULL,
            GetModuleHandle(NULL),
            NULL);
    dc = GetDC(hwnd);

    int pixel_format_arb;
    UINT pixel_formats_count;

    int* pixel_attributes = createPixelAttributes();
    if (!wglChoosePixelFormatARB(dc, pixel_attributes, NULL, 1, &pixel_format_arb, &pixel_formats_count))
        checkError("wglChoosePixelFormatARB");
    if (!SetPixelFormat(dc, pixel_format_arb, &pfd))
        checkError("SetPixelFormat (wgl)");
}


jni_win_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareRc, jint majorVersion, jint minorVersion, jboolean debug) {
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

    GLDetails details = {};
    getContextDetailsWGL(&details, rc, dc);

    return createLongArray(env, {
        (jlong) rc,
        (jlong) dc,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}


jni_win_context(jlongArray, nCreateContextForWindow)(JNIEnv* env, jobject, jlong hwnd, jboolean isCore, jlong shareRc, jint majorVersion, jint minorVersion, jboolean debug) {
    HWND hwnd = (HNWD)hwnd;
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

    GLDetails details = {};
    getContextDetailsWGL(&details, rc, dc);

    return createLongArray(env, {
        (jlong) rc,
        (jlong) dc,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_win_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    GLDetails details = {};
    getContextDetails(&details);

    return createLongArray(env, {
        (jlong) _wglGetCurrentContext(),
        (jlong) _wglGetCurrentDC(),
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_win_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong dc, jlong rc) {
    return _wglMakeCurrent(
        (HDC)dc,
        (HGLRC)rc
    );
}

jni_win_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong rc) {
    _wglDeleteContext((HGLRC)rc);
}

jni_win_context(void, nBindDebugCallback)(JNIEnv* env, jobject, jclass callbackClass) {
    bindDefaultDebugFunction(env, callbackClass, _wglGetCurrentContext());
}
