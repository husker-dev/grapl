#include <grapl.h>
#include <iostream>

typedef int GLint;
typedef int GLsizei;
typedef float GLfloat;
typedef void GLvoid;
typedef unsigned int GLuint;
typedef unsigned int GLenum;
typedef unsigned char GLubyte;
typedef unsigned int GLbitfield;
typedef unsigned char GLboolean;
typedef signed long int GLintptr;
typedef signed long int GLsizeiptr;

#define GL_FALSE          0
#define GL_TRUE           1
#define GL_MAJOR_VERSION  0x821B
#define GL_MINOR_VERSION  0x821C

typedef void (*glGetIntegervPtr)(GLenum pname, GLint* data);
typedef void (*glFlushPtr)();

static glGetIntegervPtr glGetIntegerv;
static glFlushPtr glFlush;