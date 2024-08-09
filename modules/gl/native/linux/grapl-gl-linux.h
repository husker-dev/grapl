#include "grapl-gl.h"

#include <jni.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#define __gl_h_
#include <GL/glx.h>

typedef GLXContext (*glXCreateContextAttribsARBPtr)(Display*, GLXFBConfig, GLXContext, Bool, const int*);
typedef GLXContext (*glXSwapIntervalEXTPtr)(Display*, GLXDrawable, const int);
typedef GLXContext (*glXSwapIntervalMESAPtr)(const int);

static glXCreateContextAttribsARBPtr glXCreateContextAttribsARB = 0;
static glXSwapIntervalEXTPtr glXSwapIntervalEXT = 0;
static glXSwapIntervalMESAPtr glXSwapIntervalMESA = 0;

#define jni_linux_context(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_GLXContext_##fun
#define jni_linux_platform(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_LinuxGLPlatform_##fun
