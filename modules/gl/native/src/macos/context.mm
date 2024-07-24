#import "grapl-gl-macos.h"

void* a_GetProcAddress(const char* name) {
    if(libGL == NULL){
        static const char *NAMES[] = {
            "../Frameworks/OpenGL.framework/OpenGL",
            "/Library/Frameworks/OpenGL.framework/OpenGL",
            "/System/Library/Frameworks/OpenGL.framework/OpenGL",
            "/System/Library/Frameworks/OpenGL.framework/Versions/Current/OpenGL"
        };
        for(int i = 0; i < 4; i++)
            if((libGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
                break;
    }
    return dlsym(libGL, name);
}

void checkBasicFunctions(){
    if(libGL != NULL)
        return;
    glGetIntegerv = (glGetIntegervPtr)a_GetProcAddress("glGetIntegerv");
    glFlush = (glFlushPtr)a_GetProcAddress("glFlush");
}

jni_macos_context(jlongArray, nCreateContext)(JNIEnv* env, jobject, jboolean isCore, jlong shareWith, jint majorVersion, jint minorVersion) {
    checkBasicFunctions();
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

    CGLContextObj oldContext = CGLGetCurrentContext();
    GLint major, minor;

    CGLSetCurrentContext(context);
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);
    CGLSetCurrentContext(oldContext);

    return createLongArray(env, {
        (jlong)context, (jlong)major, (jlong)minor
    });
}

jni_macos_context(jlongArray, nGetCurrentContext)(JNIEnv* env, jobject) {
    checkBasicFunctions();
    GLint major, minor;
    glGetIntegerv(GL_MAJOR_VERSION, &major);
    glGetIntegerv(GL_MINOR_VERSION, &minor);

    return createLongArray(env, {
        (jlong)CGLGetCurrentContext(), (jlong)major, (jlong)minor
    });
}

jni_macos_context(jboolean, nSetCurrentContext)(JNIEnv* env, jobject, jlong context) {
    return checkError(CGLSetCurrentContext((CGLContextObj)context)) == kCGLNoError;
}

jni_macos_context(void, nDeleteContext)(JNIEnv* env, jobject, jlong context) {
    checkError(CGLDestroyContext((CGLContextObj)context));
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


