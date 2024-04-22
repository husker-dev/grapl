#ifndef GRAPL_SHARED_H
#define GRAPL_SHARED_H


#include "jni.h"

static jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}

static jlongArray createLongArray(JNIEnv* env, jlong* array){
    int size = sizeof(array) / sizeof(*array);
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}

static jintArray createIntArray(JNIEnv* env, int size, jint* array){
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, array);
    return result;
}

static jintArray createIntArray(JNIEnv* env, jint* array){
    int size = sizeof(array) / sizeof(jint);
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, array);
    return result;
}

#endif