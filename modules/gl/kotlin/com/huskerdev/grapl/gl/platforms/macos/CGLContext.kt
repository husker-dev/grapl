package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLProfile

open class CGLContext(
    context: Long,
    majorVersion: Int,
    minorVersion: Int,
    profile: GLProfile,
    debug: Boolean
): GLContext(context, majorVersion, minorVersion, profile, debug) {

    companion object {
        @Suppress("unused") @JvmStatic private external fun nInitFunctions()
        @Suppress("unused") @JvmStatic private external fun nGetCurrentContext(): LongArray
        @Suppress("unused") @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @Suppress("unused") @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @Suppress("unused") @JvmStatic private external fun nDeleteContext(context: Long)
        @Suppress("unused") @JvmStatic private external fun nSetBackingSize(context: Long, width: Int, height: Int)
        @Suppress("unused") @JvmStatic private external fun nHasFunction(name: String): Boolean

        @Suppress("unused") @JvmStatic private external fun nLockContext(context: Long)
        @Suppress("unused") @JvmStatic private external fun nUnlockContext(context: Long)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            fromJNI(nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug))

        fun fromCurrent() =
            fromJNI(nGetCurrentContext())

        fun clear() =
            nSetCurrentContext(0L)

        private fun fromJNI(array: LongArray) = CGLContext(
            array[0],
            array[1].toInt(), array[2].toInt(),
            if(array[3].toInt() == 1) GLProfile.CORE else GLProfile.COMPATIBILITY,
            array[4].toInt() == 1
        )

        init {
            nInitFunctions()
        }
    }

    fun setBackingSize(width: Int, height: Int) = nSetBackingSize(handle, width, height)

    override fun makeCurrent() = nSetCurrentContext(handle)

    override fun delete() = nDeleteContext(handle)

    override fun hasFunction(name: String) =
        nHasFunction(name)

    override fun bindDebugCallback() = Unit // Unsupported

    fun lock() = nLockContext(handle)

    fun unlock() = nUnlockContext(handle)
}