package com.huskerdev.ojgl.gl.platforms

import com.huskerdev.ojgl.gl.GLContext
import com.huskerdev.ojgl.gl.GLPlatform

class LinuxGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(display: Long, window: Long, context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int): LongArray
        @JvmStatic private external fun nDeleteContext(display: Long, context: Long)
    }

    override fun createContext(profile: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        nCreateContext(profile, shareWith, majorVersion, minorVersion).run { GLXContext(this[0], this[1], this[2], this[3].toInt(), this[4].toInt()) }

    override fun createFromCurrent() =
        nGetCurrentContext().run { GLXContext(this[0], this[1], this[2], this[3].toInt(), this[4].toInt()) }

    override fun makeCurrent(context: GLContext?) =
        nSetCurrentContext(
            (context as GLXContext?)?.display ?: 0L,
            context?.window ?: 0L,
            context?.handle ?: 0L
        )

    override fun delete(context: GLContext) =
        nDeleteContext((context as GLXContext).display, context.handle)
}

class GLXContext(
    val display: Long,
    val window: Long,
    context: Long,
    majorVersion: Int,
    minorVersion: Int
): GLContext(context, majorVersion, minorVersion)