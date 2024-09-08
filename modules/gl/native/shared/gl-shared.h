#ifndef GRAPL_GL_H
#define GRAPL_GL_H

#include <grapl.h>
#include <stdio.h>

static JavaVM* jvm = NULL;
static jclass debugCallbackClass;

#define jni_context(returnType, fun) extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_GLContext_##fun

typedef int GLint;
typedef int GLsizei;
typedef float GLfloat;
typedef void GLvoid;
typedef char GLchar;
typedef unsigned int GLuint;
typedef unsigned int GLenum;
typedef unsigned char GLubyte;
typedef unsigned int GLbitfield;
typedef unsigned char GLboolean;
typedef signed long int GLintptr;
typedef signed long int GLsizeiptr;

#define GL_FALSE          0
#define GL_TRUE           1
#define GL_MAJOR_VERSION  0x821B
#define GL_MINOR_VERSION  0x821C
#define GL_CONTEXT_FLAGS  0x821E
#define GL_CONTEXT_FLAG_DEBUG_BIT 0x00000002
#define GL_CONTEXT_PROFILE_MASK 0x9126
#define GL_CONTEXT_CORE_PROFILE_BIT 0x00000001
#define GL_NUM_EXTENSIONS 0x821D
#define GL_EXTENSIONS     0x1F03

typedef void (*GLDEBUGPROCARB)(GLenum source, GLenum type, GLuint id, GLenum severity, GLsizei length, const GLchar *message, const void *userParam);

typedef void (*glGetIntegervPtr)(GLenum pname, GLint* data);
typedef const GLubyte* (*glGetStringiPtr)(GLenum name, GLuint index);
typedef void (*glDebugMessageCallbackARBPtr)(GLDEBUGPROCARB callback, const void *userParam);

extern glGetIntegervPtr glGetIntegerv;
extern glGetStringiPtr glGetStringi;
extern glDebugMessageCallbackARBPtr glDebugMessageCallbackARB;

struct GLDetails {
    GLint major;
    GLint minor;
    GLint flags;
    bool isCore;
    bool debug;
};


static void getContextDetails(GLDetails* details){
    GLint profileMask = 0;

    glGetIntegerv(GL_MAJOR_VERSION, &details->major);
    glGetIntegerv(GL_MINOR_VERSION, &details->minor);
    glGetIntegerv(GL_CONTEXT_FLAGS, &details->flags);
    glGetIntegerv(GL_CONTEXT_PROFILE_MASK, &profileMask);

    details->isCore = profileMask == GL_CONTEXT_CORE_PROFILE_BIT;
    details->debug = (details->flags & GL_CONTEXT_FLAG_DEBUG_BIT) != 0;
}

static void callbackFunction(GLenum source, GLenum type, GLuint id, GLenum severity, GLsizei length, const GLchar *message, const void *userParam){
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

static void bindDefaultDebugFunction(JNIEnv* env, jclass callbackClass, const void* handle) {
    if(glDebugMessageCallbackARB == NULL)
        return;
    if(jvm == NULL){
        env->GetJavaVM(&jvm);
        debugCallbackClass = (jclass)env->NewGlobalRef(callbackClass);
    }
    glDebugMessageCallbackARB(&callbackFunction, handle);
}

#endif