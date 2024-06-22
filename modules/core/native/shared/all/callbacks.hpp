#include "jni.h"


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


class WindowCallbackContainer {
public:
    JNIEnv* env;
    jclass callbackClass;
    jobject callbackObject;

    Callback* onCloseCallback;
    Callback* onResizeCallback;
    Callback* onMoveCallback;
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

    WindowCallbackContainer(JNIEnv* env, jobject callbackObject) {
        this->env = env;
        this->callbackObject = env->NewGlobalRef(callbackObject);
        this->callbackClass = env->GetObjectClass(callbackObject);

        onCloseCallback = callback("onCloseCallback", "()V");
        onResizeCallback = callback("onResizeCallback", "(II)V");
        onMoveCallback = callback("onMoveCallback", "(II)V");
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

    ~WindowCallbackContainer(){
        env->DeleteGlobalRef(callbackObject);
        delete onCloseCallback;
        delete onResizeCallback;
        delete onMoveCallback;
        delete onFocusCallback;
        delete onPointerMoveCallback;
        delete onPointerDownCallback;
        delete onPointerUpCallback;
        delete onPointerEnterCallback;
        delete onPointerLeaveCallback;
        delete onPointerScrollCallback;

        delete onPointerZoomBeginCallback;
        delete onPointerZoomCallback;
        delete onPointerZoomEndCallback;

        delete onPointerRotationBeginCallback;
        delete onPointerRotationCallback;
        delete onPointerRotationEndCallback;

        delete onKeyDownCallback;
        delete onKeyUpCallback;
    }

    Callback* callback(const char* name, const char* params){
        return new Callback(env, env->GetMethodID(callbackClass, name, params), callbackObject);
    }
};