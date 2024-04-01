#define UNICODE

#include "win-shared.h"


jni_win_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    checkBasicFunctions();

    GLint major, minor;
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);

    jlong array[4] = { (jlong)_wglGetCurrentContext(), (jlong)_wglGetCurrentDC(), (jlong)major, (jlong)minor };
    return createLongArray(env, 4, array);
}

jni_win_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong dc, jlong rc) {
    checkBasicFunctions();
    return _wglMakeCurrent((HDC)dc, (HGLRC)rc);
}

jni_win_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareRc, jint majorVersion, jint minorVersion) {
    checkBasicFunctions();

    GLint context_attributes[] = {
            WGL_CONTEXT_PROFILE_MASK_ARB, isCore ? WGL_CONTEXT_CORE_PROFILE_BIT_ARB : WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            WGL_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            WGL_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            0
    };

    HGLRC rc;
    if (!(rc = wglCreateContextAttribsARB(dc, (HGLRC)shareRc, context_attributes)))
        checkError("wglCreateContextAttribsARB");

    HGLRC oldRC = _wglGetCurrentContext();
    HDC oldDC = _wglGetCurrentDC();
    GLint major, minor;

    _wglMakeCurrent(dc, rc);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    _wglMakeCurrent(oldDC, oldRC);

    jlong array[] = { (jlong)rc, (jlong)dc, (jlong)major, (jlong)minor };
    return createLongArray(env, 4, array);
}

jni_win_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong rc) {
    checkBasicFunctions();
    _wglDeleteContext((HGLRC)rc);
}
