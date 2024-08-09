package com.huskerdev.grapl.gl.platforms.linux

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLProfile

class GLXContext(
    val display: Long,
    val window: Long,
    context: Long,
    majorVersion: Int,
    minorVersion: Int,
    debug: Boolean
): GLContext(context, majorVersion, minorVersion, debug) {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(display: Long, window: Long, context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @JvmStatic private external fun nDeleteContext(display: Long, context: Long)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug).run {
                GLXContext(this[0], this[1], this[2], this[3].toInt(), this[4].toInt(), this[5].toInt() == 1)
            }

        fun fromCurrent() =
            nGetCurrentContext().run {
                GLXContext(this[0], this[1], this[2], this[3].toInt(), this[4].toInt(), this[5].toInt() == 1)
            }

        fun clearContext() =
            nSetCurrentContext(0L, 0L, 0L)
    }

    override fun makeCurrent() = nSetCurrentContext(display, window, handle)

    override fun delete() = nDeleteContext(display, handle)

    override fun bindDebugCallback() {
        TODO("Not yet implemented")
    }
}