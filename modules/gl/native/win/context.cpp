#define UNICODE

#include "grapl-gl-win.h"


jni_win_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    checkBasicFunctions();

    GLint major, minor, flags;
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    glGetIntegerv(GL_CONTEXT_FLAGS, &flags);

    return createLongArray(env, {
        (jlong)_wglGetCurrentContext(), (jlong)_wglGetCurrentDC(), (jlong)major, (jlong)minor, (flags & GL_CONTEXT_FLAG_DEBUG_BIT) != 0
    });
}

jni_win_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong dc, jlong rc) {
    checkBasicFunctions();
    return _wglMakeCurrent((HDC)dc, (HGLRC)rc);
}

jni_win_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareRc, jint majorVersion, jint minorVersion, jboolean debug) {
    checkBasicFunctions();

    GLint context_attributes[] = {
            WGL_CONTEXT_PROFILE_MASK_ARB, isCore ? WGL_CONTEXT_CORE_PROFILE_BIT_ARB : WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            WGL_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            WGL_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            WGL_CONTEXT_FLAGS_ARB, debug ? WGL_CONTEXT_DEBUG_BIT_ARB : 0,
            0
    };

    HGLRC rc;
    if (!(rc = wglCreateContextAttribsARB(dc, (HGLRC)shareRc, context_attributes)))
        checkError("wglCreateContextAttribsARB");

    HGLRC oldRC = _wglGetCurrentContext();
    HDC oldDC = _wglGetCurrentDC();

    GLint major, minor, flags;

    _wglMakeCurrent(dc, rc);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    glGetIntegerv(GL_CONTEXT_FLAGS, &flags);
    _wglMakeCurrent(oldDC, oldRC);

    return createLongArray(env, {
        (jlong)rc, (jlong)dc, (jlong)major, (jlong)minor, (flags & GL_CONTEXT_FLAG_DEBUG_BIT) != 0
    });
}

jni_win_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong rc) {
    checkBasicFunctions();
    _wglDeleteContext((HGLRC)rc);
}

// Debug callback

static JavaVM* jvm = NULL;
static jclass debugCallbackClass;

void callbackFunction(GLenum source, GLenum type, GLuint id, GLenum severity, GLsizei length, const GLchar *message, const void *userParam){
    JNIEnv* env;
    jvm->AttachCurrentThread((void**)&env, NULL);

    jmethodID callbackMethod = env->GetStaticMethodID(debugCallbackClass, "dispatchDebug", "(JIIIILjava/lang/String;)V");
    env->CallStaticVoidMethod(debugCallbackClass, callbackMethod,
        (jlong)userParam,
        source,
        type,
        id,
        severity,
        env->NewStringUTF(message)
    );

    jvm->DetachCurrentThread();
}

jni_win_context(void, nBindDebugCallback)(JNIEnv* env, jobject, jclass callbackClass, jlong handle) {
    checkBasicFunctions();

    if(jvm == NULL){
        env->GetJavaVM(&jvm);
        debugCallbackClass = (jclass)env->NewGlobalRef(callbackClass);
    }
    glDebugMessageCallbackARB(&callbackFunction, _wglGetCurrentContext());
}
