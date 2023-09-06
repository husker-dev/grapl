package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class WinGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(dc: Long, rc: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long): LongArray
        @JvmStatic private external fun nDeleteContext(rc: Long)
    }

    override fun createContext(profile: Boolean, shareWith: Long) =
        nCreateContext(profile, shareWith).run { WGLContext(this[0], this[1]) }

    override fun createFromCurrent() =
        nGetCurrentContext().run { WGLContext(this[0], this[1]) }

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
    val dc: Long
): GLContext(context)