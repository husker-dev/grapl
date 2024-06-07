#include <grapl.h>

#define jni_linux_platform(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_platform_impl_LinuxPlatform_##fun
#define jni_linux_window(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_LinuxWindowPeer_##fun
#define jni_linux_display(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_display_impl_LinuxDisplayPeer_##fun
