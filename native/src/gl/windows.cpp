#define UNICODE

#include "../shared.h"
#include "shared-gl.h"


#define WGL_DRAW_TO_WINDOW_ARB                      0x2001
#define WGL_SUPPORT_OPENGL_ARB                      0x2010
#define WGL_ACCELERATION_ARB                        0x2003
#define WGL_PIXEL_TYPE_ARB                          0x2013
#define WGL_COLOR_BITS_ARB                          0x2014
#define WGL_DEPTH_BITS_ARB                          0x2022
#define WGL_STENCIL_BITS_ARB                        0x2023
#define WGL_FULL_ACCELERATION_ARB                   0x2027
#define WGL_TYPE_RGBA_ARB                           0x202B

#define WGL_CONTEXT_PROFILE_MASK_ARB                0x9126
#define WGL_CONTEXT_MAJOR_VERSION_ARB               0x2091
#define WGL_CONTEXT_MINOR_VERSION_ARB               0x2092
#define WGL_CONTEXT_CORE_PROFILE_BIT_ARB            0x00000001
#define WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB   0x00000002

#define ERROR_INVALID_VERSION_ARB                   0x2095
#define ERROR_INVALID_PROFILE_ARB                   0x2096

typedef HGLRC(WINAPI* wglCreateContextAttribsARBPtr) (HDC hDC, HGLRC hShareContext, const int* attribList);
typedef BOOL(WINAPI* wglChoosePixelFormatARBPtr) (HDC hdc, const int* piAttribIList, const FLOAT* pfAttribFList, UINT nMaxFormats, int* piFormats, UINT* nNumFormats);

wglChoosePixelFormatARBPtr          wglChoosePixelFormatARB;
wglCreateContextAttribsARBPtr       wglCreateContextAttribsARB;
glGetIntegervPtr                    glGetIntegerv;

HDC dc = nullptr;


void printError(const char* func, const char* error){
    std::cout << "WGL error at '" << func << "': " << error << std::endl;
}

void checkError(const char* func){
    DWORD code = GetLastError();
    if(code == ERROR_INVALID_VERSION_ARB) printError(func, "ERROR_INVALID_VERSION_ARB (invalid OpenGL version)");
    else if(code == ERROR_INVALID_PROFILE_ARB) printError(func, "ERROR_INVALID_PROFILE_ARB (requested OpenGL profile is not supported)");
    else if(code == ERROR_INVALID_OPERATION) printError(func, "ERROR_INVALID_OPERATION (share context is not a valid context handle or is not compatible)");
    else if(code == ERROR_DC_NOT_FOUND) printError(func, "ERROR_DC_NOT_FOUND (DC is not a valid device context handle)");
    else if(code == ERROR_INVALID_PIXEL_FORMAT) printError(func, "ERROR_INVALID_PIXEL_FORMAT (invalid pixel format)");
    else if(code == ERROR_NO_SYSTEM_RESOURCES) printError(func, "ERROR_NO_SYSTEM_RESOURCES (server does not have enough resources to allocate the new context)");
    else if(code == ERROR_INVALID_PARAMETER) printError(func, "ERROR_INVALID_PARAMETER (an unrecognized attribute is present in <attribList>)");
    else {
        char res[20];
        sprintf_s(res, "0x%X (Unknown)", code);
        printError(func, res);
    }
}

void checkBasicFunctions() {
    if (dc == nullptr) {
        HDC oldDC = wglGetCurrentDC();
        HGLRC oldRC = wglGetCurrentContext();

        PIXELFORMATDESCRIPTOR pfd = {};
        pfd.nVersion = 1;
        pfd.iPixelType = PFD_TYPE_RGBA;
        pfd.cColorBits = 24;
        pfd.cDepthBits = 32;
        pfd.nSize = sizeof(pfd);

        WNDCLASS wc = {};
        wc.lpfnWndProc = DefWindowProc;
        wc.hInstance = GetModuleHandle(NULL);
        wc.lpszClassName = L"offscreen-jgl";
        RegisterClass(&wc);

        // Create dummy window to initialize function
        {
            HWND hwnd = CreateWindow(
                    L"offscreen-jgl", L"",
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

            HGLRC rc = wglCreateContext(dc);
            wglMakeCurrent(dc, rc);

            // Load functions
            wglChoosePixelFormatARB = (wglChoosePixelFormatARBPtr) getProcAddress("wglChoosePixelFormatARB");
            wglCreateContextAttribsARB = (wglCreateContextAttribsARBPtr) getProcAddress("wglCreateContextAttribsARB");
            glGetIntegerv = (glGetIntegervPtr) getProcAddress("glGetIntegerv");

            // Destroy dummy context
            wglMakeCurrent(oldDC, oldRC);
            wglDeleteContext(rc);
            ReleaseDC(hwnd, dc);
            DestroyWindow(hwnd);
        }

        // Create window with ARB pixel attributes
        HWND hwnd = CreateWindow(
                L"offscreen-jgl", L"",
                WS_OVERLAPPEDWINDOW,
                0, 0,
                100, 100,
                NULL, NULL,
                GetModuleHandle(NULL),
                NULL);
        dc = GetDC(hwnd);

        int pixel_format_arb;
        UINT pixel_formats_count;

        int pixel_attributes[] = {
                WGL_DRAW_TO_WINDOW_ARB,     GL_TRUE,
                WGL_SUPPORT_OPENGL_ARB,     GL_TRUE,
                WGL_ACCELERATION_ARB,       WGL_FULL_ACCELERATION_ARB,
                WGL_PIXEL_TYPE_ARB,         WGL_TYPE_RGBA_ARB,
                WGL_COLOR_BITS_ARB,         32,
                WGL_DEPTH_BITS_ARB,         24,
                WGL_STENCIL_BITS_ARB,       8,
                0
        };
        if (!wglChoosePixelFormatARB(dc, pixel_attributes, NULL, 1, &pixel_format_arb, &pixel_formats_count))
            checkError("wglChoosePixelFormatARB");
        if (!SetPixelFormat(dc, pixel_format_arb, &pfd))
            checkError("SetPixelFormat (wgl)");
    }
}

winglfun(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    checkBasicFunctions();

    GLint major, minor;
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);

    jlong array[4] = { (jlong)wglGetCurrentContext(), (jlong)wglGetCurrentDC(), (jlong)major, (jlong)minor };
    return createLongArray(env, 4, array);
}

winglfun(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong dc, jlong rc) {
    checkBasicFunctions();
    return wglMakeCurrent((HDC)dc, (HGLRC)rc);
}

winglfun(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareRc, jint majorVersion, jint minorVersion) {
    checkBasicFunctions();

    GLint context_attributes[] = {
            WGL_CONTEXT_PROFILE_MASK_ARB, isCore ? WGL_CONTEXT_CORE_PROFILE_BIT_ARB : WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            WGL_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            WGL_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            0
    };

    HGLRC rc;
    if (!(rc = wglCreateContextAttribsARB(dc, (HGLRC)shareRc, context_attributes)))
        checkError("wglCreateContextAttribsARB");

    HGLRC oldRC = wglGetCurrentContext();
    HDC oldDC = wglGetCurrentDC();
    GLint major, minor;

    wglMakeCurrent(dc, rc);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    wglMakeCurrent(oldDC, oldRC);

    jlong array[] = { (jlong)rc, (jlong)dc, (jlong)major, (jlong)minor };
    return createLongArray(env, 4, array);
}

winglfun(void, nDeleteContext)(JNIEnv* env, jobject, jlong rc) {
    checkBasicFunctions();
    wglDeleteContext((HGLRC)rc);
}

