#include "grapl-x11.h"

#include <poll.h>
#include <cstring>

// ---- Extern from grapl-x11.h -----
Atom WM_PROTOCOLS,
     _NET_WM_NAME,
     UTF8_STRING,
     NET_WM_PING,
     WM_DELETE_WINDOW,
     _MOTIF_WM_HINTS;
std::map<int, int> keyMap;
// ----------------------------------

static void peekMessage(Display* display){
    XEvent event;
    while(XPending(display)){
        XNextEvent(display, &event);
        dispatchEvent(event);
    }
}

jni_x11_platform(jlong, nXOpenDisplay)(JNIEnv* env, jobject) {
    XInitThreads();
    Display* display = XOpenDisplay(NULL);

    initKeyMap(display);

    WM_PROTOCOLS = XInternAtom(display, "WM_PROTOCOLS", False);
    _NET_WM_NAME = XInternAtom(display, "_NET_WM_NAME", False);
    UTF8_STRING = XInternAtom(display, "UTF8_STRING", False);
    NET_WM_PING = XInternAtom(display, "NET_WM_PING", False);
    WM_DELETE_WINDOW = XInternAtom(display, "WM_DELETE_WINDOW", False);
    _MOTIF_WM_HINTS = XInternAtom(display, "_MOTIF_WM_HINTS", False);

    return (jlong)display;
}

jni_x11_platform(void, nPeekMessage)(JNIEnv* env, jobject, jlong _display) {
    Display* display = (Display*)_display;
    peekMessage(display);
}

jni_x11_platform(void, nWaitMessage)(JNIEnv* env, jobject, jlong _display, jint timeout) {
    Display* display = (Display*)_display;
    if(timeout != -1){
        struct pollfd pfd = {
            .fd = ConnectionNumber(display),
            .events = POLLIN,
        };
        if (XPending(display) > 0 || poll(&pfd, 1, timeout) > 0)
            peekMessage(display);
    } else {
        XEvent event;
        XNextEvent(display, &event);
        dispatchEvent(event);
    }
}

jni_x11_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject, jlong _display, jlong _window) {
    Display* display = (Display*)_display;
    Window window = (Window)_window;

    XClientMessageEvent dummyEvent = {};
    dummyEvent.type = ClientMessage;
    dummyEvent.window = window;
    dummyEvent.format = 32;
    XSendEvent(display, window, 0, 0, (XEvent*)&dummyEvent);
    XFlush(display);
}

