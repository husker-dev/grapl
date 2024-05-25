#include "grapl-win.h"
#include <map>

#include <iostream>

#include "touchpad-manager.cpp"

LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);

class Callback {
public:
    Callback(JNIEnv* env, jmethodID method, jobject callbackObject){
        this->env = env;
        this->method = method;
        this->callbackObject = callbackObject;
    }

    template<class... jvalue>
    void call(jvalue... values){
        env->CallVoidMethod(callbackObject, method, values...);
    }

    template<class... jvalue>
    jint callInt(jvalue... values){
        return env->CallIntMethod(callbackObject, method, values...);
    }

    template<class... jvalue>
    jobject callObject(jvalue... values){
        return env->CallObjectMethod(callbackObject, method, values...);
    }
private:
    JNIEnv* env;
    jmethodID method;
    jobject callbackObject;
};


class WindowWrapper {
public:
    HWND hwnd;
    WNDPROC prevProc;

    TouchpadManager* touchpadManager;

    JNIEnv* env;
    jclass callbackClass;
    jobject callbackObject;

    Callback* onCloseCallback;
    Callback* onResizeCallback;
    Callback* onMoveCallback;
    Callback* getCursorCallback;
    Callback* getMinMaxBounds;
    Callback* onFocusCallback;

    Callback* onPointerMoveCallback;
    Callback* onPointerDownCallback;
    Callback* onPointerUpCallback;
    Callback* onPointerEnterCallback;
    Callback* onPointerLeaveCallback;
    Callback* onPointerScrollCallback;
    Callback* onPointerZoomBeginCallback;
    Callback* onPointerZoomCallback;
    Callback* onPointerZoomEndCallback;

    Callback* onPointerRotationBeginCallback;
    Callback* onPointerRotationCallback;
    Callback* onPointerRotationEndCallback;

    Callback* onKeyDownCallback;
    Callback* onKeyUpCallback;

    WindowWrapper(JNIEnv* env, HWND hwnd, jobject callbackObject) {
        this->env = env;
        this->hwnd = hwnd;
        this->callbackObject = env->NewGlobalRef(callbackObject);
        this->callbackClass = env->GetObjectClass(callbackObject);

        touchpadManager = new TouchpadManager(hwnd);

        prevProc = (WNDPROC)GetWindowLongPtr(hwnd, GWLP_WNDPROC);
        SetWindowLongPtr(hwnd, GWLP_WNDPROC, (LONG_PTR)CustomWinProc);

        onCloseCallback = callback("onCloseCallback", "()V");
        onResizeCallback = callback("onResizeCallback", "(II)V");
        onMoveCallback = callback("onMoveCallback", "(II)V");
        getCursorCallback = callback("getCursorCallback", "()I");
        getMinMaxBounds = callback("getMinMaxBounds", "()[I");
        onFocusCallback = callback("onFocusCallback", "(Z)V");

        onPointerMoveCallback = callback("onPointerMoveCallback", "(IIII)V");
        onPointerDownCallback = callback("onPointerDownCallback", "(IIIII)V");
        onPointerUpCallback = callback("onPointerUpCallback", "(IIIII)V");
        onPointerEnterCallback = callback("onPointerEnterCallback", "(IIII)V");
        onPointerLeaveCallback = callback("onPointerLeaveCallback", "(IIII)V");
        onPointerScrollCallback = callback("onPointerScrollCallback", "(IIIDDI)V");

        onPointerZoomBeginCallback = callback("onPointerZoomBeginCallback", "(IIII)V");
        onPointerZoomCallback = callback("onPointerZoomCallback", "(IIIDI)V");
        onPointerZoomEndCallback = callback("onPointerZoomEndCallback", "(IIII)V");

        onPointerRotationBeginCallback = callback("onPointerRotationBeginCallback", "(IIII)V");
        onPointerRotationCallback = callback("onPointerRotationCallback", "(IIIDI)V");
        onPointerRotationEndCallback = callback("onPointerRotationEndCallback", "(IIII)V");

        onKeyDownCallback = callback("onKeyDownCallback", "(IILjava/lang/String;I)V");
        onKeyUpCallback = callback("onKeyUpCallback", "(IILjava/lang/String;I)V");
    }
private:
    Callback* callback(const char* name, const char* params){
        return new Callback(env, env->GetMethodID(callbackClass, name, params), callbackObject);
    }
};

