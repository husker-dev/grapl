package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLProfile

open class CGLContext(
    context: Long,
    majorVersion: Int,
    minorVersion: Int
): GLContext(context, majorVersion, minorVersion) {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int): LongArray
        @JvmStatic private external fun nDeleteContext(context: Long)
        @JvmStatic private external fun nSetBackingSize(context: Long, width: Int, height: Int)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
            nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion).run{ CGLContext(this[0], this[1].toInt(), this[2].toInt()) }

        fun fromCurrent() =
            nGetCurrentContext().run { CGLContext(this[0], this[1].toInt(), this[2].toInt()) }

        fun clearContext() =
            nSetCurrentContext(0L)
    }

    fun setBackingSize(width: Int, height: Int) = nSetBackingSize(handle, width, height)

    override fun makeCurrent() = nSetCurrentContext(handle)

    override fun delete() = nDeleteContext(handle)
}