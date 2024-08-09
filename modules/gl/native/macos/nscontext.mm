#import "grapl-gl-macos.h"

jni_macos_nscontext(jlong, nAttachToWindow)(JNIEnv* env, jobject, jlong _windowPtr, jlong _ctx) {
    __block NSOpenGLContext* nsgl;

    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        CGLContextObj ctx = (CGLContextObj)_ctx;
        NSView* view = [window contentView];

        nsgl = [[NSOpenGLContext alloc] initWithCGLContextObj:ctx];
        [nsgl setView:view];
    );
    return (jlong)nsgl;
}

jni_macos_nscontext(void, nFlushBuffer)(JNIEnv* env, jobject, jlong _nsgl) {
    NSOpenGLContext* nsgl = (NSOpenGLContext*)_nsgl;
    [nsgl flushBuffer];
}

jni_macos_nscontext(void, nReleaseContext)(JNIEnv* env, jobject, jlong context) {
    CGLReleaseContext((CGLContextObj)context);
}

jni_macos_nscontext(void, nSetSwapInterval)(JNIEnv* env, jobject, jlong _nsgl, jint swapInterval) {
    NSOpenGLContext* nsgl = (NSOpenGLContext*)_nsgl;
    [nsgl setValues:&swapInterval forParameter:NSOpenGLCPSwapInterval];
}
