#include "../gl-linux.h"

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#define __gl_h_
#include <GL/glx.h>

#include <dlfcn.h>

static void* libGL;

typedef GLXContext (*glXCreateContextAttribsARBPtr)(Display*, GLXFBConfig, GLXContext, Bool, const int*);
typedef GLXContext (*glXSwapIntervalEXTPtr)(Display*, GLXDrawable, const int);
typedef GLXContext (*glXSwapIntervalMESAPtr)(const int);

extern glXCreateContextAttribsARBPtr glXCreateContextAttribsARB;
extern glXSwapIntervalEXTPtr         glXSwapIntervalEXT;
extern glXSwapIntervalMESAPtr        glXSwapIntervalMESA;
extern glXSwapIntervalMESAPtr        glXSwapIntervalSGI;

static void* _GetProcAddress(const char* name) {
    if(libGL == NULL){
        static const char *NAMES[] = {
            "libGL-1.so",
            "libGL.so.1",
            "libGL.so"
        };
        for(int i = 0; i < 3; i++)
            if((libGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
                break;
    }
    void* handle = glXGetProcAddressARB((GLubyte*) name);
    if(handle == NULL)
        handle = glXGetProcAddress((GLubyte const*) name);
    if(handle == NULL)
        handle = dlsym(libGL, name);
    return handle;
}


#define jni_linux_glx_context(returnType, fun)  extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_glx_GLXContext_##fun
#define jni_linux_glx_manager(returnType, fun)	extern "C" JNIEXPORT returnType JNICALL Java_com_huskerdev_grapl_gl_platforms_linux_glx_GLXManager_##fun