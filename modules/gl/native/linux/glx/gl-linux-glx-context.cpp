#include "gl-linux-glx.h"

glXCreateContextAttribsARBPtr glXCreateContextAttribsARB = NULL;
glXSwapIntervalEXTPtr         glXSwapIntervalEXT = NULL;
glXSwapIntervalMESAPtr        glXSwapIntervalMESA = NULL;
glXSwapIntervalMESAPtr        glXSwapIntervalSGI = NULL;


static void getContextDetailsGLX(GLDetails* details, Display* display, GLXDrawable drawable, GLXContext context){
    Display* oldDisplay = glXGetCurrentDisplay();
    GLXDrawable oldDrawable = glXGetCurrentDrawable();
    GLXContext oldContext = glXGetCurrentContext();

    glXMakeCurrent(display, drawable, context);
    getContextDetails(details);
    glXMakeCurrent(oldDisplay, oldDrawable, oldContext);
}


jni_linux_glx_context(void, nInitFunctions)(JNIEnv* env, jobject) {
    glXCreateContextAttribsARB = (glXCreateContextAttribsARBPtr)glXGetProcAddressARB((GLubyte*) "glXCreateContextAttribsARB");
    glXSwapIntervalEXT = (glXSwapIntervalEXTPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalEXT");
    glXSwapIntervalMESA = (glXSwapIntervalMESAPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalMESA");
    glXSwapIntervalSGI = (glXSwapIntervalMESAPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalSGI");

    glDebugMessageCallbackARB = (glDebugMessageCallbackARBPtr)glXGetProcAddressARB((GLubyte*) "glDebugMessageCallbackARB");
    glGetIntegerv = (glGetIntegervPtr)glXGetProcAddressARB((GLubyte*) "glGetIntegerv");
    glGetStringi = (glGetStringiPtr)glXGetProcAddressARB((GLubyte*) "glGetStringi");
}

jni_linux_glx_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion, jboolean debug) {
    Display* display = XOpenDisplay(nullptr);

    int num_fbc = 0;
    static int visual_attribs[] = { None };
    GLXFBConfig* fbc = glXChooseFBConfig(display, DefaultScreen(display), visual_attribs, &num_fbc);

    // Creating context
    static int context_attribs[] = {
            GLX_CONTEXT_PROFILE_MASK_ARB, isCore ? GLX_CONTEXT_CORE_PROFILE_BIT_ARB : GLX_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            GLX_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            GLX_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            GLX_CONTEXT_FLAGS_ARB, debug ? GLX_CONTEXT_DEBUG_BIT_ARB : 0,
            None
    };
    GLXContext context = glXCreateContextAttribsARB(display, fbc[0], (GLXContext)shareWith, true, context_attribs);

    // Creating PBuffer
    int pbufferAttribs[] = {
            GLX_PBUFFER_WIDTH,  32,
            GLX_PBUFFER_HEIGHT, 32,
            None
    };
    GLXPbuffer pbuffer = glXCreatePbuffer(display, fbc[0], pbufferAttribs);

    GLDetails details = {};
    getContextDetailsGLX(&details, display, pbuffer, context);

    return createLongArray(env, {
        (jlong) display,
        (jlong) pbuffer,
        (jlong) context,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_linux_glx_context(jlongArray, nCreateContextForWindow)(JNIEnv* env, jobject,
    jlong _display,
    jlong _window,
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
    Display* display = (Display*)_display;
    Window window = (Window)_window;
    int screen = DefaultScreen(display);

    GLint glxAttribs[] = {
        GLX_RGBA,
        doubleBuffering ? GLX_DOUBLEBUFFER : GLX_USE_GL,

        GLX_RED_SIZE,       redBits,
        GLX_GREEN_SIZE,     greenBits,
        GLX_BLUE_SIZE,      blueBits,
        GLX_DEPTH_SIZE,     depthBits,
        GLX_STENCIL_SIZE,   stencilBits,

        GLX_SAMPLE_BUFFERS, msaa > 0,
        GLX_SAMPLES,        msaa,
        None
    };
    XVisualInfo* visual = glXChooseVisual(display, screen, glxAttribs);

    XSetWindowAttributes windowAttribs;
    windowAttribs.colormap = XCreateColormap(display, RootWindow(display, screen), visual->visual, AllocNone);
    XChangeWindowAttributes(display, window, CWColormap, &windowAttribs);


    // Create context
    int num_fbc = 0;
    static int visual_attribs[] = { None };
    GLXFBConfig* fbc = glXChooseFBConfig(display, screen, visual_attribs, &num_fbc);

    static int context_attribs[] = {
            GLX_CONTEXT_PROFILE_MASK_ARB, isCore ? GLX_CONTEXT_CORE_PROFILE_BIT_ARB : GLX_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            GLX_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            GLX_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            GLX_CONTEXT_FLAGS_ARB, debug ? GLX_CONTEXT_DEBUG_BIT_ARB : 0,
            None
    };
    GLXContext context = glXCreateContextAttribsARB(display, fbc[0], (GLXContext)shareWith, true, context_attribs);

    GLDetails details = {};
    getContextDetailsGLX(&details, display, window, context);

    return createLongArray(env, {
        (jlong) display,
        (jlong) window,
        (jlong) context,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_linux_glx_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    GLDetails details = {};
    getContextDetails(&details);

    return createLongArray(env, {
        (jlong) glXGetCurrentDisplay(),
        (jlong) glXGetCurrentDrawable(),
        (jlong) glXGetCurrentContext(),
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_linux_glx_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong display, jlong pbuffer, jlong context) {
    return glXMakeCurrent(
        (Display*)display,
        (GLXPbuffer)pbuffer,
        (GLXContext)context
    );
}

jni_linux_glx_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong display, jlong context) {
    glXDestroyContext(
        (Display*)display,
        (GLXContext)context
    );
}

jni_linux_glx_context(void, nBindDebugCallback)(JNIEnv* env, jobject, jclass callbackClass) {
    bindDefaultDebugFunction(env, callbackClass, glXGetCurrentContext());
}