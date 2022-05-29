package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class LinuxGLPlatform: GLPlatform() {
    companion object {
        @JvmStatic internal external fun nGetCurrentContext(): LongArray
        @JvmStatic internal external fun nSetCurrentContext(display: Long, window: Long, context: Long): Boolean
        @JvmStatic internal external fun nCreateContext(isCore: Boolean, shareWith: Long): LongArray

        fun createContext(profile: Boolean, shareWith: Long) = nCreateContext(profile, shareWith).run { GLXContext(this[0], this[1], this[2]) }
        fun fromCurrent() = nGetCurrentContext().run { GLXContext(this[0], this[1], this[2]) }
        fun clearCurrent() = nSetCurrentContext(0L, 0L, 0L)
    }
}

class GLXContext(
    val display: Long,
    val window: Long,
    context: Long,
): GLContext(context) {
    override fun makeCurrent() = LinuxGLPlatform.nSetCurrentContext(display, window, context)
}