static std::map<HWND, WindowWrapper*> wrappers;


void removeCallbacks(HWND hwnd){
    WindowWrapper* wrapper = wrappers[hwnd];

    wrappers.erase(hwnd);
    wrapper->env->DeleteGlobalRef(wrapper->callbackObject);
    delete wrapper;
}

jint getModifierKeys(){
    jint res = 0;
    if(GetKeyState(VK_MENU) < 0)    res |= 1;
    if(GetKeyState(VK_CONTROL) < 0) res |= 2;
    if(GetKeyState(VK_SHIFT) < 0)   res |= 4;
    return res;
}

jint translateKey(WPARAM key){
    switch(key){
        case 0x039: return 32; // SPACE
        case 0x028: return 39; // APOSTROPHE
        case 0x033: return 44; // COMMA
        case 0x00C: return 45; // MINUS
        case 0x034: return 46; // PERIOD
        case 0x035: return 47; // SLASH
        case 0x00B: return 48; // 0
        case 0x002: return 49; // 1
        case 0x003: return 50; // 2
        case 0x004: return 51; // 3
        case 0x005: return 52; // 4
        case 0x006: return 53; // 5
        case 0x007: return 54; // 6
        case 0x008: return 55; // 7
        case 0x009: return 56; // 8
        case 0x00A: return 57; // 9
        case 0x027: return 59; // ;
        case 0x00D: return 61; // =
        case 0x01E: return 65; // A
        case 0x030: return 66; // B
        case 0x02E: return 67; // C
        case 0x020: return 68; // D
        case 0x012: return 69; // E
        case 0x021: return 70; // F
        case 0x022: return 71; // G
        case 0x023: return 72; // H
        case 0x017: return 73; // I
        case 0x024: return 74; // J
        case 0x025: return 75; // K
        case 0x026: return 76; // L
        case 0x032: return 77; // M
        case 0x031: return 78; // N
        case 0x018: return 79; // O
        case 0x019: return 80; // P
        case 0x010: return 81; // Q
        case 0x013: return 82; // R
        case 0x01F: return 83; // S
        case 0x014: return 84; // T
        case 0x016: return 85; // U
        case 0x02F: return 86; // V
        case 0x011: return 87; // W
        case 0x02D: return 88; // X
        case 0x015: return 89; // Y
        case 0x02C: return 90; // Z
        case 0x01A: return 91; // [
        case 0x02B: return 92; // \.
        case 0x01B: return 93; // ]
        case 0x029: return 96; // GRAVE_ACCENT
        case 0x001: return 256; // ESC
        case 0x01C: return 257; // ENTER
        case 0x00F: return 258; // TAB
        case 0x00E: return 259; // BACKSPACE
        case 0x152: return 260; // INSERT
        case 0x153: return 261; // DELETE
        case 0x14D: return 262; // RIGHT
        case 0x14B: return 263; // LEFT
        case 0x150: return 264; // DOWN
        case 0x148: return 265; // UP
        case 0x149: return 266; // PAGE_UP
        case 0x151: return 267; // PAGE_DOWN
        case 0x147: return 268; // HOME
        case 0x14F: return 269; // END
        case 0x03A: return 280; // CAPS_LOCK
        case 0x046: return 281; // SCROLL_LOCK
        case 0x145: return 282;  /// NUM_LOCK
        case 0x137: return 283; // PRINT_SCREEN
        case 0x03B: return 290; // F1
        case 0x03C: return 291; // F2
        case 0x03D: return 292; // F3
        case 0x03E: return 293; // F4
        case 0x03F: return 294; // F5
        case 0x040: return 295; // F6
        case 0x041: return 296; // F7
        case 0x042: return 297; // F8
        case 0x043: return 298; // F9
        case 0x044: return 299; // F10
        case 0x057: return 300; // F11
        case 0x058: return 301; // F12
        case 0x064: return 302; // F13
        case 0x065: return 303; // F14
        case 0x066: return 304; // F15
        case 0x067: return 305; // F16
        case 0x068: return 306; // F17
        case 0x069: return 307; // F18
        case 0x06A: return 308; // F19
        case 0x06B: return 309; // F20
        case 0x06C: return 310; // F21
        case 0x06D: return 311; // F22
        case 0x06E: return 312; // F23
        case 0x076: return 313; // F24
        case 0x052: return 320; // KP_0
        case 0x04F: return 321; // KP_1
        case 0x050: return 322; // KP_2
        case 0x051: return 323; // KP_3
        case 0x04B: return 324; // KP_4
        case 0x04C: return 325; // KP_5
        case 0x04D: return 326; // KP_6
        case 0x047: return 327; // KP_7
        case 0x048: return 328; // KP_8
        case 0x049: return 329; // KP_9
        case 0x053: return 330; // KP_DECIMAL
        case 0x135: return 331; // KP_DIVIDE
        case 0x037: return 332; // KP_MULTIPLY
        case 0x04A: return 333; // KP_SUBTRACT
        case 0x04E: return 334; // KP_ADD
        case 0x11C: return 335; // KP_ENTER
        case 0x059: return 336; // KP_EQUAL

        case 0x02A: return 340; // LEFT_SHIFT
        case 0x01D: return 341; // LEFT_CONTROL
        case 0x038: return 342; // LEFT_ALT
        case 0x15B: return 343; // LEFT_SUPER

        case 0x036: return 344; // RIGHT_SHIFT
        case 0x11D: return 345; // RIGHT_CONTROL
        case 0x138: return 346; // RIGHT_ALT
        case 0x15C: return 347; // LEFT_SUPER

        case 0x110: return 360; // VK_MEDIA_PREVIOUS
        case 0x119: return 361; // VK_MEDIA_NEXT
        case 0x122: return 362; // VK_MEDIA_PAUSE

        case 0x130: return 370; // VK_VOLUME_UP
        case 0x12E: return 371; // VK_VOLUME_DOWN
        case 0x120: return 372; // VK_VOLUME_MUTE
    }
    return -1;
}



LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam){
    if (!wrappers.count(hwnd))
        return DefWindowProc(hwnd, msg, wParam, lParam);

    WindowWrapper* wrapper = wrappers[hwnd];
    JNIEnv* env = wrapper->env;

    switch (msg){
        case WM_CLOSE: {
            wrapper->onCloseCallback->call();
            break;
        }
        case WM_SIZE: {
            wrapper->onResizeCallback->call(LOWORD(lParam), HIWORD(lParam));
            break;
        }
        case WM_MOVE: {
            wrapper->onMoveCallback->call(LOWORD(lParam), HIWORD(lParam));
            break;
        }
        case WM_SETCURSOR: {
            jint cursor = wrapper->getCursorCallback->callInt();
            SetCursor(LoadCursor(NULL, MAKEINTRESOURCE(cursor)));
            break;
        }
        case WM_GETMINMAXINFO: {
            jobject result = wrapper->getMinMaxBounds->callObject();
            jintArray* boundsArray = (jintArray*)&result;
            jint* bounds = env->GetIntArrayElements(*boundsArray, NULL);

            LPMINMAXINFO info = (LPMINMAXINFO)lParam;
            if(bounds[0] != -1) info->ptMinTrackSize.x = bounds[0];
            if(bounds[1] != -1) info->ptMinTrackSize.y = bounds[1];
            if(bounds[2] != -1) info->ptMaxTrackSize.x = bounds[2];
            if(bounds[3] != -1) info->ptMaxTrackSize.y = bounds[3];
            break;
        }
        case WM_KILLFOCUS:
        case WM_SETFOCUS: {
            wrapper->onFocusCallback->call(msg == WM_SETFOCUS);
            break;
        }
        case WM_DESTROY: {
            removeCallbacks(hwnd);
            break;
        }
        case WM_MOUSELEAVE: {
            wrapper->onPointerLeaveCallback->call(
                GetMessageExtraInfo() & 0x7F,
                GET_X_LPARAM(lParam),
                GET_Y_LPARAM(lParam),
                getModifierKeys()
            );
            break;
        }
        case WM_MOUSEMOVE: {
            wrapper->onPointerMoveCallback->call(
                GetMessageExtraInfo() & 0x7F,
                GET_X_LPARAM(lParam),
                GET_Y_LPARAM(lParam),
                getModifierKeys()
            );
            break;
        }
        case WM_LBUTTONDOWN:
        case WM_LBUTTONUP:
        case WM_MBUTTONDOWN:
        case WM_MBUTTONUP:
        case WM_RBUTTONDOWN:
        case WM_RBUTTONUP:
        case WM_XBUTTONDOWN:
        case WM_XBUTTONUP: {
            UINT button;
            if(msg == WM_LBUTTONDOWN || msg == WM_LBUTTONUP)        button = 1;
            else if(msg == WM_MBUTTONDOWN || msg == WM_MBUTTONUP)   button = 2;
            else if(msg == WM_RBUTTONDOWN || msg == WM_RBUTTONUP)   button = 3;
            else                                                    button = 3 + HIWORD(wParam);

            UINT pointerId = GetMessageExtraInfo() & 0x7F;
            BOOL down = msg == WM_LBUTTONDOWN ||
                        msg == WM_MBUTTONDOWN ||
                        msg == WM_RBUTTONDOWN ||
                        msg == WM_XBUTTONDOWN;

            Callback* callback;
            if(down){
                SetCapture(hwnd);
                callback = wrapper->onPointerDownCallback;
            }else {
                ReleaseCapture();
                callback = wrapper->onPointerUpCallback;
            }

            callback->call(
                pointerId,
                GET_X_LPARAM(lParam),
                GET_Y_LPARAM(lParam),
                button,
                getModifierKeys()
            );
            break;
        }
        case WM_MOUSEWHEEL:
        case WM_MOUSEHWHEEL: {
            wrapper->onPointerScrollCallback->call(
                GetMessageExtraInfo() & 0x7F,
                GET_X_LPARAM(lParam),
                GET_Y_LPARAM(lParam),
                (msg == WM_MOUSEWHEEL) ? (jdouble)GET_WHEEL_DELTA_WPARAM(wParam) : 0,
                (msg == WM_MOUSEHWHEEL) ? (jdouble)GET_WHEEL_DELTA_WPARAM(wParam) : 0,
                getModifierKeys()
            );
            break;
        }
        case TM_SCROLL: {
            POINT point;
            GetCursorPos(&point);
            wrapper->onPointerScrollCallback->call(
                0,
                point.x,
                point.y,
                (jdouble)wParam,
                (jdouble)lParam,
                getModifierKeys()
            );
            break;
        }
        case TM_SCALE_BEGIN: {
            POINT point;
            GetCursorPos(&point);
            wrapper->onPointerZoomBeginCallback->call(
                0,
                point.x,
                point.y,
                getModifierKeys()
            );
            break;
        }
        case TM_SCALE: {
            POINT point;
            GetCursorPos(&point);
            wrapper->onPointerZoomEndCallback->call(
                0,
                point.x,
                point.y,
                (jdouble)FLOAT_FROM_PARAMS(wParam, lParam),
                getModifierKeys()
            );
            break;
        }
        case TM_SCALE_END: {
            POINT point;
            GetCursorPos(&point);
            wrapper->onPointerZoomEndCallback->call(
                0,
                point.x,
                point.y,
                getModifierKeys()
            );
            break;
        }
        case WM_KEYDOWN:
        case WM_SYSKEYDOWN:
        case WM_KEYUP:
        case WM_SYSKEYUP: {
            int scancode = (HIWORD(lParam) & (KF_EXTENDED | 0xff));
            if (!scancode)
                scancode = MapVirtualKeyW((UINT) wParam, MAPVK_VK_TO_VSC);

            // HACK: Alt+PrtSc has a different scancode than just PrtSc
            if (scancode == 0x54)
                scancode = 0x137;

            // HACK: Ctrl+Pause has a different scancode than just Pause
            if (scancode == 0x146)
                scancode = 0x45;

            // HACK: CJK IME sets the extended bit for right Shift
            if (scancode == 0x136)
                scancode = 0x36;

            BYTE keyboardState[256];
            GetKeyboardState(keyboardState);

            wchar_t unicodeBuffer[5] = {};
            int bufferLength = ToUnicode((UINT)wParam, scancode, keyboardState, unicodeBuffer, 5, 0);
            jstring unicodeString = env->NewString((const jchar*)&unicodeBuffer, bufferLength);

            if(HIWORD(lParam) & KF_UP){
                wrapper->onKeyUpCallback->call(
                    translateKey(scancode),
                    scancode,
                    unicodeString,
                    getModifierKeys()
                );
            }else {
                wrapper->onKeyDownCallback->call(
                    translateKey(scancode),
                    scancode,
                    unicodeString,
                    getModifierKeys()
                );
            }

            break;
        }
    }
    return CallWindowProcA(wrapper->prevProc, hwnd, msg, wParam, lParam);
}


