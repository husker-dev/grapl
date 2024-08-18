#include "../shared/grapl-gl.h"

#include <windows.h>

#define jni_win_context(returnType, fun)     extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_win_WGLContext_##fun
#define jni_win_platform(returnType, fun)	 extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_win_WGLManager_##fun


/*
    WGL constants
*/
#define WGL_DRAW_TO_WINDOW_ARB                      0x2001
#define WGL_SUPPORT_OPENGL_ARB                      0x2010
#define WGL_ACCELERATION_ARB                        0x2003
#define WGL_PIXEL_TYPE_ARB                          0x2013
#define WGL_COLOR_BITS_ARB                          0x2014
#define WGL_DEPTH_BITS_ARB                          0x2022
#define WGL_STENCIL_BITS_ARB                        0x2023
#define WGL_FULL_ACCELERATION_ARB                   0x2027
#define WGL_TYPE_RGBA_ARB                           0x202B
#define WGL_DOUBLE_BUFFER_ARB                       0x2011

#define WGL_CONTEXT_PROFILE_MASK_ARB                0x9126
#define WGL_CONTEXT_MAJOR_VERSION_ARB               0x2091
#define WGL_CONTEXT_MINOR_VERSION_ARB               0x2092
#define WGL_CONTEXT_FLAGS_ARB                       0x2094
#define WGL_CONTEXT_CORE_PROFILE_BIT_ARB            0x00000001
#define WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB   0x00000002
#define WGL_CONTEXT_DEBUG_BIT_ARB                   0x00000001

#define ERROR_INVALID_VERSION_ARB                   0x2095
#define ERROR_INVALID_PROFILE_ARB                   0x2096

/*
    GL functions
*/
typedef HGLRC (*wglCreateContextPtr)(HDC);
typedef BOOL (*wglDeleteContextPtr)(HGLRC);
typedef HGLRC (*wglGetCurrentContextPtr)();
typedef HDC (*wglGetCurrentDCPtr)();
typedef BOOL (*wglMakeCurrentPtr)(HDC, HGLRC);
typedef void* (*wglGetProcAddressPtr)(const char*);
typedef HGLRC (*wglCreateContextAttribsARBPtr)(HDC hDC, HGLRC hShareContext, const int* attribList);
typedef BOOL (*wglChoosePixelFormatARBPtr)(HDC hdc, const int* piAttribIList, const FLOAT* pfAttribFList, UINT nMaxFormats, int* piFormats, UINT* nNumFormats);
typedef BOOL (*wglSwapIntervalEXTPtr)(int interval);

static wglCreateContextPtr                 _wglCreateContext;
static wglDeleteContextPtr                 _wglDeleteContext;
static wglGetCurrentContextPtr             _wglGetCurrentContext;
static wglGetCurrentDCPtr                  _wglGetCurrentDC;
static wglMakeCurrentPtr                   _wglMakeCurrent;
static wglGetProcAddressPtr                _wglGetProcAddress;
static wglChoosePixelFormatARBPtr          wglChoosePixelFormatARB;
static wglCreateContextAttribsARBPtr       wglCreateContextAttribsARB;
static wglSwapIntervalEXTPtr               wglSwapIntervalEXT;


/*
    GL variables
*/
static HMODULE libGL;
static HDC dc = nullptr;

/*
    Functions
*/
static void* _GetProcAddress(const char* name) {
    if(libGL == NULL){
        libGL = LoadLibraryW(L"opengl32.dll");
        _wglGetProcAddress = (wglGetProcAddressPtr)GetProcAddress(libGL, "wglGetProcAddress");
    }
    void* procAddr = _wglGetProcAddress(name);
    if(procAddr == NULL)
        procAddr = GetProcAddress(libGL, name);
    return procAddr;
}

static void printError(const char* func, const char* error){
    printf("WGL error at '%s': %s", func, error);
}

static void checkError(const char* func){
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
        sprintf(res, "0x%X (Unknown)", code);
        printError(func, res);
    }
}

static PIXELFORMATDESCRIPTOR createPFD(){
    PIXELFORMATDESCRIPTOR pfd = {};
    pfd.nVersion = 1;
    pfd.iPixelType = PFD_TYPE_RGBA;
    pfd.cColorBits = 24;
    pfd.cDepthBits = 32;
    pfd.nSize = sizeof(pfd);
    return pfd;
}

static int* createPixelAttributes(){
    int pixel_attributes[] = {
            WGL_DRAW_TO_WINDOW_ARB,     GL_TRUE,
            WGL_SUPPORT_OPENGL_ARB,     GL_TRUE,
            WGL_DOUBLE_BUFFER_ARB,      GL_TRUE,
            WGL_ACCELERATION_ARB,       WGL_FULL_ACCELERATION_ARB,
            WGL_PIXEL_TYPE_ARB,         WGL_TYPE_RGBA_ARB,
            WGL_COLOR_BITS_ARB,         32,
            WGL_DEPTH_BITS_ARB,         24,
            WGL_STENCIL_BITS_ARB,       8,
            0
    };
    return &pixel_attributes[0];
}