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

// Char

static jcharArray createCharArray(JNIEnv* env, int size, jchar* array){
    jcharArray result = env->NewCharArray(size);
    env->SetCharArrayRegion(result, 0, size, array);
    return result;
}

static jcharArray createCharArray(JNIEnv* env, std::initializer_list<jchar> array){
    return createCharArray(env, (int)array.size(), (jchar*)array.begin());
}

static jcharArray createCharArray(JNIEnv* env, std::vector<jchar> array){
    return createCharArray(env, array.size(), array.data());
}

// Byte

static jbyteArray createByteArray(JNIEnv* env, int size, jbyte* array){
    jbyteArray result = env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0, size, array);
    return result;
}

static jbyteArray createByteArray(JNIEnv* env, std::initializer_list<jbyte> array){
    return createByteArray(env, (int)array.size(), (jbyte*)array.begin());
}

static jbyteArray createByteArray(JNIEnv* env, std::vector<jbyte> array){
    return createByteArray(env, array.size(), array.data());
}


#define GRAPL_VK_UNKNOWN -1

#define GRAPL_VK_SPACE 32
#define GRAPL_VK_APOSTROPHE 39
#define GRAPL_VK_COMMA 44
#define GRAPL_VK_MINUS 45
#define GRAPL_VK_PERIOD 46
#define GRAPL_VK_SLASH 47

#define GRAPL_VK_0 48
#define GRAPL_VK_1 49
#define GRAPL_VK_2 50
#define GRAPL_VK_3 51
#define GRAPL_VK_4 52
#define GRAPL_VK_5 53
#define GRAPL_VK_6 54
#define GRAPL_VK_7 55
#define GRAPL_VK_8 56
#define GRAPL_VK_9 57

#define GRAPL_VK_SEMICOLON 59
#define GRAPL_VK_EQUAL 61

#define GRAPL_VK_A 65
#define GRAPL_VK_B 66
#define GRAPL_VK_C 67
#define GRAPL_VK_D 68
#define GRAPL_VK_E 69
#define GRAPL_VK_F 70
#define GRAPL_VK_G 71
#define GRAPL_VK_H 72
#define GRAPL_VK_I 73
#define GRAPL_VK_J 74
#define GRAPL_VK_K 75
#define GRAPL_VK_L 76
#define GRAPL_VK_M 77
#define GRAPL_VK_N 78
#define GRAPL_VK_O 79
#define GRAPL_VK_P 80
#define GRAPL_VK_Q 81
#define GRAPL_VK_R 82
#define GRAPL_VK_S 83
#define GRAPL_VK_T 84
#define GRAPL_VK_U 85
#define GRAPL_VK_V 86
#define GRAPL_VK_W 87
#define GRAPL_VK_X 88
#define GRAPL_VK_Y 89
#define GRAPL_VK_Z 90

#define GRAPL_VK_LEFT_BRACKET 91
#define GRAPL_VK_BACKSLASH 92
#define GRAPL_VK_RIGHT_BRACKET 93
#define GRAPL_VK_GRAVE_ACCENT 96
#define GRAPL_VK_ESCAPE 256
#define GRAPL_VK_ENTER 257
#define GRAPL_VK_TAB 258
#define GRAPL_VK_BACKSPACE 259
#define GRAPL_VK_INSERT 260
#define GRAPL_VK_DELETE 261

#define GRAPL_VK_RIGHT 262
#define GRAPL_VK_LEFT 263
#define GRAPL_VK_DOWN 264
#define GRAPL_VK_UP 265

#define GRAPL_VK_PAGE_UP 266
#define GRAPL_VK_PAGE_DOWN 267
#define GRAPL_VK_HOME 268
#define GRAPL_VK_END 269
#define GRAPL_VK_CAPS_LOCK 280
#define GRAPL_VK_SCROLL_LOCK 281
#define GRAPL_VK_NUM_LOCK 282
#define GRAPL_VK_PRINT_SCREEN 283

#define GRAPL_VK_F1 290
#define GRAPL_VK_F2 291
#define GRAPL_VK_F3 292
#define GRAPL_VK_F4 293
#define GRAPL_VK_F5 294
#define GRAPL_VK_F6 295
#define GRAPL_VK_F7 296
#define GRAPL_VK_F8 297
#define GRAPL_VK_F9 298
#define GRAPL_VK_F10 299
#define GRAPL_VK_F11 300
#define GRAPL_VK_F12 301
#define GRAPL_VK_F13 302
#define GRAPL_VK_F14 303
#define GRAPL_VK_F15 304
#define GRAPL_VK_F16 305
#define GRAPL_VK_F17 306
#define GRAPL_VK_F18 307
#define GRAPL_VK_F19 308
#define GRAPL_VK_F20 309
#define GRAPL_VK_F21 310
#define GRAPL_VK_F22 311
#define GRAPL_VK_F23 312
#define GRAPL_VK_F24 313
#define GRAPL_VK_F25 314

#define GRAPL_VK_KP_0 320
#define GRAPL_VK_KP_1 321
#define GRAPL_VK_KP_2 322
#define GRAPL_VK_KP_3 323
#define GRAPL_VK_KP_4 324
#define GRAPL_VK_KP_5 325
#define GRAPL_VK_KP_6 326
#define GRAPL_VK_KP_7 327
#define GRAPL_VK_KP_8 328
#define GRAPL_VK_KP_9 329
#define GRAPL_VK_KP_DECIMAL 330
#define GRAPL_VK_KP_DIVIDE 331
#define GRAPL_VK_KP_MULTIPLY 332
#define GRAPL_VK_KP_SUBTRACT 333
#define GRAPL_VK_KP_ADD 334
#define GRAPL_VK_KP_ENTER 335
#define GRAPL_VK_KP_EQUAL 336

#define GRAPL_VK_LEFT_SHIFT 340
#define GRAPL_VK_LEFT_CONTROL 341
#define GRAPL_VK_LEFT_ALT 342
#define GRAPL_VK_LEFT_SUPER 343

#define GRAPL_VK_RIGHT_SHIFT 344
#define GRAPL_VK_RIGHT_CONTROL 345
#define GRAPL_VK_RIGHT_ALT 346
#define GRAPL_VK_RIGHT_SUPER 347

#define GRAPL_VK_LEFT_COMMAND 349
#define GRAPL_VK_RIGHT_COMMAND 350

#define GRAPL_VK_MEDIA_PREVIOUS 360
#define GRAPL_VK_MEDIA_NEXT 361
#define GRAPL_VK_MEDIA_PAUSE 362

#define GRAPL_VK_VOLUME_UP 370
#define GRAPL_VK_VOLUME_DOWN 371
#define GRAPL_VK_VOLUME_MUTE 372


#endif