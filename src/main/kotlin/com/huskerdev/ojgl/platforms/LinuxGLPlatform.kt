package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class LinuxGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(display: Long, window: Long, context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long): LongArray
        @JvmStatic private external fun nDeleteContext(display: Long, context: Long)
    }

    override fun createContext(profile: Boolean, shareWith: Long) =
        nCreateContext(profile, shareWith).run { GLXContext(this[0], this[1], this[2]) }

    override fun createFromCurrent() =
        nGetCurrentContext().run { GLXContext(this[0], this[1], this[2]) }

    override fun makeCurrent(context: GLContext?) =
        nSetCurrentContext(
            (context as GLXContext?)?.display ?: 0L,
            context?.window ?: 0L,
            context?.handle ?: 0L
        )

    override fun delete(context: GLContext) =
        nDeleteContext((context as GLXContext).display, context.handle)
}

class GLXContext(
    val display: Long,
    val window: Long,
    context: Long,
): GLContext(context)