package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.core.window.impl.MacWindowPeer
import com.huskerdev.grapl.gl.GLPlatform
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class MacGLPlatform: GLPlatform() {

    companion object {
        fun createGLWindow(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
            MacWindowPeer.create().run {
                GLWindow(
                    NSGLContext.createAttached(this, profile, shareWith, majorVersion, minorVersion),
                    this
                )
            }
    }

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        CGLContext.create(profile, shareWith, majorVersion, minorVersion)

    override fun createFromCurrentContext() =
        CGLContext.fromCurrent()

    override fun clearContext() =
        CGLContext.clearContext()

    override fun createWindow(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        createGLWindow(profile, shareWith, majorVersion, minorVersion)

    override fun swapBuffers(window: GLWindow) =
        (window.context as NSGLContext).flushBuffer()

    override fun setSwapInterval(window: GLWindow, value: Int) {

    }

}