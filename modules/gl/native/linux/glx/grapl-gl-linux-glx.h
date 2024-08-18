#include "../grapl-gl-linux.h"

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#define __gl_h_
#include <GL/glx.h>

typedef GLXContext (*glXCreateContextAttribsARBPtr)(Display*, GLXFBConfig, GLXContext, Bool, const int*);
typedef GLXContext (*glXSwapIntervalEXTPtr)(Display*, GLXDrawable, const int);
typedef GLXContext (*glXSwapIntervalMESAPtr)(const int);

static glXCreateContextAttribsARBPtr glXCreateContextAttribsARB = NULL;
static glXSwapIntervalEXTPtr glXSwapIntervalEXT = NULL;
static glXSwapIntervalMESAPtr glXSwapIntervalMESA = NULL;
static glXSwapIntervalMESAPtr glXSwapIntervalSGI = NULL;


#define jni_linux_glx_context(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_glx_GLXContext_##fun
#define jni_linux_glx_manager(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_glx_GLXManager_##fun