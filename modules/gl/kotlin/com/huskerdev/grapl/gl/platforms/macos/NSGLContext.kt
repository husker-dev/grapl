package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.core.window.impl.MacWindowPeer
import com.huskerdev.grapl.gl.GLProfile

class NSGLContext(
    val nsglHandle: Long,
    cgl: CGLContext
): CGLContext(cgl.handle, cgl.majorVersion, cgl.minorVersion) {

    companion object {
        @JvmStatic private external fun nAttachToWindow(windowPtr: Long, contextPtr: Long): Long
        @JvmStatic private external fun nFlushBuffer(nsgl: Long)
        @JvmStatic private external fun nReleaseContext(nsgl: Long)

        @JvmStatic private external fun nSetSwapInterval(nsgl: Long, swapInterval: Int)

        fun createAttached(peer: MacWindowPeer, profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
            create(profile, shareWith, majorVersion, minorVersion).run {
                NSGLContext(nAttachToWindow(peer.handle, this.handle), this)
            }

    }

    override fun delete() =
        nReleaseContext(nsglHandle)

    fun flushBuffer() =
        nFlushBuffer(nsglHandle)

    fun setSwapInterval(swapInterval: Int) =
        nSetSwapInterval(nsglHandle, swapInterval)
}