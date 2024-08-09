package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLProfile

open class CGLContext(
    context: Long,
    majorVersion: Int,
    minorVersion: Int,
    debug: Boolean
): GLContext(context, majorVersion, minorVersion, debug) {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @JvmStatic private external fun nDeleteContext(context: Long)
        @JvmStatic private external fun nSetBackingSize(context: Long, width: Int, height: Int)

        @JvmStatic private external fun nLockContext(context: Long)
        @JvmStatic private external fun nUnlockContext(context: Long)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug).run{
                CGLContext(this[0], this[1].toInt(), this[2].toInt(), this[3].toInt() == 1)
            }

        fun fromCurrent() =
            nGetCurrentContext().run {
                CGLContext(this[0], this[1].toInt(), this[2].toInt(), this[3].toInt() == 1)
            }

        fun clearContext() =
            nSetCurrentContext(0L)
    }

    fun setBackingSize(width: Int, height: Int) = nSetBackingSize(handle, width, height)

    override fun makeCurrent() = nSetCurrentContext(handle)

    override fun delete() = nDeleteContext(handle)

    override fun bindDebugCallback() = Unit // Unsupported

    fun lock() = nLockContext(handle)

    fun unlock() = nUnlockContext(handle)
}