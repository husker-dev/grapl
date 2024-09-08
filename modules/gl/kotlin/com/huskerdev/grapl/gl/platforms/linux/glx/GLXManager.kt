package com.huskerdev.grapl.gl.platforms.linux.glx

import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.core.window.impl.X11WindowPeer
import com.huskerdev.grapl.gl.GLManager
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class GLXManager: GLManager() {
    companion object {
        @JvmStatic private external fun nSwapBuffers(display: Long, window: Long)
        @JvmStatic private external fun nSetSwapInterval(display: Long, window: Long, context: Long, value: Int)
    }

    override fun supportsDebug() = true

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
        GLXContext.create(profile, shareWith, majorVersion, minorVersion, debug)

    override fun createFromCurrentContext() =
        GLXContext.fromCurrent()

    override fun clearContext() =
        GLXContext.clear()

    override fun createGLWindowPeer(
        profile: GLProfile,
        pixelFormat: GLPixelFormat,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ) = BackgroundMessageHandler.invokeWaiting {
        GLXWindowPeer(profile, pixelFormat, shareWith, majorVersion, minorVersion, debug).apply {
            onCreated()
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.peer as X11WindowPeer).display, window.peer.handle)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval((window.peer as X11WindowPeer).display, window.peer.handle, window.context.handle, value)
}