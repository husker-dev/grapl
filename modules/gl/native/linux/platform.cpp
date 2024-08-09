#include "grapl-gl-linux.h"

jni_linux_platform(jlongArray, nCreateWindow)(JNIEnv* env, jobject, jlong _display, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion, jboolean debug) {
    checkBasicFunctions();

    Display* display = (Display*)_display;
    int screen = DefaultScreen(display);

    GLint glxAttribs[] = {
        GLX_RGBA,
        GLX_DOUBLEBUFFER,
        GLX_DEPTH_SIZE,     24,
        GLX_STENCIL_SIZE,   8,
        GLX_RED_SIZE,       8,
        GLX_GREEN_SIZE,     8,
        GLX_BLUE_SIZE,      8,
        GLX_SAMPLE_BUFFERS, 0,
        GLX_SAMPLES,        0,
        None
    };
    XVisualInfo* visual = glXChooseVisual(display, screen, glxAttribs);

    XSetWindowAttributes windowAttribs;
    windowAttribs.border_pixel = BlackPixel(display, screen);
    windowAttribs.background_pixel = WhitePixel(display, screen);
    windowAttribs.override_redirect = True;
    windowAttribs.colormap = XCreateColormap(display, RootWindow(display, screen), visual->visual, AllocNone);
    windowAttribs.event_mask = ExposureMask | ButtonPressMask | KeyPressMask;
    Window window = XCreateWindow(display, RootWindow(display, screen),
        0, 0,
        320, 200,
        0, visual->depth, InputOutput, visual->visual,
        CWBackPixel | CWColormap | CWBorderPixel | CWEventMask,
        &windowAttribs);

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

    // Getting info
    Display* oldDisplay = glXGetCurrentDisplay();
    GLXDrawable oldDrawable = glXGetCurrentDrawable();
    GLXContext oldContext = glXGetCurrentContext();

    GLint major, minor, flags;

    glXMakeContextCurrent(display, window, window, context);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    glGetIntegerv(GL_CONTEXT_FLAGS, &flags);
    glXMakeContextCurrent(oldDisplay, oldDrawable, oldDrawable, oldContext);

    return createLongArray(env, { (jlong)window, (jlong)context, (jlong)major, (jlong)minor, (flags & GL_CONTEXT_FLAG_DEBUG_BIT) != 0 });
}

jni_linux_platform(void, nSwapBuffers)(JNIEnv* env, jobject, jlong _display, jlong _window) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    glXSwapBuffers(display, window);
}

jni_linux_platform(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong _display, jlong _window, jlong _context, jint swapInterval) {
    checkBasicFunctions();

    Display* display = (Display*)_display;
    Window window = (Window)_window;

    glXSwapIntervalEXTPtr glXSwapIntervalEXT;
    glXSwapIntervalMESAPtr glXSwapIntervalMESA;
    glXSwapIntervalMESAPtr glXSwapIntervalSGI;

    if (glXSwapIntervalEXT)
        glXSwapIntervalEXT(display, window, swapInterval);
    else if(glXSwapIntervalMESA)
        glXSwapIntervalMESA(swapInterval);
    else if(glXSwapIntervalSGI)
        glXSwapIntervalSGI(swapInterval);
}