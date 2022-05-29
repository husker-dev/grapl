package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class WinGLPlatform: GLPlatform() {
    companion object {
        @JvmStatic internal external fun nGetCurrentContext(): LongArray
        @JvmStatic internal external fun nSetCurrentContext(dc: Long, rc: Long): Boolean
        @JvmStatic internal external fun nCreateContext(isCore: Boolean, shareWith: Long): LongArray

        fun createContext(profile: Boolean, shareWith: Long) = nCreateContext(profile, shareWith).run { WGLContext(this[0], this[1]) }
        fun fromCurrent() = nGetCurrentContext().run { WGLContext(this[0], this[1]) }
        fun clearCurrent() = nSetCurrentContext(0L, 0L)
    }
}

class WGLContext(
    context: Long,
    val dc: Long
): GLContext(context) {
    override fun makeCurrent() = WinGLPlatform.nSetCurrentContext(dc, context)
}