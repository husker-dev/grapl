package com.husker.ojgl

import com.huskerdev.ojgl.GLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OJGLTest {

    @Test
    fun createInstance() {
        assertEquals(GLContext.createNew().makeCurrent(), true)
    }

    @Test
    fun createShared() {
        val first = GLContext.createNew()
        val second = GLContext.createNew(first)
        assertEquals(second.makeCurrent(), true)
    }

    @Test
    fun fromCurrent() {
        GLContext.createNew().makeCurrent()
        val second = GLContext.fromCurrent()
        assertEquals(second.makeCurrent(), true)
    }

}