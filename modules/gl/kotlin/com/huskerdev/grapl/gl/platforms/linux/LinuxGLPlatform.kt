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
        @JvmStatic private external fun nCreateWindow(display: Long, isCore: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean): LongArray
        @JvmStatic private external fun nSwapBuffers(display: Long, window: Long)
        @JvmStatic private external fun nSetSwapInterval(display: Long, window: Long, context: Long, value: Int)
    }

    override fun supportsDebug() = true

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
        GLXContext.create(profile, shareWith, majorVersion, minorVersion, debug)

    override fun createFromCurrentContext() =
        GLXContext.fromCurrent()

    override fun clearContext() =
        GLXContext.clearContext()

    override fun createGLWindowPeer(
        profile: GLProfile,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ) = BackgroundMessageHandler.invokeWaiting {
        nCreateWindow(
            (LinuxPlatform.windowingSystem as X11).display,
            profile == GLProfile.CORE,
            shareWith,
            majorVersion,
            minorVersion,
            debug
        ).run {
            GLXWindowPeer(this[0]).apply {
                context = GLXContext(xDisplay, handle, this@run[1], this@run[2].toInt(), this@run[3].toInt(), this@run[4].toInt() == 1)
                onCreated()
            }
        }
    }

    override fun swapBuffers(window: GLWindow) =
        nSwapBuffers((window.peer as X11WindowPeer).xDisplay, window.peer.handle)

    override fun setSwapInterval(window: GLWindow, value: Int) =
        nSetSwapInterval((window.peer as X11WindowPeer).xDisplay, window.peer.handle, window.context.handle, value)
}