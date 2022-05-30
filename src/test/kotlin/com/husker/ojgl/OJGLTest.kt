package com.husker.ojgl

import com.huskerdev.ojgl.GLContext
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30

class OJGLTest {

    @Test
    fun createInstance() {
        GLContext.createNew().makeCurrent()
        testGL()
    }

    @Test
    fun createShared() {
        val first = GLContext.createNew()
        val second = GLContext.createNew(first)
        second.makeCurrent()

        testGL()
    }

    @Test
    fun fromCurrent() {
        GLContext.createNew().makeCurrent()
        val second = GLContext.fromCurrent()
        second.makeCurrent()

        testGL()
    }

    private fun testGL(){
        GL.createCapabilities()
        GL30.glDeleteTextures(GL30.glGenTextures())
    }
}