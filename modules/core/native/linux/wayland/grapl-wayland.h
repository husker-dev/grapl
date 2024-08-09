#include <grapl.h>

#include <wayland-client.h>

#define jni_wayland_platform(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_platform_impl_Wayland_##fun
#define jni_wayland_window(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_LinuxWindowPeer_##fun
#define jni_wayland_display(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_display_impl_LinuxDisplayPeer_##fun
