#include "grapl-x11.h"


struct MwmHints {
    unsigned long flags;
    unsigned long functions;
    unsigned long decorations;
    long input_mode;
    unsigned long status;
};

#define MWM_HINTS_FUNCTIONS     (1L << 0)
#define MWM_HINTS_DECORATIONS   (1L << 1)

#define MWM_FUNC_RESIZE         (1L << 1)
#define MWM_FUNC_MOVE           (1L << 2)
#define MWM_FUNC_MINIMIZE       (1L << 3)
#define MWM_FUNC_MAXIMIZE       (1L << 4)
#define MWM_FUNC_CLOSE          (1L << 5)

#define MWM_DECOR_ALL           (1L << 0)


class X11WindowCallbackContainer: public WindowCallbackContainer {
public:
    Window window;
    XIC xic;
    bool disabled;

    X11WindowCallbackContainer(JNIEnv* env, Window window, XIC xic, jobject callbackObject):
                                                        WindowCallbackContainer(env, callbackObject) {
        this->window = window;
        this->xic = xic;
        this->disabled = false;
    }
};
static std::map<Window, X11WindowCallbackContainer*> wrappers;

int translateModifiers(int state){
    int mods = 0;
    if (state & Mod1Mask)    mods |= 1;
    if (state & ControlMask) mods |= 2;
    if (state & ShiftMask)   mods |= 4;
    return mods;
}

int translateKeyCode(int code){
    if (!keyMap.count(code))
        return GRAPL_VK_UNKNOWN;
    return keyMap[code];
}

void dispatchEvent(XEvent event){
    Display* display = event.xany.display;
    Window window = event.xany.window;

    if (!wrappers.count(window))
        return;
    X11WindowCallbackContainer* wrapper = wrappers[window];
    JNIEnv* env = wrapper->env;

    int keycode = 0;
    if (event.type == KeyPress || event.type == KeyRelease)
        keycode = event.xkey.keycode;

    Bool filtered = XFilterEvent(&event, None);

    switch(event.type){
        case ClientMessage: {
            if (filtered || event.xclient.message_type == None)
                return;

            if (event.xclient.message_type == WM_PROTOCOLS){
                const Atom protocol = event.xclient.data.l[0];
                if (protocol == None)
                    return;

                if (protocol == WM_DELETE_WINDOW){
                    wrapper->onCloseCallback->call();
                    return;
                } else if (protocol == NET_WM_PING) {
                    XEvent reply = event;
                    reply.xclient.window = DefaultRootWindow(display);
                    XSendEvent(display, reply.xclient.window, False,
                               SubstructureNotifyMask | SubstructureRedirectMask,
                               &reply);
                }
            }
            break;
        }
        case ConfigureNotify: {
            XConfigureEvent e = event.xconfigure;
            wrapper->onResizeCallback->call(e.width, e.height);
            wrapper->onMoveCallback->call(e.x, e.y);
            break;
        }
        case FocusIn: {
            wrapper->onFocusCallback->call(true);
            break;
        }
        case FocusOut: {
            wrapper->onFocusCallback->call(false);
            break;
        }
        case MotionNotify: {
            const int x = event.xmotion.x;
            const int y = event.xmotion.y;
            const int mods = translateModifiers(event.xmotion.state);
            wrapper->onPointerMoveCallback->call(1, x, y, mods);
            break;
        }
        case ButtonPress: {
            const int x = event.xbutton.x;
            const int y = event.xbutton.y;
            const int mods = translateModifiers(event.xbutton.state);

            if (event.xbutton.button <= 3)
                wrapper->onPointerDownCallback->call(1, x, y, event.xbutton.button, mods);
            else if (event.xbutton.button > 7)
                wrapper->onPointerDownCallback->call(1, x, y, event.xbutton.button - 4, mods);
            else if (event.xbutton.button == 4)
                wrapper->onPointerScrollCallback->call(1, x, y, 0.0, 10.0, mods);
            else if (event.xbutton.button == 5)
                wrapper->onPointerScrollCallback->call(1, x, y, 0.0, -10.0, mods);
            else if (event.xbutton.button == 6)
                wrapper->onPointerScrollCallback->call(1, x, y, 10.0, 0.0, mods);
            else if (event.xbutton.button == 7)
                wrapper->onPointerScrollCallback->call(1, x, y, -10.0, 0.0, mods);
            break;
        }
        case ButtonRelease: {
            const int x = event.xbutton.x;
            const int y = event.xbutton.y;
            const int mods = translateModifiers(event.xbutton.state);

            if (event.xbutton.button <= 3)
                wrapper->onPointerUpCallback->call(1, x, y, event.xbutton.button, mods);
            if (event.xbutton.button > 7)
                wrapper->onPointerUpCallback->call(1, x, y, event.xbutton.button - 4, mods);
            break;
        }
        case EnterNotify: {
            const int x = event.xcrossing.x;
            const int y = event.xcrossing.y;
            const int mods = translateModifiers(event.xbutton.state);
            wrapper->onPointerEnterCallback->call(1, x, y, mods);
            break;
        }
        case LeaveNotify: {
            const int x = event.xcrossing.x;
            const int y = event.xcrossing.y;
            const int mods = translateModifiers(event.xbutton.state);
            wrapper->onPointerLeaveCallback->call(1, x, y, mods);
            break;
        }
        case KeyPress: {
            const int key = translateKeyCode(keycode);
            const int mods = translateModifiers(event.xkey.state);

            Status status;
            char buffer[200];
            Xutf8LookupString(wrapper->xic, &event.xkey, buffer, sizeof(buffer) - 1, NULL, &status);

            wrapper->onKeyDownCallback->call(key, keycode, toJString(env, buffer), mods);
            break;
        }
        case KeyRelease: {
            const int key = translateKeyCode(keycode);
            const int mods = translateModifiers(event.xkey.state);
            wrapper->onKeyUpCallback->call(key, keycode, toJString(env, (char*)""), mods);
            break;
        }
    }
}


