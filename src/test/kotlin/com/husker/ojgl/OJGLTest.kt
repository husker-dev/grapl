package com.husker.ojgl

import com.huskerdev.ojgl.GLContext
import org.junit.jupiter.api.Test

class OJGLTest {

    @Test
    fun createInstance() {
        GLContext.createNew().makeCurrent()
    }

    @Test
    fun createShared() {
        val first = GLContext.createNew()
        val second = GLContext.createNew(first)
        second.makeCurrent()
    }

    @Test
    fun fromCurrent() {
        GLContext.createNew().makeCurrent()
        val second = GLContext.fromCurrent()
        second.makeCurrent()
    }

}