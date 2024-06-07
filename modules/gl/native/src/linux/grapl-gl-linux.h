#include "../grapl-gl.h"



#define jni_linux_context(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_GLXContext_##fun
#define jni_linux_platform(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_GLXPlatform_##fun
