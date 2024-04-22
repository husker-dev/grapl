#include "grapl-win.h"
#include <map>

struct CallbackContainer {
    JNIEnv* env;
    jobject object;
    jmethodID onCloseCallback;
    jmethodID onResizeCallback;
    jmethodID onMoveCallback;
    jmethodID getCursorCallback;
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
        env->GetMethodID(callbackClass, "getCursorCallback", "()I")
    };
}

void removeCallbacks(HWND hwnd){
    CallbackContainer* callbacks = callbackObjects[hwnd];

    callbackObjects.erase(hwnd);
    callbacks->env->DeleteGlobalRef(callbacks->object);
    delete callbacks;
}

LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam){
    if (!callbackObjects.count(hwnd))
        return DefWindowProc(hwnd, uMsg, wParam, lParam);

    CallbackContainer* callbacks = callbackObjects[hwnd];
    JNIEnv* env = callbacks->env;
    jobject object = callbacks->object;

    switch (uMsg){
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
        case WM_DESTROY: {
            removeCallbacks(hwnd);
            break;
        }
    }
    return DefWindowProc(hwnd, uMsg, wParam, lParam);
}


/*
    JNI
*/

jni_win_window(void, nHookWindow)(JNIEnv* env, jobject, jlong hwnd, jobject callbackObject) {
    addCallbacks(env, (HWND)hwnd, callbackObject);
    SetWindowLongPtr((HWND)hwnd, GWLP_WNDPROC, (LONG_PTR)CustomWinProc);
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

jni_win_window(void, nSetSize)(JNIEnv* env, jobject, jlong hwnd, jint width, jint height) {
    SetWindowPos((HWND)hwnd, 0, 0, 0, width, height, SWP_NOMOVE);
    UpdateWindow((HWND)hwnd);
}

jni_win_window(void, nSetTitle)(JNIEnv* env, jobject, jlong hwnd, jobject _title) {
    char* title = (char*)env->GetDirectBufferAddress(_title);
    SetWindowTextW((HWND)hwnd, (LPCWSTR)title);
}

jni_win_window(jlong, nGetMonitor)(JNIEnv* env, jobject, jlong hwnd) {
    return (jlong)MonitorFromWindow((HWND)hwnd, MONITOR_DEFAULTTONEAREST);
}