/*
    JNI
*/

jni_win_window(void, nHookWindow)(JNIEnv* env, jobject, jlong _hwnd, jobject callbackObject) {
    HWND hwnd = (HWND)_hwnd;
    wrappers[hwnd] = new WindowWrapper(env, hwnd, callbackObject);
}

jni_win_window(void, nPostQuit)(JNIEnv* env, jobject, jlong hwnd) {
    PostMessage((HWND)hwnd, WM_CLOSE, 0, 0);
}

jni_win_window(void, nSetVisible)(JNIEnv* env, jobject, jlong hwnd, jboolean value) {
    ShowWindow((HWND)hwnd, value ? SW_SHOW : SW_HIDE);
}

jni_win_window(void, nSetPosition)(JNIEnv* env, jobject, jlong hwnd, jint x, jint y) {
    SetWindowPos((HWND)hwnd, 0, x, y, 0, 0, SWP_NOSIZE);
    UpdateWindow((HWND)hwnd);
}

jni_win_window(void, nSetSize)(JNIEnv* env, jobject, jlong _hwnd, jint width, jint height) {
    HWND hwnd = (HWND)_hwnd;
    LONG style = GetWindowLong(hwnd, GWL_STYLE);
    LONG exStyle = GetWindowLong(hwnd, GWL_EXSTYLE);
    RECT rect = { 0, 0, width, height };

    AdjustWindowRectEx(&rect, style, 0, exStyle);
    SetWindowPos(hwnd, 0, 0, 0, rect.right - rect.left, rect.bottom - rect.top, SWP_NOMOVE);
    UpdateWindow(hwnd);
}

