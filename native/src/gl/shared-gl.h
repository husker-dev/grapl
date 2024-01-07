typedef int GLint;
typedef unsigned int GLenum;

#define GL_FALSE                                    0
#define GL_TRUE                                     1
#define GL_MAJOR_VERSION                            0x821B
#define GL_MINOR_VERSION                            0x821C

typedef void (*glGetIntegervPtr)(GLenum pname, GLint* data);

// OpenGL lib variable
#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
#include <windows.h>
static HMODULE libGL;

#elif defined(__linux__)
#include <dlfcn.h>
static void* libGL;

typedef void* (* glXGetProcAddressPtr)(const char*);
static glXGetProcAddressPtr a_gladGetProcAddress;

#elif defined(__APPLE__)
#include <dlfcn.h>
static void* libGL;
#endif

// OpenGL functions getter
void* getProcAddress(const char* name) {
#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
    if(libGL == NULL){
        libGL = LoadLibraryW(L"opengl32.dll");
        a_wglGetProcAddress = (wglGetProcAddressPtr)GetProcAddress(libGL, "wglGetProcAddress");
    }
    void* procAddr = a_wglGetProcAddress(name);
    if(procAddr == NULL)
        procAddr = GetProcAddress(libGL, name);
    return procAddr;

#elif defined(__linux__)
    if(libGL == NULL){
        static const char *NAMES[] = {"libGL.so.1", "libGL.so"};
        for(int i = 0; i < 2; i++)
            if((libGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
                break;
        a_gladGetProcAddress = (glXGetProcAddressPtr)dlsym(libGL, "glXGetProcAddressARB");
    }
    void* procAddr = (void*)a_gladGetProcAddress(name);
    if(procAddr == NULL)
        procAddr = dlsym(libGL, name);
    return procAddr;

#elif defined(__APPLE__)
    if(libGL == NULL){
        static const char *NAMES[] = {
            "../Frameworks/OpenGL.framework/OpenGL",
            "/Library/Frameworks/OpenGL.framework/OpenGL",
            "/System/Library/Frameworks/OpenGL.framework/OpenGL",
            "/System/Library/Frameworks/OpenGL.framework/Versions/Current/OpenGL"
        };
        for(int i = 0; i < 4; i++)
            if((libGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
                break;
    }
    return dlsym(libGL, name);
#endif
}