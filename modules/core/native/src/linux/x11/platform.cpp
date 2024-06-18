#include "grapl-x11.h"

#include <poll.h>

static void peekMessage(Display* display){
    XEvent event;
    while(XPending(display))
        XNextEvent(display, &event);
}

jni_x11_platform(jlong, nXOpenDisplay)(JNIEnv* env, jobject) {
    XInitThreads();
    return (jlong)XOpenDisplay(NULL);
}

jni_x11_platform(void, nPeekMessage)(JNIEnv* env, jobject, jlong _display) {
    Display* display = (Display*)_display;
    peekMessage(display);
}

jni_x11_platform(void, nWaitMessage)(JNIEnv* env, jobject, jlong _display, jint timeout) {
    Display* display = (Display*)_display;
    if(timeout != -1){
        struct pollfd pfd = {
            .fd = ConnectionNumber(display),
            .events = POLLIN,
        };
        if (XPending(display) > 0 || poll(&pfd, 1, timeout) > 0)
            peekMessage(display);
    } else {
        XEvent event;
        XNextEvent(display, &event);
    }
}

jni_x11_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject, jlong _display, jlong _window) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    XClientMessageEvent dummyEvent = {};
    dummyEvent.type = ClientMessage;
    dummyEvent.window = window;
    dummyEvent.format = 32;
    XSendEvent(display, window, 0, 0, (XEvent*)&dummyEvent);
    XFlush(display);
}