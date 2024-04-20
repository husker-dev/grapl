#import "grapl-macos.h"

jni_macos_utils(void, nInvokeOnMainThread)(JNIEnv* env, jobject, jobject runnable, jboolean wait) {
    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jobject runnableGlobal = env->NewGlobalRef(runnable);
    jclass runnableClass = env->GetObjectClass(runnableGlobal);
    jmethodID runMethod = env->GetMethodID(runnableClass, "run", "()V");

    [ThreadUtilities performOnMainThread:(wait ? YES : NO) block:^() {
        JNIEnv* env;
        jvm->AttachCurrentThread((void**)&env, NULL);
        env->CallVoidMethod(runnableGlobal, runMethod);
        env->DeleteGlobalRef(runnableGlobal);
        jvm->DetachCurrentThread();
    }];
}