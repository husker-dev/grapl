#include "grapl-win.h"

static void peekMessage(){
    MSG msg = {};
    if(PeekMessageA(&msg, NULL, 0, 0, PM_REMOVE)){
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
}

jni_win_platform(void, nPeekMessage)(JNIEnv* env, jobject) {
    peekMessage();
}

jni_win_platform(void, nWaitMessage)(JNIEnv* env, jobject, jint timeout) {
    if(timeout == -1)
        WaitMessage();
    else
        MsgWaitForMultipleObjects(0, NULL, FALSE, (DWORD)timeout, QS_ALLINPUT);
    peekMessage();
}

jni_win_platform(void, nPostEmptyMessage)(JNIEnv* env, jobject) {
     PostMessageW(NULL, WM_NULL, 0, 0);
}