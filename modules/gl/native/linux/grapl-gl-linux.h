#include "../shared/grapl-gl.h"

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
static glXSwapIntervalMESAPtr glXSwapIntervalSGI = 0;

#define jni_linux_context(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_GLXContext_##fun
#define jni_linux_platform(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_LinuxGLPlatform_##fun


static void checkBasicFunctions(){
    if(glXCreateContextAttribsARB == 0){
        glXCreateContextAttribsARB = (glXCreateContextAttribsARBPtr)glXGetProcAddressARB((GLubyte*) "glXCreateContextAttribsARB");
        glGetIntegerv = (glGetIntegervPtr)glXGetProcAddressARB((GLubyte*) "glGetIntegerv");

        glDebugMessageCallbackARB = (glDebugMessageCallbackARBPtr)glXGetProcAddressARB((GLubyte*) "glDebugMessageCallbackARB");

        glXSwapIntervalEXT = (glXSwapIntervalEXTPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalEXT");
        glXSwapIntervalMESA = (glXSwapIntervalMESAPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalMESA");
        glXSwapIntervalSGI = (glXSwapIntervalMESAPtr)glXGetProcAddress((GLubyte const*)"glXSwapIntervalSGI");
    }
}