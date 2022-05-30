#define UNICODE

#include <d3d9.h>
#include <gl/GL.h>

#include <wingdi.h>
#include "wgl.h"
#include <jni.h>
#include <iostream>


// Emulate internal JavaFX's code for memory mapping
struct IManagedResource {
    void* virtualTable;

    IManagedResource* pPrev;
    IManagedResource* pNext;
};

struct D3DResource {
    IManagedResource managedResource;
    IDirect3DResource9* pResource;
    IDirect3DSwapChain9* pSwapChain;
    IDirect3DSurface9* pSurface;
    IDirect3DSurface9* pDepthSurface;
    IDirect3DTexture9* pTexture;

    D3DSURFACE_DESC desc;
};

HDC dc = nullptr;

PFNWGLCHOOSEPIXELFORMATARBPROC          wglChoosePixelFormatARB;
PFNWGLCREATECONTEXTATTRIBSARBPROC       wglCreateContextAttribsARB;

extern "C" {

jlongArray createLongArray(JNIEnv* env, int size, jlong* elements) {
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, elements);
    return result;
}

void checkBasicFunctions() {
    if (dc == nullptr) {
        HDC oldDC = wglGetCurrentDC();
        HGLRC oldRC = wglGetCurrentContext();

        PIXELFORMATDESCRIPTOR pfd = {};
        pfd.nSize = sizeof(pfd);

        WNDCLASS wc = {};
        wc.lpfnWndProc = DefWindowProc;
        wc.hInstance = GetModuleHandle(NULL);
        wc.lpszClassName = L"ojgl";
        RegisterClass(&wc);

        // Create dummy window to initialize function
        {
            HWND hwnd = CreateWindow(
                    L"ojgl", L"",
                    WS_OVERLAPPEDWINDOW,
                    0, 0,
                    100, 100,
                    NULL, NULL,
                    GetModuleHandle(NULL),
                    NULL);
            HDC dc = GetDC(hwnd);

            int pixel_format = 0;
            if (!(pixel_format = ChoosePixelFormat(dc, &pfd)))
                std::cout << "Failed to choose pixel format" << std::endl;
            if (!SetPixelFormat(dc, pixel_format, &pfd))
                std::cout << "Failed to set pixel format" << std::endl;

            HGLRC rc = wglCreateContext(dc);
            wglMakeCurrent(dc, rc);

            // Load functions
            wglChoosePixelFormatARB = (PFNWGLCHOOSEPIXELFORMATARBPROC)wglGetProcAddress("wglChoosePixelFormatARB");
            wglCreateContextAttribsARB = (PFNWGLCREATECONTEXTATTRIBSARBPROC)wglGetProcAddress("wglCreateContextAttribsARB");

            // Destroy dummy context
            wglMakeCurrent(oldDC, oldRC);
            wglDeleteContext(rc);
            ReleaseDC(hwnd, dc);
            DestroyWindow(hwnd);
        }

        // Create window with ARB pixel attributes
        HWND hwnd = CreateWindow(
                L"openglfx", L"",
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
                WGL_DRAW_TO_WINDOW_ARB, GL_TRUE,
                WGL_SUPPORT_OPENGL_ARB, GL_TRUE,
                0
        };
        if (!wglChoosePixelFormatARB(dc, pixel_attributes, NULL, 1, &pixel_format_arb, &pixel_formats_count))
            std::cout << "Failed to choose supported pixel format (WGL)" << std::endl;
        if (!SetPixelFormat(dc, pixel_format_arb, &pfd))
            std::cout << "Failed to set pixel format (WGL)" << std::endl;
    }
}

JNIEXPORT jlongArray JNICALL Java_com_huskerdev_ojgl_platforms_WinGLPlatform_nGetCurrentContext(JNIEnv* env, jobject) {
    checkBasicFunctions();

    jlong array[2] = { (jlong)wglGetCurrentContext(), (jlong)wglGetCurrentDC() };
    return createLongArray(env, 2, array);
}

JNIEXPORT jboolean JNICALL Java_com_huskerdev_ojgl_platforms_WinGLPlatform_nSetCurrentContext(JNIEnv* env, jobject, jlong dc, jlong rc) {
    checkBasicFunctions();
    return wglMakeCurrent((HDC)dc, (HGLRC)rc);
}

JNIEXPORT jlongArray JNICALL Java_com_huskerdev_ojgl_platforms_WinGLPlatform_nCreateContext(JNIEnv* env, jobject, jboolean isCore, jlong shareRc) {
    checkBasicFunctions();

    GLint context_attributes[] = {
            WGL_CONTEXT_PROFILE_MASK_ARB, isCore ? WGL_CONTEXT_CORE_PROFILE_BIT_ARB : WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            0
    };

    HGLRC rc;
    if (!(rc = wglCreateContextAttribsARB(dc, (HGLRC)shareRc, context_attributes)))
    std::cout << "Failed to create context (WGL)" << std::endl;

    jlong array[2] = { (jlong)rc, (jlong)dc };
    return createLongArray(env, 2, array);
}
}