#ifndef GRAPL_SHARED_H
#define GRAPL_SHARED_H


#include "jni.h"
#include <initializer_list>
#include <vector>

// Long

static jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}

static jlongArray createLongArray(JNIEnv* env, std::initializer_list<jlong> array){
    return createLongArray(env, (int)array.size(), (jlong*)array.begin());
}

static jlongArray createLongArray(JNIEnv* env, std::vector<jlong> array){
    return createLongArray(env, array.size(), array.data());
}

// Integer

static jintArray createIntArray(JNIEnv* env, int size, jint* array){
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, array);
    return result;
}

static jintArray createIntArray(JNIEnv* env, std::initializer_list<jint> array){
    return createIntArray(env, (int)array.size(), (jint*)array.begin());
}

static jintArray createIntArray(JNIEnv* env, std::vector<jint> array){
    return createIntArray(env, array.size(), array.data());
}

// Double

static jdoubleArray createDoubleArray(JNIEnv* env, int size, jdouble* array){
    jdoubleArray result = env->NewDoubleArray(size);
    env->SetDoubleArrayRegion(result, 0, size, array);
    return result;
}

static jdoubleArray createDoubleArray(JNIEnv* env, std::initializer_list<jdouble> array){
    return createDoubleArray(env, (int)array.size(), (jdouble*)array.begin());
}

static jdoubleArray createDoubleArray(JNIEnv* env, std::vector<jdouble> array){
    return createDoubleArray(env, array.size(), array.data());
}

#endif