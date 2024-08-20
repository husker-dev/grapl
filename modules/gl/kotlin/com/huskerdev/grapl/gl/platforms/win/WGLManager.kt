package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.gl.*

class WGLManager: GLManager() {

    companion object {
        @JvmStatic private external fun nSwapBuffers(dc: Long)
        @JvmStatic private external fun nSetSwapInterval(hwnd: Long, value: Int)
    }

    override fun supportsDebug() = true

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
        WGLContext.create(profile, shareWith, majorVersion, minorVersion, debug)

    override fun createFromCurrentContext() =
        WGLContext.fromCurrent()

    override fun clearContext() =
        WGLContext.clear()

    override fun createGLWindowPeer(
        profile: GLProfile,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ) = BackgroundMessageHandler.invokeWaiting {
        WGLWindowPeer(profile, shareWith, majorVersion, minorVersion, debug).apply {
            this.onCreated()
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.context as WGLContext).dc)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval(window.peer.handle, value)
}