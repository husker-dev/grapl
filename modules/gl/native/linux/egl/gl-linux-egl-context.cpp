#include "gl-linux-egl.h"

eglGetProcAddressPtr      eglGetProcAddress;

eglBindAPIPtr             eglBindAPI;
eglGetDisplayPtr          eglGetDisplay;
eglInitializePtr          eglInitialize;
eglChooseConfigPtr        eglChooseConfig;
eglCreateContextPtr       eglCreateContext;
eglCreateWindowSurfacePtr eglCreateWindowSurface;
eglGetCurrentDisplayPtr   eglGetCurrentDisplay;
eglGetCurrentSurfacePtr   eglGetCurrentSurface;
eglGetCurrentContextPtr   eglGetCurrentContext;
eglMakeCurrentPtr         eglMakeCurrent;
eglDestroyContextPtr      eglDestroyContext;
eglSwapBuffersPtr         eglSwapBuffers;
eglSwapIntervalPtr        eglSwapInterval;

glGetIntegervPtr          glGetIntegerv;
glGetStringiPtr           glGetStringi;
glDebugMessageCallbackARBPtr glDebugMessageCallbackARB;


static void getContextDetailsEGL(GLDetails* details, EGLDisplay display, EGLSurface surfaceRead, EGLSurface surfaceWrite, EGLContext context){
    EGLDisplay oldDisplay = eglGetCurrentDisplay();
    EGLSurface oldSurfaceRead = eglGetCurrentSurface(EGL_READ);
    EGLSurface oldSurfaceWrite = eglGetCurrentSurface(EGL_DRAW);
    EGLContext oldContext = eglGetCurrentContext();

    eglMakeCurrent(display, surfaceRead, surfaceWrite, context);
    getContextDetails(details);
    eglMakeCurrent(oldDisplay, oldSurfaceRead, oldSurfaceWrite, oldContext);
}


