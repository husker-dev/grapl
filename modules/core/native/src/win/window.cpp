#include "grapl-win.h"
#include "touchpad-manager.cpp"

#include <map>

LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);

class WinWindowCallbackContainer: public WindowCallbackContainer {
public:
    HWND hwnd;
    WNDPROC prevProc;
    TouchpadManager* touchpadManager;

    Callback* getCursorCallback;
    Callback* getMinMaxBounds;

    WinWindowCallbackContainer(JNIEnv* env, HWND hwnd, jobject callbackObject): WindowCallbackContainer(env, callbackObject) {
        this->hwnd = hwnd;

        touchpadManager = new TouchpadManager(hwnd);
        prevProc = (WNDPROC)SetWindowLongPtr(hwnd, GWLP_WNDPROC, (LONG_PTR)CustomWinProc);

        getCursorCallback = callback("getCursorCallback", "()I");
        getMinMaxBounds = callback("getMinMaxBounds", "()[I");
    }
};

static std::map<HWND, WinWindowCallbackContainer*> wrappers;


void removeCallbacks(HWND hwnd){
    WinWindowCallbackContainer* wrapper = wrappers[hwnd];
    wrappers.erase(hwnd);
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
        case 0x039: return GRAPL_VK_SPACE;
        case 0x028: return GRAPL_VK_APOSTROPHE;
        case 0x033: return GRAPL_VK_COMMA;
        case 0x00C: return GRAPL_VK_MINUS;
        case 0x034: return GRAPL_VK_PERIOD;
        case 0x035: return GRAPL_VK_SLASH;

        case 0x00B: return GRAPL_VK_0;
        case 0x002: return GRAPL_VK_1;
        case 0x003: return GRAPL_VK_2;
        case 0x004: return GRAPL_VK_3;
        case 0x005: return GRAPL_VK_4;
        case 0x006: return GRAPL_VK_5;
        case 0x007: return GRAPL_VK_6;
        case 0x008: return GRAPL_VK_7;
        case 0x009: return GRAPL_VK_8;
        case 0x00A: return GRAPL_VK_9;

        case 0x027: return GRAPL_VK_SEMICOLON;
        case 0x00D: return GRAPL_VK_EQUAL;

        case 0x01E: return GRAPL_VK_A;
        case 0x030: return GRAPL_VK_B;
        case 0x02E: return GRAPL_VK_C;
        case 0x020: return GRAPL_VK_D;
        case 0x012: return GRAPL_VK_E;
        case 0x021: return GRAPL_VK_F;
        case 0x022: return GRAPL_VK_G;
        case 0x023: return GRAPL_VK_H;
        case 0x017: return GRAPL_VK_I;
        case 0x024: return GRAPL_VK_J;
        case 0x025: return GRAPL_VK_K;
        case 0x026: return GRAPL_VK_L;
        case 0x032: return GRAPL_VK_M;
        case 0x031: return GRAPL_VK_N;
        case 0x018: return GRAPL_VK_O;
        case 0x019: return GRAPL_VK_P;
        case 0x010: return GRAPL_VK_Q;
        case 0x013: return GRAPL_VK_R;
        case 0x01F: return GRAPL_VK_S;
        case 0x014: return GRAPL_VK_T;
        case 0x016: return GRAPL_VK_U;
        case 0x02F: return GRAPL_VK_V;
        case 0x011: return GRAPL_VK_W;
        case 0x02D: return GRAPL_VK_X;
        case 0x015: return GRAPL_VK_Y;
        case 0x02C: return GRAPL_VK_Z;

        case 0x01A: return GRAPL_VK_LEFT_BRACKET;
        case 0x02B: return GRAPL_VK_BACKSLASH;
        case 0x01B: return GRAPL_VK_RIGHT_BRACKET;
        case 0x029: return GRAPL_VK_GRAVE_ACCENT;
        case 0x001: return GRAPL_VK_ESCAPE;
        case 0x01C: return GRAPL_VK_ENTER;
        case 0x00F: return GRAPL_VK_TAB;
        case 0x00E: return GRAPL_VK_BACKSPACE;
        case 0x152: return GRAPL_VK_INSERT;
        case 0x153: return GRAPL_VK_DELETE;

        case 0x14D: return GRAPL_VK_RIGHT;
        case 0x14B: return GRAPL_VK_LEFT;
        case 0x150: return GRAPL_VK_DOWN;
        case 0x148: return GRAPL_VK_UP;

        case 0x149: return GRAPL_VK_PAGE_UP;
        case 0x151: return GRAPL_VK_PAGE_DOWN;
        case 0x147: return GRAPL_VK_HOME;
        case 0x14F: return GRAPL_VK_END;
        case 0x03A: return GRAPL_VK_CAPS_LOCK;
        case 0x046: return GRAPL_VK_SCROLL_LOCK;
        case 0x145: return GRAPL_VK_NUM_LOCK;
        case 0x137: return GRAPL_VK_PRINT_SCREEN;
        case 0x03B: return GRAPL_VK_F1;
        case 0x03C: return GRAPL_VK_F2;
        case 0x03D: return GRAPL_VK_F3;
        case 0x03E: return GRAPL_VK_F4;
        case 0x03F: return GRAPL_VK_F5;
        case 0x040: return GRAPL_VK_F6;
        case 0x041: return GRAPL_VK_F7;
        case 0x042: return GRAPL_VK_F8;
        case 0x043: return GRAPL_VK_F9;
        case 0x044: return GRAPL_VK_F10;
        case 0x057: return GRAPL_VK_F11;
        case 0x058: return GRAPL_VK_F12;
        case 0x064: return GRAPL_VK_F13;
        case 0x065: return GRAPL_VK_F14;
        case 0x066: return GRAPL_VK_F15;
        case 0x067: return GRAPL_VK_F16;
        case 0x068: return GRAPL_VK_F17;
        case 0x069: return GRAPL_VK_F18;
        case 0x06A: return GRAPL_VK_F19;
        case 0x06B: return GRAPL_VK_F20;
        case 0x06C: return GRAPL_VK_F21;
        case 0x06D: return GRAPL_VK_F22;
        case 0x06E: return GRAPL_VK_F23;
        case 0x076: return GRAPL_VK_F24;

        case 0x052: return GRAPL_VK_KP_0;
        case 0x04F: return GRAPL_VK_KP_1;
        case 0x050: return GRAPL_VK_KP_2;
        case 0x051: return GRAPL_VK_KP_3;
        case 0x04B: return GRAPL_VK_KP_4;
        case 0x04C: return GRAPL_VK_KP_5;
        case 0x04D: return GRAPL_VK_KP_6;
        case 0x047: return GRAPL_VK_KP_7;
        case 0x048: return GRAPL_VK_KP_8;
        case 0x049: return GRAPL_VK_KP_9;
        case 0x053: return GRAPL_VK_KP_DECIMAL;
        case 0x135: return GRAPL_VK_KP_DIVIDE;
        case 0x037: return GRAPL_VK_KP_MULTIPLY;
        case 0x04A: return GRAPL_VK_KP_SUBTRACT;
        case 0x04E: return GRAPL_VK_KP_ADD;
        case 0x11C: return GRAPL_VK_KP_ENTER;
        case 0x059: return GRAPL_VK_KP_EQUAL;

        case 0x02A: return GRAPL_VK_LEFT_SHIFT;
        case 0x01D: return GRAPL_VK_LEFT_CONTROL;
        case 0x038: return GRAPL_VK_LEFT_ALT;
        case 0x15B: return GRAPL_VK_LEFT_SUPER;

        case 0x036: return GRAPL_VK_RIGHT_SHIFT;
        case 0x11D: return GRAPL_VK_RIGHT_CONTROL;
        case 0x138: return GRAPL_VK_RIGHT_ALT;
        case 0x15C: return GRAPL_VK_LEFT_SUPER;

        case 0x110: return GRAPL_VK_MEDIA_PREVIOUS;
        case 0x119: return GRAPL_VK_MEDIA_NEXT;
        case 0x122: return GRAPL_VK_MEDIA_PAUSE;

        case 0x130: return GRAPL_VK_VOLUME_UP;
        case 0x12E: return GRAPL_VK_VOLUME_DOWN;
        case 0x120: return GRAPL_VK_VOLUME_MUTE;
    }
    return GRAPL_VK_UNKNOWN;
}



LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam){
    if (!wrappers.count(hwnd))
        return DefWindowProc(hwnd, msg, wParam, lParam);

    WinWindowCallbackContainer* wrapper = wrappers[hwnd];
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
    wrappers[hwnd] = new WinWindowCallbackContainer(env, hwnd, callbackObject);
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

