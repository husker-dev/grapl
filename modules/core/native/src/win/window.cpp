#include "grapl-win.h"
#include <map>

#include <directmanipulation.h>


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

    IDirectManipulationManager* manipulationManager;

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

    Callback* printInt;

    WindowWrapper(JNIEnv* env, HWND hwnd, jobject callbackObject) {
        this->env = env;
        this->hwnd = hwnd;
        this->callbackObject = env->NewGlobalRef(callbackObject);
        this->callbackClass = env->GetObjectClass(callbackObject);

        CoInitializeEx(NULL, COINIT_MULTITHREADED);
        CoCreateInstance(CLSID_DirectManipulationManager, NULL, CLSCTX_ALL,
                IID_IDirectManipulationManager, (void**)(&this->manipulationManager));


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

        printInt = callback("printInt", "(I)V");
    }
private:
    Callback* callback(const char* name, const char* params){
        return new Callback(env, env->GetMethodID(callbackClass, name, params), callbackObject);
    }
};

static std::map<HWND, WindowWrapper*> wrappers;


void addCallbacks(JNIEnv* env, HWND hwnd, jobject callbackObject){
    wrappers[hwnd] = new WindowWrapper(env, hwnd, callbackObject);
}

void removeCallbacks(HWND hwnd){
    WindowWrapper* wrapper = wrappers[hwnd];

    wrappers.erase(hwnd);
    wrapper->env->DeleteGlobalRef(wrapper->callbackObject);
    delete wrapper;
}

jint getModifierKeys(){
    jint res = 0;
    if(GetKeyState(VK_MENU) < 0)    res |= 0x00000001;
    if(GetKeyState(VK_CONTROL) < 0) res |= 0x00000002;
    if(GetKeyState(VK_SHIFT) < 0)   res |= 0x00000004;
    return res;
}



LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam){
    if (!wrappers.count(hwnd))
        return DefWindowProc(hwnd, msg, wParam, lParam);

    WindowWrapper* wrapper = wrappers[hwnd];
    JNIEnv* env = wrapper->env;
    jobject object = wrapper->callbackObject;

    wrapper->printInt->call((jint)msg);

    switch (msg){
        case WM_CLOSE: {
            wrapper->onCloseCallback->call();
            break;
        }

        case WM_SIZE: {
            wrapper->onResizeCallback->call(LOWORD(lParam), HIWORD(lParam));

            if(wrapper->manipulationManager){
                switch (wParam){
                    case SIZE_MINIMIZED:
                    case SIZE_MAXHIDE:
                        wrapper->manipulationManager->Deactivate(hwnd);
                        break;
                    default:
                        wrapper->manipulationManager->Activate(hwnd);
                        break;
                }
            }
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
        case WM_GESTURE: {
            GESTUREINFO gestureInfo = {};
            gestureInfo.cbSize = sizeof(gestureInfo);
            GetGestureInfo((HGESTUREINFO)lParam, &gestureInfo);

            if(gestureInfo.dwID == GID_ZOOM){
                if(gestureInfo.dwFlags & GF_BEGIN == GF_BEGIN){
                    wrapper->onPointerZoomBeginCallback->call(
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else if(gestureInfo.dwFlags & GF_BEGIN == GF_END){
                    wrapper->onPointerZoomCallback->call(
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else {
                    wrapper->onPointerZoomEndCallback->call(
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        (jdouble)gestureInfo.ullArguments,
                        getModifierKeys()
                    );
                }
                return 0;
            }else if(gestureInfo.dwID == GID_ROTATE){
                if(gestureInfo.dwFlags & GF_BEGIN == GF_BEGIN){
                    wrapper->onPointerRotationBeginCallback->call(
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else if(gestureInfo.dwFlags & GF_BEGIN == GF_END){
                    wrapper->onPointerRotationCallback->call(
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else {
                    wrapper->onPointerRotationEndCallback->call(
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        (jdouble)gestureInfo.ullArguments,
                        getModifierKeys()
                    );
                }
                return 0;
            }
            break;
        }
    }
    return DefWindowProc(hwnd, msg, wParam, lParam);
}


/*
    JNI
*/

jni_win_window(void, nHookWindow)(JNIEnv* env, jobject, jlong _hwnd, jobject callbackObject) {
    HWND hwnd = (HWND)_hwnd;
    addCallbacks(env, hwnd, callbackObject);
    SetWindowLongPtr(hwnd, GWLP_WNDPROC, (LONG_PTR)CustomWinProc);
}

jni_win_window(void, nPeekMessage)(JNIEnv* env, jobject, jlong hwnd) {
    MSG msg = {};
    if(PeekMessageA(&msg, (HWND)hwnd, 0, 0, PM_REMOVE)){
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
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

