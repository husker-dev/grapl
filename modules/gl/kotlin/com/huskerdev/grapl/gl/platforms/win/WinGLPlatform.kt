package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.gl.*

class WinGLPlatform: GLPlatform() {

    companion object {
        @JvmStatic private external fun nCreateGLWindow(
            isCore: Boolean,
            shareWith: Long,
            majorVersion: Int,
            minorVersion: Int,
            debug: Boolean
        ): LongArray

        @JvmStatic private external fun nSwapBuffers(dc: Long)
        @JvmStatic private external fun nSetSwapInterval(hwnd: Long, value: Int)
    }

    override fun supportsDebug() = true

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
        WGLContext.create(profile, shareWith, majorVersion, minorVersion, debug)

    override fun createFromCurrentContext() =
        WGLContext.fromCurrent()

    override fun clearContext() =
        WGLContext.clearContext()

    override fun createGLWindowPeer(
        profile: GLProfile,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ) = BackgroundMessageHandler.invokeWaiting {
        nCreateGLWindow(profile == GLProfile.CORE, shareWith, majorVersion, minorVersion, debug).run {
            WinGLWindowPeer(
                this[0],
                WGLContext(this[1], this[2], this[3].toInt(), this[4].toInt(), this[5].toInt() == 1),
            ).apply { this.onCreated() }
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.context as WGLContext).dc)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval(window.peer.handle, value)
}