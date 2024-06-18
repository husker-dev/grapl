#include "grapl-x11.h"



static void peekMessage(){

}

jni_x11_platform(jlong, nXOpenDisplay)(JNIEnv* env, jobject) {
    XInitThreads();
    return (jlong)XOpenDisplay(NULL);
}

jni_x11_platform(void, nInit)(JNIEnv* env, jobject) {

}

jni_x11_platform(void, nPeekMessage)(JNIEnv* env, jobject, jlong _display) {
    Display* display = (Display*)_display;
    XEvent event;
    while(XPending(display))
        XNextEvent(display, &event);
}

jni_x11_platform(void, nWaitMessage)(JNIEnv* env, jobject, jint timeout) {

}

jni_x11_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject) {

}