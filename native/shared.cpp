#include <jni.h>


#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
#include <windows.h>
#include <gl/gl.h>
static HMODULE libGL;

#elif defined(__linux__)
#include <dlfcn.h>
#include <GL/gl.h>
#include <GL/glx.h>
static void* libGL;

#elif defined(__APPLE__)
#include <dlfcn.h>
#include <OpenGL/gl.h>
static void* libGL;

#endif

typedef void (*GLDELETEFRAMEBUFFERSPROC)(GLsizei n, const GLuint* framebuffers);
typedef void (*GLDELETERENDERBUFFERSPROC)(GLsizei n, const GLuint* renderbuffers);
typedef void (*GLDELETETEXTURESPROC)(GLsizei n, const GLuint* textures);
typedef void (*GLGENFRAMEBUFFERSPROC)(GLsizei n, GLuint* framebuffers);
typedef void (*GLGENRENDERBUFFERSPROC)(GLsizei n, GLuint* renderbuffers);
typedef void (*GLGENTEXTURESPROC)(GLsizei n, GLuint* textures);
typedef void (*GLBINDFRAMEBUFFERPROC)(GLenum target, GLuint framebuffer);
typedef void (*GLBINDRENDERBUFFERPROC)(GLenum target, GLuint renderbuffer);
typedef void (*GLBINDTEXTURESPROC)(GLuint first, GLsizei count, const GLuint* textures);
typedef void (*GLFRAMEBUFFERTEXTURE2DPROC)(GLenum target, GLenum attachment, GLenum textarget, GLuint texture, GLint level);
typedef void (*GLRENDERBUFFERSTORAGEPROC)(GLenum target, GLenum internalformat, GLsizei width, GLsizei height);
typedef void (*GLFRAMEBUFFERRENDERBUFFERPROC)(GLenum target, GLenum attachment, GLenum renderbuffertarget, GLuint renderbuffer);
typedef void (*GLREADPIXELSPROC)(GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, void* pixels);
typedef void (*GLTEXIMAGE2DPROC)(GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLint border, GLenum format, GLenum type, const void* pixels);
typedef void (*GLTEXPARAMETERIPROC)(GLenum target, GLenum pname, GLint param);
typedef void (*GLVIEWPORTPROC)(GLint x, GLint y, GLsizei width, GLsizei height);
typedef void (*GLFINISHPROC)(void);

GLVIEWPORTPROC a_glViewport;
GLTEXPARAMETERIPROC a_glTexParameteri;
GLTEXIMAGE2DPROC a_glTexImage2D;
GLREADPIXELSPROC a_glReadPixels;
GLFRAMEBUFFERRENDERBUFFERPROC a_glFramebufferRenderbuffer;
GLRENDERBUFFERSTORAGEPROC a_glRenderbufferStorage;
GLFRAMEBUFFERTEXTURE2DPROC a_glFramebufferTexture2D;
GLBINDTEXTURESPROC a_glBindTextures;
GLBINDRENDERBUFFERPROC a_glBindRenderbuffer;
GLBINDFRAMEBUFFERPROC a_glBindFramebuffer;
GLGENTEXTURESPROC a_glGenTextures;
GLGENRENDERBUFFERSPROC a_glGenRenderbuffers;
GLGENFRAMEBUFFERSPROC a_glGenFramebuffers;
GLDELETETEXTURESPROC a_glDeleteTextures;
GLDELETERENDERBUFFERSPROC a_glDeleteRenderbuffers;
GLDELETEFRAMEBUFFERSPROC a_glDeleteFramebuffers;
GLFINISHPROC a_glFinish;

