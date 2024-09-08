package com.huskerdev.grapl.gl.platforms.linux.egl

import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.gl.GLManager
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class EGLManager: GLManager() {
    companion object {
        @JvmStatic private external fun nSwapBuffers(display: Long, window: Long)
        @JvmStatic private external fun nSetSwapInterval(display: Long, value: Int)
    }

    override fun supportsDebug() = true

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
        EGLContext.create(profile, shareWith, majorVersion, minorVersion, debug)

    override fun createFromCurrentContext() =
        EGLContext.fromCurrent()

    override fun clearContext() =
        EGLContext.clear()

    override fun createGLWindowPeer(
        profile: GLProfile,
        pixelFormat: GLPixelFormat,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ) = BackgroundMessageHandler.invokeWaiting {
        EGLWindowPeer.X11(profile, pixelFormat, shareWith, majorVersion, minorVersion, debug).apply {
            onCreated()
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.context as EGLContext).display, (window.context as EGLContext).surfaceWrite)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval((window.context as EGLContext).display, value)

}