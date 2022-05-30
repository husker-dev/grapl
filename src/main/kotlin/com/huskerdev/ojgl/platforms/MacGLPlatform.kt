package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class MacGLPlatform: GLPlatform() {
    companion object {
        @JvmStatic private external fun nGetCurrentContext(): Long
        @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long): Long

        fun createContext(profile: Boolean, shareWith: Long) = CGLContext(nCreateContext(profile, shareWith))
        fun fromCurrent() = CGLContext(nGetCurrentContext())
        fun clearCurrent() = nSetCurrentContext(0L)
        fun makeCurrent(context: CGLContext) = nSetCurrentContext(context.context)
    }
}

class CGLContext(
    context: Long
): GLContext(context) {
    override fun makeCurrent() = MacGLPlatform.makeCurrent(this)
}