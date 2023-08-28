#define UNICODE
#include <jni.h>


#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
#include <windows.h>
#include <gl/gl.h>
#elif defined(__linux__)
#include <GL/gl.h>
#elif defined(__APPLE__)
#include <OpenGL/gl.h>
#include <mach-o/dyld.h>
#include <stdlib.h>
#include <string.h>
#endif

typedef void (*PFNGLDELETEFRAMEBUFFERSPROC)(GLsizei n, const GLuint* framebuffers);
typedef void (*PFNGLDELETERENDERBUFFERSPROC)(GLsizei n, const GLuint* renderbuffers);
typedef void (*PFNGLDELETETEXTURESPROC)(GLsizei n, const GLuint* textures);
typedef void (*PFNGLGENFRAMEBUFFERSPROC)(GLsizei n, GLuint* framebuffers);
typedef void (*PFNGLGENRENDERBUFFERSPROC)(GLsizei n, GLuint* renderbuffers);
typedef void (*PFNGLGENTEXTURESPROC)(GLsizei n, GLuint* textures);
typedef void (*PFNGLBINDFRAMEBUFFERPROC)(GLenum target, GLuint framebuffer);
typedef void (*PFNGLBINDRENDERBUFFERPROC)(GLenum target, GLuint renderbuffer);
typedef void (*PFNGLBINDTEXTURESPROC)(GLuint first, GLsizei count, const GLuint* textures);
typedef void (*PFNGLFRAMEBUFFERTEXTURE2DPROC)(GLenum target, GLenum attachment, GLenum textarget, GLuint texture, GLint level);
typedef void (*PFNGLRENDERBUFFERSTORAGEPROC)(GLenum target, GLenum internalformat, GLsizei width, GLsizei height);
typedef void (*PFNGLFRAMEBUFFERRENDERBUFFERPROC)(GLenum target, GLenum attachment, GLenum renderbuffertarget, GLuint renderbuffer);
typedef void (*PFNGLREADPIXELSPROC)(GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, void* pixels);
typedef void (*PFNGLTEXIMAGE2DPROC)(GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLint border, GLenum format, GLenum type, const void* pixels);
typedef void (*PFNGLTEXPARAMETERIPROC)(GLenum target, GLenum pname, GLint param);
typedef void (*PFNGLVIEWPORTPROC)(GLint x, GLint y, GLsizei width, GLsizei height);
typedef void (*PFNGLFINISHPROC)(void);

PFNGLVIEWPORTPROC a_glViewport;
PFNGLTEXPARAMETERIPROC a_glTexParameteri;
PFNGLTEXIMAGE2DPROC a_glTexImage2D;
PFNGLREADPIXELSPROC a_glReadPixels;
PFNGLFRAMEBUFFERRENDERBUFFERPROC a_glFramebufferRenderbuffer;
PFNGLRENDERBUFFERSTORAGEPROC a_glRenderbufferStorage;
PFNGLFRAMEBUFFERTEXTURE2DPROC a_glFramebufferTexture2D;
PFNGLBINDTEXTURESPROC a_glBindTextures;
PFNGLBINDRENDERBUFFERPROC a_glBindRenderbuffer;
PFNGLBINDFRAMEBUFFERPROC a_glBindFramebuffer;
PFNGLGENTEXTURESPROC a_glGenTextures;
PFNGLGENRENDERBUFFERSPROC a_glGenRenderbuffers;
PFNGLGENFRAMEBUFFERSPROC a_glGenFramebuffers;
PFNGLDELETETEXTURESPROC a_glDeleteTextures;
PFNGLDELETERENDERBUFFERSPROC a_glDeleteRenderbuffers;
PFNGLDELETEFRAMEBUFFERSPROC a_glDeleteFramebuffers;
PFNGLFINISHPROC a_glFinish;

extern "C" {

// GLMin
void* a_GetProcAddress(const char* name) {
#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
    return (void*)wglGetProcAddress(name);
#elif defined(__linux__)
    return (void*)glXGetProcAddressARB((GLubyte*)name);
#elif defined(__APPLE__)
    NSSymbol symbol;
    char* symbolName;
    symbolName = malloc(strlen(name) + 2);
    strcpy(symbolName + 1, name);
    symbolName[0] = '_';
    symbol = NULL;
    if (NSIsSymbolNameDefined(symbolName))
        symbol = NSLookupAndBindSymbol(symbolName);
    free(symbolName);
    return symbol ? NSAddressOfSymbol(symbol) : NULL;
#endif
}

JNIEXPORT void JNICALL Java_com_huskerdev_ojgl_GLMin_init(JNIEnv* env, jobject) {
    a_glViewport = (PFNGLVIEWPORTPROC)a_GetProcAddress("glViewport");
    a_glTexParameteri = (PFNGLTEXPARAMETERIPROC)a_GetProcAddress("glTexParameteri");
    a_glTexImage2D = (PFNGLTEXIMAGE2DPROC)a_GetProcAddress("glTexImage2D");
    a_glReadPixels = (PFNGLREADPIXELSPROC)a_GetProcAddress("glReadPixels");
    a_glFramebufferRenderbuffer = (PFNGLFRAMEBUFFERRENDERBUFFERPROC)a_GetProcAddress("glFramebufferRenderbuffer");
    a_glRenderbufferStorage = (PFNGLRENDERBUFFERSTORAGEPROC)a_GetProcAddress("glRenderbufferStorage");
    a_glFramebufferTexture2D = (PFNGLFRAMEBUFFERTEXTURE2DPROC)a_GetProcAddress("glFramebufferTexture2D");
    a_glBindTextures = (PFNGLBINDTEXTURESPROC)a_GetProcAddress("glBindTextures");
    a_glBindRenderbuffer = (PFNGLBINDRENDERBUFFERPROC)a_GetProcAddress("glBindRenderbuffer");
    a_glBindFramebuffer = (PFNGLBINDFRAMEBUFFERPROC)a_GetProcAddress("glBindFramebuffer");
    a_glGenTextures = (PFNGLGENTEXTURESPROC)a_GetProcAddress("glGenTextures");
    a_glGenRenderbuffers = (PFNGLGENRENDERBUFFERSPROC)a_GetProcAddress("glGenRenderbuffers");
    a_glGenFramebuffers = (PFNGLGENFRAMEBUFFERSPROC)a_GetProcAddress("glGenFramebuffers");
    a_glDeleteTextures = (PFNGLDELETETEXTURESPROC)a_GetProcAddress("glDeleteTextures");
    a_glDeleteRenderbuffers = (PFNGLDELETERENDERBUFFERSPROC)a_GetProcAddress("glDeleteRenderbuffers");
    a_glDeleteFramebuffers = (PFNGLDELETEFRAMEBUFFERSPROC)a_GetProcAddress("glDeleteFramebuffers");
    a_glFinish = (PFNGLFINISHPROC)a_GetProcAddress("glFinish");
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