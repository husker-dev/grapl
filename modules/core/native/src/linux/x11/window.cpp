#include "grapl-x11.h"


struct MwmHints {
    unsigned long flags;
    unsigned long functions;
    unsigned long decorations;
    long input_mode;
    unsigned long status;
};
enum {
    MWM_HINTS_FUNCTIONS = (1L << 0),
    MWM_HINTS_DECORATIONS =  (1L << 1),

    MWM_FUNC_ALL = (1L << 0),
    MWM_FUNC_RESIZE = (1L << 1),
    MWM_FUNC_MOVE = (1L << 2),
    MWM_FUNC_MINIMIZE = (1L << 3),
    MWM_FUNC_MAXIMIZE = (1L << 4),
    MWM_FUNC_CLOSE = (1L << 5)
};


jni_x11_window(jlong, nCreateWindow)(JNIEnv* env, jobject, jlong _display) {
    Display* display = (Display*)_display;

   	int screen = DefaultScreen(display);
   	Window window = XCreateSimpleWindow(
   	    display,
   	    DefaultRootWindow(display),
   	    0, 0,
		200, 300,
		5,
		WhitePixel(display, screen), BlackPixel(display, screen)
    );
    XSelectInput(display, window, ExposureMask|ButtonPressMask|KeyPressMask);

	return (jlong)window;
}

jni_x11_window(void, nDestroyWindow)(JNIEnv* env, jobject, jlong _display, jlong _window) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;
    XDestroyWindow(display, window);
}

jni_x11_window(void, nSetTitle)(JNIEnv* env, jobject, jlong _display, jlong _window, jobject _title) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    char* title = (char*)env->GetDirectBufferAddress(_title);
    XStoreName(display, window, title);
}

jni_x11_window(void, nSetVisible)(JNIEnv* env, jobject, jlong _display, jlong _window, jboolean isVisible) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    if(isVisible) XMapRaised(display, window);
    else          XUnmapWindow(display, window);
    XFlush(display);
}

static void setCursor(Display* display, Window window, const char* name){
    char* theme = XcursorGetTheme(display);
    const int size = XcursorGetDefaultSize(display);
    if(!theme) return;

    XcursorImage* image = XcursorLibraryLoadImage(name, theme, size);
    if(image){
        XDefineCursor(display, window, XcursorImageLoadCursor(display, image));
        XcursorImageDestroy(image);
        XFlush(display);
    }else {
        XcursorImage* image = XcursorLibraryLoadImage("default", theme, size);
        if(image){
            XDefineCursor(display, window, XcursorImageLoadCursor(display, image));
            XcursorImageDestroy(image);
            XFlush(display);
        }
    }
}

jni_x11_window(void, nSetCursor)(JNIEnv* env, jobject, jlong _display, jlong _window, jint cursor) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    switch(cursor){
        case 1: setCursor(display, window, "pointer"); return;
        case 2: setCursor(display, window, "xterm"); return;
        case 3: setCursor(display, window, "circle"); return;
        case 5: setCursor(display, window, "cross"); return;
        case 6: setCursor(display, window, "crossed_circle"); return;
        case 7: setCursor(display, window, "question_arrow"); return;
        case 8: setCursor(display, window, "sb_h_double_arrow"); return;
        case 9: setCursor(display, window, "sb_v_double_arrow"); return;
        case 10: setCursor(display, window, "w-resize"); return;
        case 11: setCursor(display, window, "e-resize"); return;
        case 12: setCursor(display, window, "n-resize"); return;
        case 13: setCursor(display, window, "s-resize"); return;
        case 14: setCursor(display, window, "ne-resize"); return;
        case 15: setCursor(display, window, "se-resize"); return;
        case 16: setCursor(display, window, "move"); return;
        case 17: setCursor(display, window, "all-scroll"); return;
        case 18: setCursor(display, window, "sb_up_arrow"); return;
        case 19: setCursor(display, window, "sb_down_arrow"); return;
        case 20: setCursor(display, window, "sb_left_arrow"); return;
        case 21: setCursor(display, window, "sb_right_arrow"); return;
        case 22: setCursor(display, window, "ul_angle"); return;
        case 23: setCursor(display, window, "ur_angle"); return;
        case 24: setCursor(display, window, "ll_angle"); return;
        case 25: setCursor(display, window, "lr_angle"); return;
        default: setCursor(display, window, "default"); return;
    };
}


jni_x11_window(void, nSetPosition)(JNIEnv* env, jobject, jlong _display, jlong _window, jint x, jint y) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    XMoveWindow(display, window, x, y);
    XFlush(display);
}

jni_x11_window(void, nSetSize)(JNIEnv* env, jobject, jlong _display, jlong _window, jint width, jint height) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    XResizeWindow(display, window, width, height);
    XFlush(display);
}

jni_x11_window(void, nUpdateMinMax)(JNIEnv* env, jobject, jlong _display, jlong _window,
    jint minWidth, jint minHeight,
    jint maxWidth, jint maxHeight
) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    XSizeHints* sizeHints = XAllocSizeHints();
    sizeHints->flags = PMinSize | PMaxSize;
    sizeHints->min_width = minWidth;
    sizeHints->min_height = minHeight;
    sizeHints->max_width = maxWidth;
    sizeHints->max_height = maxHeight;
    XSetWMNormalHints(display, window, sizeHints);
    XFree(sizeHints);
    XFlush(display);
}

jni_x11_window(void, nUpdateActions)(JNIEnv* env, jobject, jlong _display, jlong _window, jboolean minimizable, jboolean maximizable) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    struct MwmHints hints;
    hints.flags = MWM_HINTS_FUNCTIONS;
    hints.functions = MWM_FUNC_RESIZE | MWM_FUNC_CLOSE | MWM_FUNC_MOVE;
    if(minimizable) hints.functions |= MWM_FUNC_MINIMIZE;
    if(maximizable) hints.functions |= MWM_FUNC_MAXIMIZE;

    XChangeProperty(display, window, XInternAtom(display, "_MOTIF_WM_HINTS", False), XA_ATOM, 32, PropModeReplace, (unsigned char*)&hints, 5);
    XFlush(display);
}