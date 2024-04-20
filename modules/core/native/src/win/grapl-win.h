#include <grapl.h>
#include <windows.h>

#define jni_win_window(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_WinWindowPeer_##fun
