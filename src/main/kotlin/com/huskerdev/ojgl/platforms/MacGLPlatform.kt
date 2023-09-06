package com.huskerdev.ojgl.platforms

import com.huskerdev.ojgl.GLContext
import com.huskerdev.ojgl.GLPlatform

class MacGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nGetCurrentContext(): Long
        @JvmStatic private external fun nSetCurrentContext(context: Long): Boolean
        @JvmStatic private external fun nCreateContext(isCore: Boolean, shareWith: Long): Long
        @JvmStatic private external fun nDeleteContext(context: Long)
    }

    override fun createContext(profile: Boolean, shareWith: Long) =
        CGLContext(nCreateContext(profile, shareWith))

    override fun createFromCurrent() =
        CGLContext(nGetCurrentContext())

    override fun makeCurrent(context: GLContext?) =
        nSetCurrentContext(context?.handle ?: 0L)

    override fun delete(context: GLContext) =
        nDeleteContext(context.handle)
}

class CGLContext(context: Long): GLContext(context)