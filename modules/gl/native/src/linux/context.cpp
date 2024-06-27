#include "grapl-gl-linux.h"


jni_linux_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion) {
    Display* display = XOpenDisplay(nullptr);
    auto glXCreateContextAttribsARB = (glXCreateContextAttribsARBPtr) glXGetProcAddressARB((GLubyte*) "glXCreateContextAttribsARB");
    auto glGetIntegerv = (glGetIntegervPtr) glXGetProcAddressARB((GLubyte*) "glGetIntegerv");

    int num_fbc = 0;
    static int visual_attribs[] = { None };
    GLXFBConfig* fbc = glXChooseFBConfig(display, DefaultScreen(display), visual_attribs, &num_fbc);

    // Creating context
    static int context_attribs[] = {
            GLX_CONTEXT_PROFILE_MASK_ARB, isCore ? GLX_CONTEXT_CORE_PROFILE_BIT_ARB : GLX_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
            GLX_CONTEXT_MAJOR_VERSION_ARB, (majorVersion == -1) ? 1 : majorVersion,
            GLX_CONTEXT_MINOR_VERSION_ARB, (minorVersion == -1) ? 0 : minorVersion,
            None
    };
    GLXContext context = glXCreateContextAttribsARB(display, fbc[0], (GLXContext)shareWith, true, context_attribs);

    // Creating PBuffer
    int pbufferAttribs[] = {
            GLX_PBUFFER_WIDTH,  32,
            GLX_PBUFFER_HEIGHT, 32,
            None
    };
    GLXPbuffer pbuffer = glXCreatePbuffer(display, fbc[0], pbufferAttribs);

    Display* oldDisplay = glXGetCurrentDisplay();
    GLXPbuffer oldPBuffer = glXGetCurrentDrawable();
    GLXContext oldContext = glXGetCurrentContext();
    GLint major, minor;

    glXMakeContextCurrent(display, pbuffer, pbuffer, context);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    glXMakeContextCurrent(oldDisplay, oldPBuffer, oldPBuffer, oldContext);

    return createLongArray(env, {
        (jlong)display, (jlong)pbuffer, (jlong)context, (jlong)major, (jlong)minor
    });
}

jni_linux_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    auto glGetIntegerv = (glGetIntegervPtr) glXGetProcAddressARB((GLubyte*) "glGetIntegerv");
    GLint major, minor;
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);

    return createLongArray(env, { (jlong)glXGetCurrentDisplay(), (jlong)glXGetCurrentDrawable(), (jlong)glXGetCurrentContext(), (jlong)major, (jlong)minor });
}

jni_linux_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong display, jlong pbuffer, jlong context) {
    return glXMakeContextCurrent((Display*)display, (GLXPbuffer)pbuffer, (GLXPbuffer)pbuffer, (GLXContext)context);
}

jni_linux_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong display, jlong context) {
    glXDestroyContext((Display*)display, (GLXContext)context);
}