void initKeyMap(Display* display){
    const struct {
        const char* name;
        int key;
    } keys[] = {
        { "SPCE", GRAPL_VK_SPACE },
        { "AC11", GRAPL_VK_APOSTROPHE },
        { "AB08", GRAPL_VK_COMMA },
        { "AE11", GRAPL_VK_MINUS },
        { "AB09", GRAPL_VK_PERIOD },
        { "AB10", GRAPL_VK_SLASH },

        { "AE01", GRAPL_VK_1 },
        { "AE02", GRAPL_VK_2 },
        { "AE03", GRAPL_VK_3 },
        { "AE04", GRAPL_VK_4 },
        { "AE05", GRAPL_VK_5 },
        { "AE06", GRAPL_VK_6 },
        { "AE07", GRAPL_VK_7 },
        { "AE08", GRAPL_VK_8 },
        { "AE09", GRAPL_VK_9 },
        { "AE10", GRAPL_VK_0 },

        { "AC10", GRAPL_VK_SEMICOLON },
        { "AE12", GRAPL_VK_EQUAL },

        { "AC01", GRAPL_VK_A },
        { "AB05", GRAPL_VK_B },
        { "AB03", GRAPL_VK_C },
        { "AC03", GRAPL_VK_D },
        { "AD03", GRAPL_VK_E },
        { "AC04", GRAPL_VK_F },
        { "AC05", GRAPL_VK_G },
        { "AC06", GRAPL_VK_H },
        { "AD08", GRAPL_VK_I },
        { "AC07", GRAPL_VK_J },
        { "AC08", GRAPL_VK_K },
        { "AC09", GRAPL_VK_L },
        { "AB07", GRAPL_VK_M },
        { "AB06", GRAPL_VK_N },
        { "AD09", GRAPL_VK_O },
        { "AD10", GRAPL_VK_P },
        { "AD01", GRAPL_VK_Q },
        { "AD04", GRAPL_VK_R },
        { "AC02", GRAPL_VK_S },
        { "AD05", GRAPL_VK_T },
        { "AD07", GRAPL_VK_U },
        { "AB04", GRAPL_VK_V },
        { "AD02", GRAPL_VK_W },
        { "AB02", GRAPL_VK_X },
        { "AD06", GRAPL_VK_Y },
        { "AB01", GRAPL_VK_Z },

        { "AD11", GRAPL_VK_LEFT_BRACKET },
        { "BKSL", GRAPL_VK_BACKSLASH },
        { "AD12", GRAPL_VK_RIGHT_BRACKET },
        { "TLDE", GRAPL_VK_GRAVE_ACCENT },
        { "ESC",  GRAPL_VK_ESCAPE },
        { "RTRN", GRAPL_VK_ENTER },
        { "TAB",  GRAPL_VK_TAB },
        { "BKSP", GRAPL_VK_BACKSPACE },
        { "INS",  GRAPL_VK_INSERT },
        { "DELE", GRAPL_VK_DELETE },

        { "RGHT", GRAPL_VK_RIGHT },
        { "LEFT", GRAPL_VK_LEFT },
        { "DOWN", GRAPL_VK_DOWN },
        { "UP",   GRAPL_VK_UP },

        { "PGUP", GRAPL_VK_PAGE_UP },
        { "PGDN", GRAPL_VK_PAGE_DOWN },
        { "HOME", GRAPL_VK_HOME },
        { "END",  GRAPL_VK_END },
        { "CAPS", GRAPL_VK_CAPS_LOCK },
        { "SCLK", GRAPL_VK_SCROLL_LOCK },
        { "NMLK", GRAPL_VK_NUM_LOCK },
        { "PRSC", GRAPL_VK_PRINT_SCREEN },

        { "FK01", GRAPL_VK_F1 },
        { "FK02", GRAPL_VK_F2 },
        { "FK03", GRAPL_VK_F3 },
        { "FK04", GRAPL_VK_F4 },
        { "FK05", GRAPL_VK_F5 },
        { "FK06", GRAPL_VK_F6 },
        { "FK07", GRAPL_VK_F7 },
        { "FK08", GRAPL_VK_F8 },
        { "FK09", GRAPL_VK_F9 },
        { "FK10", GRAPL_VK_F10 },
        { "FK11", GRAPL_VK_F11 },
        { "FK12", GRAPL_VK_F12 },
        { "FK13", GRAPL_VK_F13 },
        { "FK14", GRAPL_VK_F14 },
        { "FK15", GRAPL_VK_F15 },
        { "FK16", GRAPL_VK_F16 },
        { "FK17", GRAPL_VK_F17 },
        { "FK18", GRAPL_VK_F18 },
        { "FK19", GRAPL_VK_F19 },
        { "FK20", GRAPL_VK_F20 },
        { "FK21", GRAPL_VK_F21 },
        { "FK22", GRAPL_VK_F22 },
        { "FK23", GRAPL_VK_F23 },
        { "FK24", GRAPL_VK_F24 },
        { "FK25", GRAPL_VK_F25 },

        { "KP0", GRAPL_VK_KP_0 },
        { "KP1", GRAPL_VK_KP_1 },
        { "KP2", GRAPL_VK_KP_2 },
        { "KP3", GRAPL_VK_KP_3 },
        { "KP4", GRAPL_VK_KP_4 },
        { "KP5", GRAPL_VK_KP_5 },
        { "KP6", GRAPL_VK_KP_6 },
        { "KP7", GRAPL_VK_KP_7 },
        { "KP8", GRAPL_VK_KP_8 },
        { "KP9", GRAPL_VK_KP_9 },

        { "KPDL", GRAPL_VK_KP_DECIMAL },
        { "KPDV", GRAPL_VK_KP_DIVIDE },
        { "KPMU", GRAPL_VK_KP_MULTIPLY },
        { "KPSU", GRAPL_VK_KP_SUBTRACT },
        { "KPAD", GRAPL_VK_KP_ADD },
        { "KPEN", GRAPL_VK_KP_ENTER },
        { "KPEQ", GRAPL_VK_KP_EQUAL },

        { "LFSH", GRAPL_VK_LEFT_SHIFT },
        { "LCTL", GRAPL_VK_LEFT_CONTROL },
        { "LALT", GRAPL_VK_LEFT_ALT },
        { "LWIN", GRAPL_VK_LEFT_SUPER },

        { "RTSH", GRAPL_VK_RIGHT_SHIFT },
        { "RCTL", GRAPL_VK_RIGHT_CONTROL },
        { "RALT", GRAPL_VK_RIGHT_ALT },
        { "LVL3", GRAPL_VK_RIGHT_ALT },
        { "MDSW", GRAPL_VK_RIGHT_ALT },
        { "RWIN", GRAPL_VK_RIGHT_SUPER },

        { "I173", GRAPL_VK_MEDIA_PREVIOUS },
        { "I171", GRAPL_VK_MEDIA_NEXT },
        { "PAUS", GRAPL_VK_MEDIA_PAUSE },

        { "VOL+", GRAPL_VK_VOLUME_UP },
        { "VOL-", GRAPL_VK_VOLUME_DOWN },
        { "MUTE", GRAPL_VK_VOLUME_MUTE },
    };

    XkbDescPtr desc = XkbGetMap(display, 0, XkbUseCoreKbd);
    XkbGetNames(display, XkbKeyNamesMask | XkbKeyAliasesMask, desc);

    for (int i = desc->min_key_code; i <= desc->max_key_code; i++){
        int key = -1;

        for (long unsigned int r = 0; r < sizeof(keys) / sizeof(keys[0]); r++) {
            if (strncmp(desc->names->keys[i].name, keys[r].name, XkbKeyNameLength) == 0){
                key = keys[i].key;
                break;
            }
        }

        if(key != -1){
            for (int r = 0; r < desc->names->num_key_aliases; r++){
                if (strncmp(desc->names->key_aliases[r].real, desc->names->keys[i].name, XkbKeyNameLength) != 0)
                    continue;

                for (long unsigned int j = 0; j < sizeof(keys) / sizeof(keys[0]); j++){
                    if (strncmp(desc->names->key_aliases[r].alias, keys[j].name, XkbKeyNameLength) == 0){
                        key = keys[j].key;
                        break;
                    }
                }
            }
        }

        if(key != -1)
            keyMap[i] = key;
    }
}