jni_linux_egl_context(void, nInitFunctions)(JNIEnv* env, jobject) {
    static const char *NAMES[] = { "libEGL.so.1", "libEGL.so" };
    for(int i = 0; i < 4; i++)
        if((libEGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
            break;

    eglGetProcAddress = (eglGetProcAddressPtr)dlsym(libEGL, "eglGetProcAddress");

    eglBindAPI = (eglBindAPIPtr)eglGetProcAddress("eglBindAPI");
    eglGetDisplay = (eglGetDisplayPtr)eglGetProcAddress("eglGetDisplay");
    eglInitialize = (eglInitializePtr)eglGetProcAddress("eglInitialize");
    eglChooseConfig = (eglChooseConfigPtr)eglGetProcAddress("eglChooseConfig");
    eglCreateContext = (eglCreateContextPtr)eglGetProcAddress("eglCreateContext");
    eglCreateWindowSurface = (eglCreateWindowSurfacePtr)eglGetProcAddress("eglCreateWindowSurface");
    eglGetCurrentDisplay = (eglGetCurrentDisplayPtr)eglGetProcAddress("eglGetCurrentDisplay");
    eglGetCurrentSurface = (eglGetCurrentSurfacePtr)eglGetProcAddress("eglGetCurrentSurface");
    eglGetCurrentContext = (eglGetCurrentContextPtr)eglGetProcAddress("eglGetCurrentContext");
    eglMakeCurrent = (eglMakeCurrentPtr)eglGetProcAddress("eglMakeCurrent");
    eglDestroyContext = (eglDestroyContextPtr)eglGetProcAddress("eglDestroyContext");
    eglSwapBuffers = (eglSwapBuffersPtr)eglGetProcAddress("eglSwapBuffers");
    eglSwapInterval = (eglSwapIntervalPtr)eglGetProcAddress("eglSwapInterval");

    glDebugMessageCallbackARB = (glDebugMessageCallbackARBPtr)eglGetProcAddress("glDebugMessageCallbackARB");
    glGetIntegerv = (glGetIntegervPtr)eglGetProcAddress("glGetIntegerv");
    glGetStringi = (glGetStringiPtr)eglGetProcAddress("glGetStringi");
}

jni_linux_egl_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion, jboolean debug) {
    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    eglInitialize(display, nullptr, nullptr);
    eglBindAPI(EGL_OPENGL_API);

    EGLConfig config;
    EGLint num_config;
    eglChooseConfig(display, nullptr, &config, 1, &num_config);

    const EGLint flags[] ={
        EGL_CONTEXT_MAJOR_VERSION, (majorVersion == -1) ? 1 : majorVersion,
        EGL_CONTEXT_MINOR_VERSION, (minorVersion == -1) ? 0 : minorVersion,
        EGL_CONTEXT_OPENGL_PROFILE_MASK, isCore ? EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT : EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT,
        EGL_CONTEXT_OPENGL_DEBUG, debug ? EGL_TRUE : EGL_FALSE,
        EGL_NONE
    };
    EGLContext context = eglCreateContext(display, config, (EGLContext)shareWith, flags);

    GLDetails details = {};
    getContextDetailsEGL(&details, display, EGL_NO_SURFACE, EGL_NO_SURFACE, context);

    return createLongArray(env, {
        (jlong) display,
        (jlong) EGL_NO_SURFACE,
        (jlong) EGL_NO_SURFACE,
        (jlong) context,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_linux_egl_context(jlongArray, nCreateContextForWindow)(JNIEnv* env, jobject,
    jlong _display,
    jlong window,
    jboolean isCore,
    jint msaa,
    jboolean doubleBuffering,
    jint redBits, jint greenBits, jint blueBits, jint alphaBits, jint depthBits, jint stencilBits,
    jboolean transparency,
    jlong shareWith,
    jint majorVersion,
    jint minorVersion,
    jboolean debug
) {
    EGLDisplay display = eglGetDisplay((EGLNativeDisplayType)_display);
    eglInitialize(display, nullptr, nullptr);
    eglBindAPI(EGL_OPENGL_API);

    // Craete config
    EGLConfig config;
    EGLint num_config;
    EGLint configAttr[] = {
        EGL_SURFACE_TYPE,       EGL_WINDOW_BIT,
        EGL_CONFORMANT,         EGL_OPENGL_BIT,
        EGL_RENDERABLE_TYPE,    EGL_OPENGL_BIT,
        EGL_COLOR_BUFFER_TYPE,  EGL_RGB_BUFFER,

        EGL_RED_SIZE,           redBits,
        EGL_GREEN_SIZE,         greenBits,
        EGL_BLUE_SIZE,          blueBits,
        EGL_ALPHA_SIZE,         alphaBits,
        EGL_DEPTH_SIZE,         depthBits,
        EGL_STENCIL_SIZE,       stencilBits,

        EGL_SAMPLE_BUFFERS,     msaa ? 1 : 0,
        EGL_SAMPLES,            msaa,

        EGL_TRANSPARENT_TYPE,   transparency ? EGL_TRANSPARENT_RGB : EGL_NONE,
        EGL_NONE
    };
    eglChooseConfig(display, configAttr, &config, 1, &num_config);

    // Create surface
    EGLint surfaceAttr[] = {
        EGL_GL_COLORSPACE, EGL_GL_COLORSPACE_LINEAR,
        EGL_RENDER_BUFFER, doubleBuffering ? EGL_BACK_BUFFER : EGL_SINGLE_BUFFER,
        EGL_NONE,
    };
    EGLSurface surface = eglCreateWindowSurface(display, config, (void*)window, surfaceAttr);

    // Create context
    EGLint flags[] = {
        EGL_CONTEXT_MAJOR_VERSION, (majorVersion == -1) ? 1 : majorVersion,
        EGL_CONTEXT_MINOR_VERSION, (minorVersion == -1) ? 0 : minorVersion,
        EGL_CONTEXT_OPENGL_PROFILE_MASK, isCore ? EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT : EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT,
        EGL_CONTEXT_OPENGL_DEBUG, debug ? EGL_TRUE : EGL_FALSE,
        EGL_NONE
    };
    EGLContext context = eglCreateContext(display, config, (EGLContext)shareWith, flags);

    GLDetails details = {};
    getContextDetailsEGL(&details, display, surface, surface, context);

    return createLongArray(env, {
        (jlong) display,
        (jlong) surface,
        (jlong) surface,
        (jlong) context,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_linux_egl_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    GLDetails details = {};
    getContextDetails(&details);

    return createLongArray(env, {
        (jlong) eglGetCurrentDisplay(),
        (jlong) eglGetCurrentSurface(EGL_READ),
        (jlong) eglGetCurrentSurface(EGL_DRAW),
        (jlong) eglGetCurrentContext(),
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_linux_egl_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong display, jlong surfaceRead, jlong surfaceWrite, jlong context) {
    return (jboolean)eglMakeCurrent(
        display == 0 ? eglGetCurrentDisplay() : (EGLDisplay)display,
        (EGLSurface) surfaceRead,
        (EGLSurface) surfaceWrite,
        (EGLContext) context
    );
}

jni_linux_egl_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong display, jlong context) {
    eglDestroyContext(
        (EGLDisplay) display,
        (EGLContext) context
    );
}

jni_linux_egl_context(void, nBindDebugCallback)(JNIEnv* env, jobject, jclass callbackClass) {
    bindDefaultDebugFunction(env, callbackClass, eglGetCurrentContext());
}