jni_x11_window(jlong, nCreateWindow)(JNIEnv* env, jobject, jlong _display, jobject callbackObject) {
    Display* display = (Display*)_display;

   	int screen = DefaultScreen(display);

   	XSetWindowAttributes windowAttribs;
    windowAttribs.border_pixel = BlackPixel(display, screen);
    windowAttribs.background_pixel = WhitePixel(display, screen);
    windowAttribs.event_mask = ExposureMask | ButtonPressMask | KeyPressMask;

   	Window window = XCreateWindow(display, DefaultRootWindow(display),
                  0, 0, 10, 10, 0,
                  CopyFromParent, InputOutput, CopyFromParent,
                  CWEventMask | CWBackPixel | CWBorderPixel,
                  &windowAttribs);

    // Set events
    XSelectInput(display, window, StructureNotifyMask | KeyPressMask    | KeyReleaseMask       |
                                  PointerMotionMask   | ButtonPressMask | ButtonReleaseMask    |
                                  ExposureMask        | FocusChangeMask | VisibilityChangeMask |
                                  EnterWindowMask     | LeaveWindowMask | PropertyChangeMask   );

    // Set protocols to handle 'close' and 'ping' events
    Atom protocols[] = {
        WM_DELETE_WINDOW,
        NET_WM_PING
    };
    XSetWMProtocols(display, window, protocols, sizeof(protocols) / sizeof(Atom));

    // Setup XIC
    XSetLocaleModifiers("");
    XIM xim = XOpenIM(display, 0, 0, 0);
    if(!xim){
        XSetLocaleModifiers("@im=none");
        xim = XOpenIM(display, 0, 0, 0);
    }

    XIC xic = XCreateIC(xim,
                        XNInputStyle,   XIMPreeditNothing | XIMStatusNothing,
                        XNClientWindow, window,
                        XNFocusWindow,  window,
                        NULL);
    XSetICFocus(xic);

    // Create callback container
    wrappers[window] = new X11WindowCallbackContainer(env, window, xic, callbackObject);

	return (jlong)window;
}

jni_x11_window(void, nDestroyWindow)(JNIEnv* env, jobject, jlong _display, jlong _window) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    X11WindowCallbackContainer* wrapper = wrappers[window];
    wrappers.erase(window);
    delete wrapper;

    XDestroyWindow(display, window);
}

jni_x11_window(void, nSetTitle)(JNIEnv* env, jobject, jlong _display, jlong _window, jobject _title) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    char* title = (char*)env->GetDirectBufferAddress(_title);
    XChangeProperty(display, window,
            _NET_WM_NAME, UTF8_STRING,
            8, PropModeReplace, (unsigned char*) title,
            env->GetDirectBufferCapacity(_title)-2);
}

jni_x11_window(void, nSetVisible)(JNIEnv* env, jobject, jlong _display, jlong _window, jboolean isVisible, jint x, jint y, jint width, jint height) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    if(isVisible) XMapRaised(display, window);
    else          XUnmapWindow(display, window);

    XMoveWindow(display, window, x, y);
    XResizeWindow(display, window, width, height);
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

jni_x11_window(void, nUpdateHints)(JNIEnv* env, jobject, jlong _display, jlong _window,
    jboolean minimizable,
    jboolean maximizable,
    jboolean resizable,
    jboolean closable,
    jboolean decorations
) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    struct MwmHints hints;
    hints.flags = MWM_HINTS_FUNCTIONS | MWM_HINTS_DECORATIONS;
    hints.functions = MWM_FUNC_MOVE;
    if(decorations) hints.decorations |= MWM_DECOR_ALL;
    if(minimizable) hints.functions |= MWM_FUNC_MINIMIZE;
    if(maximizable) hints.functions |= MWM_FUNC_MAXIMIZE;
    if(resizable)   hints.functions |= MWM_FUNC_RESIZE;
    if(closable)    hints.functions |= MWM_FUNC_CLOSE;

    XChangeProperty(display, window, _MOTIF_WM_HINTS, XA_ATOM, 32, PropModeReplace, (unsigned char*)&hints, 5);
    XFlush(display);
}

jni_x11_window(void, nSetEnabled)(JNIEnv* env, jobject, jlong _display, jlong _window, jboolean enabled) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    X11WindowCallbackContainer* wrapper = wrappers[window];
    wrapper->disabled = !enabled;

    XFlush(display);
}