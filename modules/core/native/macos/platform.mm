#import "grapl-macos.h"

jni_macos_platform(void, nInvokeOnMainThread)(JNIEnv* env, jobject, jobject runnable, jboolean wait) {
    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jobject runnableGlobal = env->NewGlobalRef(runnable);
    jclass runnableClass = env->GetObjectClass(runnableGlobal);
    jmethodID runMethod = env->GetMethodID(runnableClass, "run", "()V");

    [ThreadUtilities_Core performOnMainThread:(wait ? YES : NO) block:^() {
        JNIEnv* env;
        jvm->AttachCurrentThread((void**)&env, NULL);
        env->CallVoidMethod(runnableGlobal, runMethod);
        env->DeleteGlobalRef(runnableGlobal);
        jvm->DetachCurrentThread();
    }];
}

jni_macos_platform(void, nPeekMessage)(JNIEnv* env, jobject) {
    ON_MAIN_THREAD(
        for (;;) {
            NSEvent* event = [NSApp nextEventMatchingMask:NSEventMaskAny
                                                untilDate:[NSDate distantPast]
                                                   inMode:NSDefaultRunLoopMode
                                                  dequeue:YES];
            if (event == nil)
                break;
            [NSApp sendEvent:event];
        }
    );
}

jni_macos_platform(void, nWaitMessage)(JNIEnv* env, jobject, jint timeout) {
    ON_MAIN_THREAD(
        NSDate* date = timeout != -1 ?
                [NSDate dateWithTimeIntervalSinceNow:timeout] :
                [NSDate distantFuture];
        NSEvent *event = [NSApp nextEventMatchingMask:NSEventMaskAny
                                            untilDate:date
                                               inMode:NSDefaultRunLoopMode
                                              dequeue:YES];
        [NSApp sendEvent:event];
        for (;;) {
            event = [NSApp nextEventMatchingMask:NSEventMaskAny
                                       untilDate:[NSDate distantPast]
                                          inMode:NSDefaultRunLoopMode
                                         dequeue:YES];
            if (event == nil)
                break;
            [NSApp sendEvent:event];
        }
    );
}

jni_macos_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject) {
    ON_MAIN_THREAD(
        NSEvent* event = [NSEvent otherEventWithType:NSEventTypeApplicationDefined
                                            location:NSMakePoint(0, 0)
                                       modifierFlags:0
                                           timestamp:0
                                        windowNumber:0
                                             context:nil
                                             subtype:0
                                               data1:0
                                               data2:0];
        [NSApp postEvent:event atStart:YES];
    );
}