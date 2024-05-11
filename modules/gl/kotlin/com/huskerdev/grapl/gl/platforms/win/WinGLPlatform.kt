package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.gl.GLPlatform
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class WinGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nCreateGLWindow(
            isCore: Boolean,
            shareWith: Long,
            majorVersion: Int,
            minorVersion: Int
        ): LongArray

        @JvmStatic private external fun nSwapBuffers(hwnd: Long)
        @JvmStatic private external fun nSwapInterval(hwnd: Long, value: Int)
    }

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        WGLContext.create(profile, shareWith, majorVersion, minorVersion)

    override fun createFromCurrentContext() =
        WGLContext.fromCurrent()

    override fun clearContext() =
        WGLContext.clearContext()

    override fun createGLWindowPeer(
        profile: GLProfile,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int
    ) = nCreateGLWindow(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion).run {
        WinGLWindowPeer(
            this[0],
            WGLContext(this[1], this[2], this[3].toInt(), this[4].toInt()),
        )
    }

    override fun swapBuffers(window: GLWindow) = nSwapBuffers(window.peer.handle)
    override fun setSwapInterval(window: GLWindow, value: Int) = nSwapInterval(window.peer.handle, value)
}