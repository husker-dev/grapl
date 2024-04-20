#import <grapl.h>
#import <thread-utils.h>

#import <Cocoa/Cocoa.h>

#define jni_macos_window(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_MacWindowPeer_##fun
#define jni_macos_utils(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_util_MacOSUtils_##fun