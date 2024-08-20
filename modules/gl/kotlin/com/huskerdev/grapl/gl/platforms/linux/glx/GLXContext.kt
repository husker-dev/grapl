package com.huskerdev.grapl.gl.platforms.linux.glx

import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLProfile

class GLXContext(
    val display: Long,
    val drawable: Long,
    context: Long,
    majorVersion: Int,
    minorVersion: Int,
    profile: GLProfile,
    debug: Boolean
): GLContext(context, majorVersion, minorVersion, profile, debug) {

    companion object {
        @JvmStatic private external fun nInitFunctions()
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @JvmStatic private external fun nCreateContextForWindow(display: Long, window: Long, isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(display: Long, window: Long, context: Long): Boolean
        @JvmStatic private external fun nDeleteContext(display: Long, context: Long)
        @JvmStatic private external fun nBindDebugCallback(callbackClass: Class<GLContext>)

        fun create(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            fromJNI(nCreateContext(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug))

        fun createForWindow(display: Long, window: Long, profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
            fromJNI(nCreateContextForWindow(display, window, profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug))

        fun fromCurrent() =
            fromJNI(nGetCurrentContext())

        fun clear() =
            nSetCurrentContext(0L, 0L, 0L)

        private fun fromJNI(array: LongArray) = GLXContext(
            array[0], array[1], array[2],
            array[3].toInt(), array[4].toInt(),
            if(array[5].toInt() == 1) GLProfile.CORE else GLProfile.COMPATIBILITY,
            array[6].toInt() == 1
        )

        init {
            nInitFunctions()
        }
    }

    override fun makeCurrent() = nSetCurrentContext(display, drawable, handle)

    override fun delete() = nDeleteContext(display, handle)

    override fun bindDebugCallback() = nBindDebugCallback(GLContext::class.java)
}