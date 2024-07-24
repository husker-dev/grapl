package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
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

        @JvmStatic private external fun nSwapBuffers(dc: Long)
        @JvmStatic private external fun nSetSwapInterval(hwnd: Long, value: Int)
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
    ) = BackgroundMessageHandler.invokeWaiting {
        nCreateGLWindow(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion).run {
            WinGLWindowPeer(
                this[0],
                WGLContext(this[1], this[2], this[3].toInt(), this[4].toInt()),
            ).apply { this.onCreated() }
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.context as WGLContext).dc)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval(window.peer.handle, value)
}