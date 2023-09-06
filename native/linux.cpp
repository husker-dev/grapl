#include <jni.h>
#include <GL/glx.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>

#define JNIEXPORT1 __attribute__((unused)) JNIEXPORT

typedef GLXContext (*glXCreateContextAttribsARBProc)(Display*, GLXFBConfig, GLXContext, Bool, const int*);
typedef Bool (*glXMakeContextCurrentARBProc)(Display*, GLXDrawable, GLXDrawable, GLXContext);
typedef void (*glXDestroyContextProc)(Display*, GLXContext);

static bool initialized = false;
static glXCreateContextAttribsARBProc   glXCreateContextAttribsARB;
static glXMakeContextCurrentARBProc     glXMakeContextCurrentARB;
static glXDestroyContextProc            glXDestroyContext;



jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}

void checkBasicFunctions() {
    if(initialized) return;
    initialized = true;

    glXCreateContextAttribsARB = (glXCreateContextAttribsARBProc)       glXGetProcAddressARB((GLubyte*) "glXCreateContextAttribsARB");
    glXMakeContextCurrentARB = (glXMakeContextCurrentARBProc)           glXGetProcAddressARB((GLubyte*) "glXMakeContextCurrent");
    glXDestroyContext = (glXDestroyContextProc)                         glXGetProcAddressARB((GLubyte*) "glXDestroyContext");
}

extern "C" {

JNIEXPORT1 jlongArray JNICALL Java_com_huskerdev_ojgl_platforms_LinuxGLPlatform_nCreateContext(JNIEnv* env, jobject, jboolean isCore, jlong shareWith) {
    checkBasicFunctions();
    Display* display = XOpenDisplay(nullptr);

    int num_fbc = 0;
    static int visual_attribs[] = { None };
    GLXFBConfig* fbc = glXChooseFBConfig(display, DefaultScreen(display), visual_attribs, &num_fbc);

    // Creating context
    static int context_attribs[] = {
            GLX_CONTEXT_PROFILE_MASK_ARB, isCore ? GLX_CONTEXT_CORE_PROFILE_BIT_ARB : GLX_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB,
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

    jlong array[] = { (jlong)display, (jlong)pbuffer, (jlong)context };
    return createLongArray(env, 3, array);
}

JNIEXPORT1 jlongArray JNICALL Java_com_huskerdev_ojgl_platforms_LinuxGLPlatform_nGetCurrentContext(JNIEnv* env, jobject) {
    checkBasicFunctions();
    jlong array[] = { (jlong)glXGetCurrentDisplay(), (jlong)glXGetCurrentDrawable(), (jlong)glXGetCurrentContext() };
    return createLongArray(env, 3, array);
}

JNIEXPORT1 jboolean JNICALL Java_com_huskerdev_ojgl_platforms_LinuxGLPlatform_nSetCurrentContext(JNIEnv* env, jobject, jlong display, jlong pbuffer, jlong context) {
    checkBasicFunctions();
    return glXMakeContextCurrentARB((Display*)display, (GLXPbuffer)pbuffer, (GLXPbuffer)pbuffer, (GLXContext)context);
}

JNIEXPORT1 void JNICALL Java_com_huskerdev_ojgl_platforms_LinuxGLPlatform_nDeleteContext(JNIEnv* env, jobject, jlong display, jlong context) {
    checkBasicFunctions();
    glXDestroyContext((Display*)display, (GLXContext)context);
}
}