package com.husker.ojgl

import com.huskerdev.ojgl.GLContext
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL

class OJGLTest {

    @Test
    fun createInstance() {
        GLContext.createNew().makeCurrent()

        GL.createCapabilities()
    }

    @Test
    fun createShared() {
        val first = GLContext.createNew()
        val second = GLContext.createNew(first)
        second.makeCurrent()

        GL.createCapabilities()
    }

    @Test
    fun fromCurrent() {
        GLContext.createNew().makeCurrent()
        val second = GLContext.fromCurrent()
        second.makeCurrent()

        GL.createCapabilities()
    }

}