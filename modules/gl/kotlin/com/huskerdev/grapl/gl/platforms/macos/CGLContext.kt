package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile

open class CGLContext(
    context: Long,
    majorVersion: Int,
    minorVersion: Int,
    profile: GLProfile,
    debug: Boolean
): GLContext(context, majorVersion, minorVersion, profile, debug) {

    companion object {
        @JvmStatic private external fun nInitFunctions()
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @JvmStatic private external fun nCreateContext(
            isCore: Boolean,
            msaa: Int,
            doubleBuffering: Boolean,
            redBits: Int, greenBits: Int, blueBits: Int, alphaBits: Int, depthBits: Int, stencilBits: Int,
            transparency: Boolean,
            shareWith: Long,
            majorVersion: Int,
            minorVersion: Int, debug: Boolean
        ): LongArray
        @JvmStatic private external fun nDeleteContext(context: Long)
        @JvmStatic private external fun nSetBackingSize(context: Long, width: Int, height: Int)

        @JvmStatic private external fun nLockContext(context: Long)
        @JvmStatic private external fun nUnlockContext(context: Long)

        fun create(profile: GLProfile, pixelFormat: GLPixelFormat, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            fromJNI(nCreateContext(
                profile == GLProfile.CORE,
                pixelFormat.msaa,
                pixelFormat.doubleBuffering,
                pixelFormat.redBits, pixelFormat.greenBits, pixelFormat.blueBits, pixelFormat.alphaBits, pixelFormat.depthBits, pixelFormat.stencilBits,
                pixelFormat.transparency,
                shareWith,
                majorVersion, minorVersion,
                debug
            ))

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

    override fun bindDebugCallback() = Unit // Unsupported

    fun lock() = nLockContext(handle)

    fun unlock() = nUnlockContext(handle)
}