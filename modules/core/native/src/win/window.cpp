#include "grapl-win.h"
#include <map>

struct CallbackContainer {
    JNIEnv* env;
    jobject object;
    jmethodID onCloseCallback;
    jmethodID onResizeCallback;
    jmethodID onMoveCallback;
    jmethodID getCursorCallback;
    jmethodID getMinMaxBounds;
    jmethodID onFocusCallback;

    jmethodID onPointerMoveCallback;
    jmethodID onPointerDownCallback;
    jmethodID onPointerUpCallback;
    jmethodID onPointerEnterCallback;
    jmethodID onPointerLeaveCallback;
    jmethodID onPointerScrollCallback;

    jmethodID onPointerZoomBeginCallback;
    jmethodID onPointerZoomCallback;
    jmethodID onPointerZoomEndCallback;

    jmethodID onPointerRotationBeginCallback;
    jmethodID onPointerRotationCallback;
    jmethodID onPointerRotationEndCallback;
};
static std::map<HWND, CallbackContainer*> callbackObjects;

void addCallbacks(JNIEnv* env, HWND hwnd, jobject callbackObject){
    jclass callbackClass = env->GetObjectClass(callbackObject);
    callbackObjects[hwnd] = new CallbackContainer{
        env,
        env->NewGlobalRef(callbackObject),

        env->GetMethodID(callbackClass, "onCloseCallback", "()V"),
        env->GetMethodID(callbackClass, "onResizeCallback", "(II)V"),
        env->GetMethodID(callbackClass, "onMoveCallback", "(II)V"),
        env->GetMethodID(callbackClass, "getCursorCallback", "()I"),
        env->GetMethodID(callbackClass, "getMinMaxBounds", "()[I"),
        env->GetMethodID(callbackClass, "onFocusCallback", "(Z)V"),

        env->GetMethodID(callbackClass, "onPointerMoveCallback", "(IIII)V"),
        env->GetMethodID(callbackClass, "onPointerDownCallback", "(IIIII)V"),
        env->GetMethodID(callbackClass, "onPointerUpCallback", "(IIIII)V"),
        env->GetMethodID(callbackClass, "onPointerEnterCallback", "(IIII)V"),
        env->GetMethodID(callbackClass, "onPointerLeaveCallback", "(IIII)V"),
        env->GetMethodID(callbackClass, "onPointerScrollCallback", "(IIIDDI)V"),

        env->GetMethodID(callbackClass, "onPointerZoomBeginCallback", "(IIII)V"),
        env->GetMethodID(callbackClass, "onPointerZoomCallback", "(IIIDI)V"),
        env->GetMethodID(callbackClass, "onPointerZoomEndCallback", "(IIII)V"),

        env->GetMethodID(callbackClass, "onPointerRotationBeginCallback", "(IIII)V"),
        env->GetMethodID(callbackClass, "onPointerRotationCallback", "(IIIDI)V"),
        env->GetMethodID(callbackClass, "onPointerRotationEndCallback", "(IIII)V")
    };
}

void removeCallbacks(HWND hwnd){
    CallbackContainer* callbacks = callbackObjects[hwnd];

    callbackObjects.erase(hwnd);
    callbacks->env->DeleteGlobalRef(callbacks->object);
    delete callbacks;
}

jint getModifierKeys(){
    jint res = 0;
    if(GetKeyState(VK_MENU) < 0)    res |= 0x00000001;
    if(GetKeyState(VK_CONTROL) < 0) res |= 0x00000002;
    if(GetKeyState(VK_SHIFT) < 0)   res |= 0x00000004;
    return res;
}



LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam){
    if (!callbackObjects.count(hwnd))
        return DefWindowProc(hwnd, msg, wParam, lParam);

    CallbackContainer* callbacks = callbackObjects[hwnd];
    JNIEnv* env = callbacks->env;
    jobject object = callbacks->object;

    switch (msg){
        case WM_CLOSE: {
            env->CallVoidMethod(object, callbacks->onCloseCallback);
            break;
        }
        case WM_SIZE: {
            env->CallVoidMethod(object, callbacks->onResizeCallback, LOWORD(lParam), HIWORD(lParam));
            break;
        }
        case WM_MOVE: {
            env->CallVoidMethod(object, callbacks->onMoveCallback, LOWORD(lParam), HIWORD(lParam));
            break;
        }
        case WM_SETCURSOR: {
            jint cursor = env->CallIntMethod(object, callbacks->getCursorCallback);
            SetCursor(LoadCursor(NULL, MAKEINTRESOURCE(cursor)));
            break;
        }
        case WM_GETMINMAXINFO: {
            jobject result = env->CallObjectMethod(object, callbacks->getMinMaxBounds);
            jintArray* boundsArray = (jintArray*)&result;
            jint* bounds = env->GetIntArrayElements(*boundsArray, NULL);

            LPMINMAXINFO info = (LPMINMAXINFO)lParam;
            if(bounds[0] != -1) info->ptMinTrackSize.x = bounds[0];
            if(bounds[1] != -1) info->ptMinTrackSize.y = bounds[1];
            if(bounds[2] != -1) info->ptMaxTrackSize.x = bounds[2];
            if(bounds[3] != -1) info->ptMaxTrackSize.y = bounds[3];
            break;
        }
        case WM_SETFOCUS: {
            env->CallVoidMethod(object, callbacks->onFocusCallback, true);
            break;
        }
        case WM_KILLFOCUS: {
            env->CallVoidMethod(object, callbacks->onFocusCallback, false);
            break;
        }
        case WM_DESTROY: {
            removeCallbacks(hwnd);
            break;
        }
        case WM_MOUSELEAVE: {
            env->CallVoidMethod(object, callbacks->onPointerLeaveCallback,
                GetMessageExtraInfo() & 0x7F,
                GET_X_LPARAM(lParam),
                GET_Y_LPARAM(lParam),
                getModifierKeys()
            );
            break;
        }
        case WM_MOUSEMOVE: {
            env->CallVoidMethod(object, callbacks->onPointerMoveCallback,
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

            jmethodID callback;
            if(down){
                SetCapture(hwnd);
                callback = callbacks->onPointerDownCallback;
            }else {
                ReleaseCapture();
                callback = callbacks->onPointerUpCallback;
            }

            env->CallVoidMethod(object, callback,
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
            env->CallVoidMethod(object, callbacks->onPointerScrollCallback,
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
                    env->CallVoidMethod(object, callbacks->onPointerZoomBeginCallback,
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else if(gestureInfo.dwFlags & GF_BEGIN == GF_END){
                    env->CallVoidMethod(object, callbacks->onPointerZoomEndCallback,
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else {
                    env->CallVoidMethod(object, callbacks->onPointerZoomCallback,
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
                    env->CallVoidMethod(object, callbacks->onPointerRotationBeginCallback,
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else if(gestureInfo.dwFlags & GF_BEGIN == GF_END){
                    env->CallVoidMethod(object, callbacks->onPointerRotationEndCallback,
                        0,
                        gestureInfo.ptsLocation.x,
                        gestureInfo.ptsLocation.y,
                        getModifierKeys()
                    );
                }else {
                    env->CallVoidMethod(object, callbacks->onPointerRotationCallback,
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

