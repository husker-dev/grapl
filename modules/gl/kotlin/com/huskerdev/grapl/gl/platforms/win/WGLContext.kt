package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLProfile

class WGLContext(
    context: Long,
    val dc: Long,
    majorVersion: Int,
    minorVersion: Int
): GLContext(context, majorVersion, minorVersion){
    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(dc: Long, rc: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int): LongArray
        @JvmStatic private external fun nDeleteContext(rc: Long)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
            nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion)
                .run { WGLContext(this[0], this[1], this[2].toInt(), this[3].toInt()) }

        fun fromCurrent() =
            nGetCurrentContext().run { WGLContext(this[0], this[1], this[2].toInt(), this[3].toInt()) }

        fun clearContext() =
            nSetCurrentContext(0L, 0L)
    }

    override fun makeCurrent() = nSetCurrentContext(dc, handle)

    override fun delete() = nDeleteContext(handle)
}