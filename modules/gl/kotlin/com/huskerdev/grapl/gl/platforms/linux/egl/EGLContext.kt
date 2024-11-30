package com.huskerdev.grapl.gl.platforms.linux.egl

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile

class EGLContext(
    val display: Long,
    val surfaceRead: Long,
    val surfaceWrite: Long,
    context: Long,
    majorVersion: Int,
    minorVersion: Int,
    profile: GLProfile,
    debug: Boolean
): GLContext(context, majorVersion, minorVersion, profile, debug) {

    companion object {
        @Suppress("unused") @JvmStatic private external fun nInitFunctions()
        @Suppress("unused") @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @Suppress("unused") @JvmStatic private external fun nCreateContextForWindow(
            display: Long,
            surface: Long,
            isCore: Boolean,
            msaa: Int,
            doubleBuffering: Boolean,
            redBits: Int, greenBits: Int, blueBits: Int, alphaBits: Int, depthBits: Int, stencilBits: Int,
            transparency: Boolean,
            shareWith: Long,
            majorVersion: Int, minorVersion: Int,
            debug: Boolean
        ): LongArray
        @Suppress("unused") @JvmStatic private external fun nGetCurrentContext(): LongArray
        @Suppress("unused") @JvmStatic private external fun nSetCurrentContext(display: Long, surfaceRead: Long, surfaceWrite: Long, context: Long): Boolean
        @Suppress("unused") @JvmStatic private external fun nDeleteContext(display: Long, context: Long)
        @Suppress("unused") @JvmStatic private external fun nHasFunction(name: String): Boolean
        @Suppress("unused") @JvmStatic private external fun nBindDebugCallback(callbackClass: Class<GLContext>)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            fromJNI(nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug))

        fun createForWindow(display: Long, window: Long, profile: GLProfile, pixelFormat: GLPixelFormat, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            fromJNI(nCreateContextForWindow(
                display,
                window,
                profile == GLProfile.CORE,
                pixelFormat.msaa,
                pixelFormat.doubleBuffering,
                pixelFormat.redBits, pixelFormat.greenBits, pixelFormat.blueBits, pixelFormat.alphaBits, pixelFormat.depthBits, pixelFormat.stencilBits,
                pixelFormat.transparency,
                shareWith, majorVersion, minorVersion, debug
            ))

        fun fromCurrent() =
            fromJNI(nGetCurrentContext())

        fun clear() =
            nSetCurrentContext(0L, 0L, 0L, 0L)

        private fun fromJNI(array: LongArray) = EGLContext(
            array[0], array[1], array[2], array[3],
            array[4].toInt(), array[5].toInt(),
            if(array[6].toInt() == 1) GLProfile.CORE else GLProfile.COMPATIBILITY,
            array[7].toInt() == 1
        )

        init {
            nInitFunctions()
        }
    }

    override fun makeCurrent() =
        nSetCurrentContext(display, surfaceRead, surfaceWrite, handle)

    override fun delete() =
        nDeleteContext(display, handle)

    override fun hasFunction(name: String) =
        nHasFunction(name)

    override fun bindDebugCallback() =
        nBindDebugCallback(GLContext::class.java)

}