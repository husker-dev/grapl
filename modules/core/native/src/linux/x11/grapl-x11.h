#ifndef GRAPL_X11_H
#define GRAPL_X11_H

#include <grapl.h>
#include <callbacks.hpp>

#include <map>

#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xresource.h>
#include <X11/extensions/Xrandr.h>
#include <X11/cursorfont.h>
#include <X11/Xcursor/Xcursor.h>
#include <X11/Xatom.h>
#include <X11/XKBlib.h>

#include <fcntl.h>


extern Atom WM_PROTOCOLS,
            _NET_WM_NAME,
            UTF8_STRING,
            NET_WM_PING,
            WM_DELETE_WINDOW,
            _MOTIF_WM_HINTS;
extern std::map<int, int> keyMap;

void dispatchEvent(XEvent event);
void initKeyMap(Display* display);

#define jni_x11_platform(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_platform_impl_X11_##fun
#define jni_x11_window(returnType, fun)   extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_window_impl_X11WindowPeer_##fun
#define jni_x11_display(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_core_display_impl_X11DisplayPeer_##fun

static jstring toJString(JNIEnv* env, char utf8[]){
    return env->NewStringUTF(utf8);
}

#endif