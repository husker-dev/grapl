package com.husker.ojgl

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLMin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OJGLTest {

    @Test
    fun createInstance() {
        assertEquals(GLContext.create().makeCurrent(), true)
    }

    @Test
    fun createShared() {
        val first = GLContext.create()
        val second = GLContext.create(first)
        assertEquals(second.makeCurrent(), true)
    }

    @Test
    fun fromCurrent() {
        GLContext.create().makeCurrent()
        val second = GLContext.current()
        assertEquals(second.makeCurrent(), true)
    }

    @Test
    fun testFunctions() {
        GLContext.create().makeCurrent()

        GLMin.init()
        val fbo = GLMin.glGenFramebuffers()
        val fbo1 = GLMin.glGenFramebuffers()
        GLMin.glDeleteFramebuffers(fbo)
        GLMin.glDeleteFramebuffers(fbo1)

        assertEquals(fbo != fbo1, true)
        GLMin.glFinish()
    }

}