package com.huskerdev.grapl.gl.platforms.linux

import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.core.platform.impl.LinuxPlatform
import com.huskerdev.grapl.core.platform.impl.X11
import com.huskerdev.grapl.core.window.impl.X11WindowPeer
import com.huskerdev.grapl.gl.GLPlatform
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class LinuxGLPlatform: GLPlatform() {
    companion object {
        @JvmStatic private external fun nCreateWindow(display: Long): LongArray
        @JvmStatic private external fun nSwapBuffers(display: Long, window: Long)
        @JvmStatic private external fun nSetSwapInterval(display: Long, window: Long, context: Long, value: Int)
    }

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        GLXContext.create(profile, shareWith, majorVersion, minorVersion)

    override fun createFromCurrentContext() =
        GLXContext.fromCurrent()

    override fun clearContext() =
        GLXContext.clearContext()

    override fun createGLWindowPeer(
        profile: GLProfile,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int
    ) = BackgroundMessageHandler.invokeWaiting {
        nCreateWindow((LinuxPlatform.windowingSystem as X11).display).run {
            GLXWindowPeer(this[0]).apply { context = GLXContext(xDisplay, handle, this@run[1], 0, 0) }
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.peer as X11WindowPeer).xDisplay, window.peer.handle)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval((window.peer as X11WindowPeer).xDisplay, window.peer.handle, window.context.handle, value)
}