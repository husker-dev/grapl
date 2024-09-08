package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.gl.GLManager
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class MacGLManager: GLManager() {

    override fun supportsDebug() = false

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int, debug: Boolean) =
        CGLContext.create(profile, shareWith, majorVersion, minorVersion, debug)

    override fun createFromCurrentContext() =
        CGLContext.fromCurrent()

    override fun clearContext() =
        CGLContext.clear()

    override fun createGLWindowPeer(
        profile: GLProfile,
        pixelFormat: GLPixelFormat,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ) = NSGLWindowPeer(profile, pixelFormat, shareWith, majorVersion, minorVersion, debug).apply {
        this.onCreated()
    }

    override fun swapBuffers(window: GLWindow) =
        (window.context as NSGLContext).flushBuffer()

    override fun setSwapInterval(window: GLWindow, value: Int) =
        (window.context as NSGLContext).setSwapInterval(value)

}