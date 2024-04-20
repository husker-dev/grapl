#import "grapl-gl-macos.h"
#import <thread-utils.h>
#import <Cocoa/Cocoa.h>

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
    ON_MAIN_THREAD(
        NSOpenGLContext* nsgl = (NSOpenGLContext*)_nsgl;
        [nsgl flushBuffer];
    );
}

jni_macos_nscontext(jboolean, nMakeCurrentContext)(JNIEnv* env, jobject, jlong _nsgl) {
    ON_MAIN_THREAD(
        NSOpenGLContext* nsgl = (NSOpenGLContext*)_nsgl;
        [nsgl makeCurrentContext];
    );
    return true;
}

jni_macos_nscontext(void, nReleaseContext)(JNIEnv* env, jobject, jlong context) {
    CGLReleaseContext((CGLContextObj)context);
}
