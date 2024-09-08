#include "gl-shared.h"


jni_context(jobjectArray, nGetExtensions)(JNIEnv* env, jobject) {
    GLint extCount = 0;
    glGetIntegerv(GL_NUM_EXTENSIONS, &extCount);

    jobjectArray extensions = env->NewObjectArray(extCount, env->FindClass("java/lang/String"), NULL);

    for(int i = 0; i < extCount; i++){
        const char* ext = (const char*)glGetStringi(GL_EXTENSIONS, i);
        env->SetObjectArrayElement(extensions, i, env->NewStringUTF(ext));
    }
    return extensions;
}
