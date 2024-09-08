#ifndef GRAPL_GL_LINUX_EGL_H
#define GRAPL_GL_LINUX_EGL_H

#include "../grapl-gl-linux.h"
#include <dlfcn.h>

static void* libEGL;

#define jni_linux_egl_context(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_egl_EGLContext_##fun
#define jni_linux_egl_manager(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_egl_EGLManager_##fun


#define EGL_CONTEXT_MAJOR_VERSION 0x3098
#define EGL_CONTEXT_MINOR_VERSION 0x30FB
#define EGL_CONTEXT_OPENGL_PROFILE_MASK 0x30FD
#define EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT 0x00000001
#define EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT 0x00000002
#define EGL_CONTEXT_OPENGL_DEBUG 0x31B0
#define EGL_TRUE 1
#define EGL_FALSE 0
#define EGL_NO_SURFACE (EGLSurface)0
#define EGL_DEFAULT_DISPLAY (EGLNativeDisplayType)0
#define EGL_SURFACE_TYPE 0x3033
#define EGL_WINDOW_BIT 0x0004
#define EGL_CONFORMANT 0x3042
#define EGL_OPENGL_BIT 0x0008
#define EGL_RENDERABLE_TYPE 0x3040
#define EGL_COLOR_BUFFER_TYPE 0x303F
#define EGL_RGB_BUFFER 0x308E
#define EGL_RED_SIZE 0x3024
#define EGL_GREEN_SIZE 0x3023
#define EGL_BLUE_SIZE 0x3022
#define EGL_DEPTH_SIZE 0x3025
#define EGL_STENCIL_SIZE 0x3026
#define EGL_NONE 0x3038
#define EGL_READ 0x305A
#define EGL_DRAW 0x3059
#define EGL_OPENGL_API 0x30A2
#define EGL_GL_COLORSPACE 0x309D
#define EGL_GL_COLORSPACE_LINEAR 0x308A
#define EGL_RENDER_BUFFER 0x3086
#define EGL_BACK_BUFFER 0x3084

typedef void* EGLDisplay;
typedef void* EGLConfig;
typedef void* EGLContext;
typedef void* EGLSurface;
typedef void* EGLNativeDisplayType;
typedef void *EGLNativeWindowType;
typedef int EGLint;
typedef unsigned int EGLBoolean;
typedef unsigned int EGLenum;

typedef void* (*eglGetProcAddressPtr)(const char * procname);

typedef EGLBoolean (*eglBindAPIPtr)(EGLenum api);
typedef EGLDisplay (*eglGetDisplayPtr)(EGLNativeDisplayType display_id);
typedef EGLBoolean (*eglInitializePtr)(EGLDisplay dpy, EGLint* major, EGLint* minor);
typedef EGLBoolean (*eglChooseConfigPtr)(EGLDisplay dpy, const EGLint* attrib_list, EGLConfig* configs, EGLint config_size, EGLint* num_config);
typedef EGLContext (*eglCreateContextPtr)(EGLDisplay dpy, EGLConfig config, EGLContext share_context, const EGLint* attrib_list);
typedef EGLSurface (*eglCreateWindowSurfacePtr)(EGLDisplay dpy, EGLConfig config, EGLNativeWindowType win, const EGLint* attrib_list);
typedef EGLDisplay (*eglGetCurrentDisplayPtr)(void);
typedef EGLSurface (*eglGetCurrentSurfacePtr)(EGLint readdraw);
typedef EGLContext (*eglGetCurrentContextPtr)(void);
typedef EGLBoolean (*eglMakeCurrentPtr)(EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
typedef EGLBoolean (*eglDestroyContextPtr)(EGLDisplay dpy, EGLContext ctx);
typedef EGLBoolean (*eglSwapBuffersPtr)(EGLDisplay dpy, EGLSurface surface);
typedef EGLBoolean (*eglSwapIntervalPtr)(EGLDisplay dpy, EGLint interval);


extern eglGetProcAddressPtr      eglGetProcAddress;

extern eglBindAPIPtr             eglBindAPI;
extern eglGetDisplayPtr          eglGetDisplay;
extern eglInitializePtr          eglInitialize;
extern eglChooseConfigPtr        eglChooseConfig;
extern eglCreateContextPtr       eglCreateContext;
extern eglCreateWindowSurfacePtr eglCreateWindowSurface;
extern eglGetCurrentDisplayPtr   eglGetCurrentDisplay;
extern eglGetCurrentSurfacePtr   eglGetCurrentSurface;
extern eglGetCurrentContextPtr   eglGetCurrentContext;
extern eglMakeCurrentPtr         eglMakeCurrent;
extern eglDestroyContextPtr      eglDestroyContext;
extern eglSwapBuffersPtr         eglSwapBuffers;
extern eglSwapIntervalPtr        eglSwapInterval;

#endif