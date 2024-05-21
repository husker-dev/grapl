#define UNICODE

#include <grapl.h>
#include <windows.h>
#include <windowsx.h>

#define jni_win_platform(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_platform_impl_WinPlatform_##fun
#define jni_win_window(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_WinWindowPeer_##fun
#define jni_win_display(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_display_impl_WinDisplayPeer_##fun
