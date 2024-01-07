package com.huskerdev.ojgl.gl.platforms

import com.huskerdev.ojgl.gl.GLContext
import com.huskerdev.ojgl.gl.GLPlatform

class MacGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int): LongArray
        @JvmStatic private external fun nDeleteContext(context: Long)
    }

    override fun createContext(profile: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        nCreateContext(profile, shareWith, majorVersion, minorVersion).run{ CGLContext(this[0], this[1].toInt(), this[2].toInt()) }

    override fun createFromCurrent() =
        nGetCurrentContext().run { CGLContext(this[0], this[1].toInt(), this[2].toInt()) }

    override fun makeCurrent(context: GLContext?) =
        nSetCurrentContext(context?.handle ?: 0L)

    override fun delete(context: GLContext) =
        nDeleteContext(context.handle)
}

class CGLContext(
    context: Long,
    majorVersion: Int,
    minorVersion: Int
): GLContext(context, majorVersion, minorVersion)