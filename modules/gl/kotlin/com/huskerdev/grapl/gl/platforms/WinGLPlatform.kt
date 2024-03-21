package com.huskerdev.grapl.gl.platforms

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLPlatform

class WinGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(dc: Long, rc: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int): LongArray
        @JvmStatic private external fun nDeleteContext(rc: Long)
    }

    override fun createContext(profile: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        nCreateContext(profile, shareWith, majorVersion, minorVersion).run { WGLContext(this[0], this[1], this[2].toInt(), this[3].toInt()) }

    override fun createFromCurrent() =
        nGetCurrentContext().run { WGLContext(this[0], this[1], this[2].toInt(), this[3].toInt()) }

    override fun makeCurrent(context: GLContext?) =
        nSetCurrentContext(
            (context as WGLContext?)?.dc ?: 0L,
            context?.handle ?: 0L
        )

    override fun delete(context: GLContext) =
        nDeleteContext(context.handle)
}

class WGLContext(
    context: Long,
    val dc: Long,
    majorVersion: Int,
    minorVersion: Int
): GLContext(context, majorVersion, minorVersion)