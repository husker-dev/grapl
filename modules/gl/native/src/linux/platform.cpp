#include "grapl-gl-linux.h"

jni_linux_platform(jlongArray, nCreateWindow)(JNIEnv* env, jobject, jlong _display) {
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

    GLXContext context = glXCreateContext(display, visual, NULL, GL_TRUE);
    return createLongArray(env, { (jlong)window, (jlong)context });
}

jni_linux_platform(void, nSwapBuffers)(JNIEnv* env, jobject, jlong _display, jlong _window) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    glXSwapBuffers(display, window);
}

jni_linux_platform(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong _display, jlong _window, jlong _context, jint swapInterval) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    glXSwapIntervalEXTPtr glXSwapIntervalEXT;
    glXSwapIntervalMESAPtr glXSwapIntervalMESA;
    glXSwapIntervalMESAPtr glXSwapIntervalSGI;

    if (glXSwapIntervalEXT = (glXSwapIntervalEXTPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalEXT"))
        glXSwapIntervalEXT(display, window, swapInterval);
    else if(glXSwapIntervalMESA = (glXSwapIntervalMESAPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalMESA"))
        glXSwapIntervalMESA(swapInterval);
    else if(glXSwapIntervalSGI = (glXSwapIntervalMESAPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalSGI"))
        glXSwapIntervalSGI(swapInterval);
}