jni_win_window(void, nSetTitle)(JNIEnv* env, jobject, jlong hwnd, jobject _title) {
    char* title = (char*)env->GetDirectBufferAddress(_title);
    SetWindowTextW((HWND)hwnd, (LPCWSTR)title);
}

jni_win_window(jlong, nGetMonitor)(JNIEnv* env, jobject, jlong hwnd) {
    return (jlong)MonitorFromWindow((HWND)hwnd, MONITOR_DEFAULTTONEAREST);
}

jni_win_window(jint, nUpdateDisplayState)(JNIEnv* env, jobject, jlong hwnd, jboolean isFullscreen, jlong monitor, jint width, jint height, jint bits, jint frequency) {
    if(isFullscreen){
        MONITORINFOEXW info;
        info.cbSize = sizeof(info);
        GetMonitorInfoW((HMONITOR)monitor, &info);

        DEVMODE dm = {};
        DWORD iModeNum = 0;
        while (EnumDisplaySettings(info.szDevice, iModeNum++, &dm)){
            if(dm.dmPelsWidth == width &&
                dm.dmPelsHeight == height &&
                dm.dmBitsPerPel == bits &&
                dm.dmDisplayFrequency == frequency
            ) break;
        }

        if(dm.dmPelsWidth != width ||
            dm.dmPelsHeight != height ||
            dm.dmBitsPerPel != bits ||
            dm.dmDisplayFrequency != frequency
        ){
            dm = {};
            dm.dmSize = sizeof(dm);
            dm.dmPelsWidth = width;
            dm.dmPelsHeight = height;
            dm.dmBitsPerPel = bits;
            dm.dmDisplayFrequency = frequency;
            dm.dmFields = DM_BITSPERPEL | DM_PELSWIDTH | DM_PELSHEIGHT;
        }

        switch(ChangeDisplaySettings(&dm, CDS_FULLSCREEN)){
            case DISP_CHANGE_BADDUALVIEW: return 1;
            case DISP_CHANGE_BADFLAGS: return 2;
            case DISP_CHANGE_BADMODE: return 3;
            case DISP_CHANGE_BADPARAM: return 4;
            case DISP_CHANGE_FAILED: return 5;
            case DISP_CHANGE_NOTUPDATED: return 6;
            case DISP_CHANGE_RESTART: return 7;
        }

        DWORD dwExStyle = WS_EX_APPWINDOW;
        DWORD dwStyle = WS_POPUP;

        RECT rect = { 0, 0, width, height };
        AdjustWindowRectEx(&rect, dwStyle, FALSE, dwExStyle);

        SetWindowLongPtr((HWND)hwnd, GWL_STYLE, dwStyle);
        SetWindowLongPtr((HWND)hwnd, GWL_EXSTYLE, dwExStyle);

        SetWindowPos((HWND)hwnd, 0,
            rect.left,
            rect.top,
            rect.right - rect.left,
            rect.bottom - rect.top,
            SWP_NOZORDER | SWP_NOACTIVATE | SWP_FRAMECHANGED);
        return 0;
    }
}

jni_win_window(void, nTrackMouseEvent)(JNIEnv* env, jobject, jlong hwnd) {
    TRACKMOUSEEVENT tme = {};
    tme.cbSize = sizeof(tme);
    tme.dwFlags = TME_LEAVE;
    tme.hwndTrack = (HWND)hwnd;
    TrackMouseEvent(&tme);
}

jni_win_window(void, nSetMinimizable)(JNIEnv* env, jobject, jlong _hwnd, jboolean value) {
    HWND hwnd = (HWND)_hwnd;
    LONG style = GetWindowLong(hwnd, GWL_STYLE);
    if(value) style |= WS_MINIMIZEBOX;
    else      style &= ~WS_MINIMIZEBOX;
    SetWindowLong(hwnd, GWL_STYLE, style);
}

jni_win_window(void, nSetMaximizable)(JNIEnv* env, jobject, jlong _hwnd, jboolean value) {
    HWND hwnd = (HWND)_hwnd;
    LONG style = GetWindowLong(hwnd, GWL_STYLE);
    if(value) style |= WS_MAXIMIZEBOX;
    else      style &= ~WS_MAXIMIZEBOX;
    SetWindowLong(hwnd, GWL_STYLE, style);
}

