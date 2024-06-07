#include "grapl-linux.h"



static void peekMessage(){
    for (;;){
        char dummy[64];
        const ssize_t result = read(_glfw.x11.emptyEventPipe[0], dummy, sizeof(dummy));
        if (result == -1 && errno != EINTR)
            break;
    }

    Display* display = glXGetCurrentDisplay();
    XPending(display);

    while (QLength(display)){
        XEvent event;
        XNextEvent(display, &event);
        processEvent(&event);
    }
    XFlush(display);
}

jni_linux_platform(void, nInit)(JNIEnv* env, jobject) {

}

jni_linux_platform(void, nPeekMessage)(JNIEnv* env, jobject) {
    peekMessage();
}

jni_linux_platform(void, nWaitMessage)(JNIEnv* env, jobject, jint timeout) {
    Display* display = glXGetCurrentDisplay();

    enum { XLIB_FD, PIPE_FD, INOTIFY_FD };
    struct pollfd fds[] = {
        [XLIB_FD] = { ConnectionNumber(display), POLLIN },
        [PIPE_FD] = { _glfw.x11.emptyEventPipe[0], POLLIN },
        [INOTIFY_FD] = { -1, POLLIN }
    };

    while (!XPending(_glfw.x11.display)){
        if (!_glfwPollPOSIX(fds, sizeof(fds) / sizeof(fds[0]), timeout))
            return GLFW_FALSE;

        for (int i = 1; i < sizeof(fds) / sizeof(fds[0]); i++){
            if (fds[i].revents & POLLIN)
                return GLFW_TRUE;
        }
    }

    peekMessage();
}

jni_linux_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject) {
     PostMessageW(NULL, WM_NULL, 0, 0);
}