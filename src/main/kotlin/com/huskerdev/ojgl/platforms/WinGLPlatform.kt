package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class WinGLPlatform: GLPlatform() {
    companion object {
        @JvmStatic private external fun nGetCurrentContext(): LongArray
        @JvmStatic private external fun nSetCurrentContext(dc: Long, rc: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long): LongArray

        fun createContext(profile: Boolean, shareWith: Long) = nCreateContext(profile, shareWith).run { WGLContext(this[0], this[1]) }
        fun fromCurrent() = nGetCurrentContext().run { WGLContext(this[0], this[1]) }
        fun clearCurrent() = nSetCurrentContext(0L, 0L)
        fun makeCurrent(context: WGLContext) = nSetCurrentContext(context.dc, context.context)
    }
}

class WGLContext(
    context: Long,
    val dc: Long
): GLContext(context) {
    override fun makeCurrent() = WinGLPlatform.makeCurrent(this)
}