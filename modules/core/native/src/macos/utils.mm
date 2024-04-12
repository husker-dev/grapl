#include "macos-shared.h"
#import <Cocoa/Cocoa.h>

/*
    Based on JDK's AWT implementation
    https://github.com/openjdk/jdk/blob/master/src/java.desktop/macosx/native/libosxapp/ThreadUtilities.m
*/

@interface ThreadUtilities : NSObject { }
+ (void)performOnMainThread:(BOOL)wait block:(void (^)())block;
@end

@implementation ThreadUtilities
static NSArray<NSString*> *javaModes = [[NSArray alloc] initWithObjects:
        NSDefaultRunLoopMode, NSModalPanelRunLoopMode, NSEventTrackingRunLoopMode, @"grapl", nil];

+ (void)invokeBlock:(void (^)())block {
    block();
}

+ (void)invokeBlockCopy:(void (^)(void))blockCopy {
    blockCopy();
    Block_release(blockCopy);
}

+ (void)performOnMainThread:(BOOL)wait block:(void (^)())block {
    if (![NSThread isMainThread]){
        [self
            performSelectorOnMainThread:    wait == YES ? @selector(invokeBlock:) : @selector(invokeBlockCopy:)
            withObject:                     wait == YES ? block : Block_copy(block)
            waitUntilDone:                  wait
            modes:                          javaModes
        ];
    } else
        block();
}
@end



jni_macos_utils(void, nInvokeOnMainThread)(JNIEnv* env, jobject, jobject runnable, jboolean wait) {
    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jobject runnableGlobal = env->NewGlobalRef(runnable);
    jclass runnableClass = env->GetObjectClass(runnableGlobal);
    jmethodID runMethod = env->GetMethodID(runnableClass, "run", "()V");

    [ThreadUtilities performOnMainThread:(wait ? YES : NO) block:^() {
        JNIEnv* env;
        jvm->AttachCurrentThread((void**)&env, NULL);
        env->CallVoidMethod(runnableGlobal, runMethod);
        env->DeleteGlobalRef(runnableGlobal);
        jvm->DetachCurrentThread();
    }];
}