extern "C" {

// GLMin
void* a_GetProcAddress(const char* name) {
#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
    if(libGL == NULL) 
        libGL = LoadLibraryW(L"opengl32.dll");
    void* procAddr = wglGetProcAddress(name);
    if(procAddr == NULL)
        procAddr = GetProcAddress(libGL, name);
    return procAddr;

#elif defined(__linux__)
    if(libGL == NULL){
        static const char *NAMES[] = {"libGL.so.1", "libGL.so"};
        for(int i = 0; i < 2; i++)
            if((libGL = dlopen(NAMES[i], RTLD_NOW | RTLD_GLOBAL)) != NULL)
                break;
    }
    void* procAddr = (void*)glXGetProcAddressARB((GLubyte*)name);
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

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_init(JNIEnv* env, jobject) {
    a_glViewport = (GLVIEWPORTPROC)a_GetProcAddress("glViewport");
    a_glTexParameteri = (GLTEXPARAMETERIPROC)a_GetProcAddress("glTexParameteri");
    a_glTexImage2D = (GLTEXIMAGE2DPROC)a_GetProcAddress("glTexImage2D");
    a_glReadPixels = (GLREADPIXELSPROC)a_GetProcAddress("glReadPixels");
    a_glFramebufferRenderbuffer = (GLFRAMEBUFFERRENDERBUFFERPROC)a_GetProcAddress("glFramebufferRenderbuffer");
    a_glRenderbufferStorage = (GLRENDERBUFFERSTORAGEPROC)a_GetProcAddress("glRenderbufferStorage");
    a_glFramebufferTexture2D = (GLFRAMEBUFFERTEXTURE2DPROC)a_GetProcAddress("glFramebufferTexture2D");
    a_glBindTextures = (GLBINDTEXTURESPROC)a_GetProcAddress("glBindTextures");
    a_glBindRenderbuffer = (GLBINDRENDERBUFFERPROC)a_GetProcAddress("glBindRenderbuffer");
    a_glBindFramebuffer = (GLBINDFRAMEBUFFERPROC)a_GetProcAddress("glBindFramebuffer");
    a_glGenTextures = (GLGENTEXTURESPROC)a_GetProcAddress("glGenTextures");
    a_glGenRenderbuffers = (GLGENRENDERBUFFERSPROC)a_GetProcAddress("glGenRenderbuffers");
    a_glGenFramebuffers = (GLGENFRAMEBUFFERSPROC)a_GetProcAddress("glGenFramebuffers");
    a_glDeleteTextures = (GLDELETETEXTURESPROC)a_GetProcAddress("glDeleteTextures");
    a_glDeleteRenderbuffers = (GLDELETERENDERBUFFERSPROC)a_GetProcAddress("glDeleteRenderbuffers");
    a_glDeleteFramebuffers = (GLDELETEFRAMEBUFFERSPROC)a_GetProcAddress("glDeleteFramebuffers");
    a_glFinish = (GLFINISHPROC)a_GetProcAddress("glFinish");
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glDeleteFramebuffers(JNIEnv* env, jobject, jint fbo) {
    a_glDeleteFramebuffers(1, (GLuint*)&fbo);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glDeleteRenderbuffers(JNIEnv* env, jobject, jint rbo) {
    a_glDeleteRenderbuffers(1, (GLuint*)&rbo);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glDeleteTextures(JNIEnv* env, jobject, jint texture) {
    a_glDeleteTextures(1, (GLuint*)&texture);
}

JNIEXPORT jint JNICALL Java_com_huskerdev_ojgl_GLMin_glGenFramebuffers(JNIEnv* env, jobject) {
    GLuint framebuffer;
    a_glGenFramebuffers(1, &framebuffer);
    return framebuffer;
}

JNIEXPORT jint JNICALL Java_com_huskerdev_ojgl_GLMin_glGenRenderbuffers(JNIEnv* env, jobject) {
    GLuint renderbuffer;
    a_glGenRenderbuffers(1, &renderbuffer);
    return renderbuffer;
}

JNIEXPORT jint JNICALL Java_com_huskerdev_ojgl_GLMin_glGenTextures(JNIEnv* env, jobject) {
    GLuint texture;
    a_glGenTextures(1, &texture);
    return texture;
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glBindFramebuffer(JNIEnv* env, jobject, jint target, jint fbo) {
    a_glBindFramebuffer(target, fbo);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glBindRenderbuffer(JNIEnv* env, jobject, jint target, jint rbo) {
    a_glBindRenderbuffer(target, rbo);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glBindTexture(JNIEnv* env, jobject, jint target, jint texture) {
    a_glBindTextures(target, 1, (GLuint*)&texture);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glFramebufferTexture2D(JNIEnv* env, jobject, jint target, jint attachment, jint texture, jint texId, jint level) {
    a_glFramebufferTexture2D(target, attachment, texture, texId, level);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glRenderbufferStorage(JNIEnv* env, jobject, jint target, jint internalFormat, jint width, jint height) {
    a_glRenderbufferStorage(target, internalFormat, width, height);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glFramebufferRenderbuffer(JNIEnv* env, jobject, jint target, jint attachment, jint renderbufferTarget, jint renderbuffer) {
    a_glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glReadPixels(JNIEnv* env, jobject, jint x, jint y, jint width, jint height, jint format, jint type, jobject pixels) {
    jbyte* bb = (jbyte*)env->GetDirectBufferAddress(pixels);
    a_glReadPixels(x, y, width, height, format, type, &bb);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glTexImage2D(JNIEnv* env, jobject, jint target, jint level, jint internalFormat, jint width, jint height, jint border, jint format, jint type, jlong pixels) {
    a_glTexImage2D(target, level, internalFormat, width, height, border, format, type, (void*)pixels);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glTexParameteri(JNIEnv* env, jobject, jint target, jint pname, jint param) {
    a_glTexParameteri(target, pname, param);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glViewport(JNIEnv* env, jobject, jint x, jint y, jint w, jint h) {
    a_glViewport(x, y, w, h);
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_glFinish(JNIEnv* env, jobject) {
    a_glFinish();
}
}