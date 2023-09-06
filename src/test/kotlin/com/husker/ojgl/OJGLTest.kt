package com.husker.ojgl

import com.huskerdev.ojgl.GLContext
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

}