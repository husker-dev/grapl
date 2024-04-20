#ifndef GRAPL_SHARED_H
#define GRAPL_SHARED_H


#include "jni.h"

static jlongArray createLongArray(JNIEnv* env, int size, jlong* array){
    jlongArray result = env->NewLongArray(size);
    env->SetLongArrayRegion(result, 0, size, array);
    return result;
}

#endif