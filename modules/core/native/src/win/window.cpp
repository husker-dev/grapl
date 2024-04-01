#define UNICODE

#include "win-shared.h"

static JNIEnv* env;
static jclass callbacks = nullptr;

static jmethodID onDestroyCallback;
static jmethodID onResizeCallback;
static jmethodID onMoveCallback;

void checkCallbacks(JNIEnv* newEnv, jclass callbackClass){
    if(callbacks == nullptr){
        env = newEnv;
        callbacks = callbackClass;

        onDestroyCallback = env->GetStaticMethodID(callbacks, "onClose", "(J)V");
        onResizeCallback = env->GetStaticMethodID(callbacks, "onResize", "(JII)V");
        onMoveCallback = env->GetStaticMethodID(callbacks, "onMove", "(JII)V");
    }
}

LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam){
    switch (uMsg){
        case WM_CLOSE: {
            env->CallStaticVoidMethod(callbacks, onDestroyCallback, (jlong)hwnd);
            break;
        }
        case WM_SIZE: {
            env->CallStaticVoidMethod(callbacks, onResizeCallback, (jlong)hwnd, LOWORD(lParam), HIWORD(lParam));
            break;
        }
        case WM_MOVE: {
            env->CallStaticVoidMethod(callbacks, onMoveCallback, (jlong)hwnd, LOWORD(lParam), HIWORD(lParam));
            break;
        }
    }
    return DefWindowProc(hwnd, uMsg, wParam, lParam);
}


/*
    JNI
*/

jni_win_window(void, nHookWindow)(JNIEnv* env, jobject, jlong hwnd, jclass callbackClass) {
    checkCallbacks(env, callbackClass);
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
