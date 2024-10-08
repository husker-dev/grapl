#import <grapl.h>
#import <callbacks.hpp>

#import "utils/thread-utils.h"

#import <Cocoa/Cocoa.h>
#import <Carbon/Carbon.h>

#define jni_macos_window(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_NSWindowPeer_##fun
#define jni_macos_platform(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_platform_impl_MacPlatform_##fun
#define jni_macos_display(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_display_impl_MacDisplayPeer_##fun

static jstring toJString(JNIEnv* env, NSString* string){
    return env->NewStringUTF([string UTF8String]);
}