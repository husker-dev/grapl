#import "gl-macos.h"


static void getContextDetailsCGL(GLDetails* details, CGLContextObj context){
    CGLContextObj oldContext = CGLGetCurrentContext();

    CGLSetCurrentContext(context);
    getContextDetails(details);
    CGLSetCurrentContext(oldContext);
}


jni_macos_context(void, nInitFunctions)(JNIEnv* env, jobject) {
    glGetIntegerv = (glGetIntegervPtr)a_GetProcAddress("glGetIntegerv");
}

jni_macos_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion, jboolean debug) {
    CGLContextObj context;

    CGLPixelFormatObj pix;
    GLint num;
    CGLPixelFormatAttribute attributes[5] = {
            kCGLPFADoubleBuffer,
            kCGLPFAAccelerated,
            kCGLPFAOpenGLProfile,
            (CGLPixelFormatAttribute) (isCore ?
                ((majorVersion >= 4 || majorVersion == -1) ? kCGLOGLPVersion_GL4_Core : kCGLOGLPVersion_GL3_Core)
                : kCGLOGLPVersion_Legacy
            ),
            (CGLPixelFormatAttribute) 0
    };

    checkError(CGLChoosePixelFormat(attributes, &pix, &num));
    checkError(CGLCreateContext(pix, (CGLContextObj)shareWith, &context));
    checkError(CGLDestroyPixelFormat(pix));

    GLDetails details = {};
    getContextDetailsCGL(&details, context);

    return createLongArray(env, {
        (jlong) context,
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_macos_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    GLDetails details = {};
    getContextDetails(&details);

    return createLongArray(env, {
        (jlong) CGLGetCurrentContext(),
        (jlong) details.major,
        (jlong) details.minor,
        (jlong) details.isCore,
        (jlong) details.debug
    });
}

jni_macos_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong context) {
    return checkError(CGLSetCurrentContext((CGLContextObj)context)) == kCGLNoError;
}

jni_macos_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong context) {
    checkError(CGLDestroyContext((CGLContextObj)context));
}

jni_macos_context(jboolean, nHasFunction)(JNIEnv* env, jobject, jstring _name) {
    const char* name = env->GetStringUTFChars(_name, 0);
    jboolean result = a_GetProcAddress(name) != 0;
    env->ReleaseStringUTFChars(_name, name);
    return result;
}

jni_macos_context(void, nSetBackingSize)(JNIEnv* env, jobject, jlong _context, jint width, jint height) {
    CGLContextObj context = (CGLContextObj)_context;
    GLint dim[2] = { width, height};
    CGLSetParameter(context, kCGLCPSurfaceBackingSize, dim);
    CGLEnable(context, kCGLCESurfaceBackingSize);
}

jni_macos_context(void, nLockContext)(JNIEnv* env, jobject, jlong _context) {
    CGLContextObj context = (CGLContextObj)_context;
    CGLLockContext(context);
}

jni_macos_context(void, nUnlockContext)(JNIEnv* env, jobject, jlong _context) {
    CGLContextObj context = (CGLContextObj)_context;
    CGLUnlockContext(context);
}


