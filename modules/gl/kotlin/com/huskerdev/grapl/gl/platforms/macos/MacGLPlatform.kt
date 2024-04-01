package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.gl.GLPlatform
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class MacGLPlatform: GLPlatform() {

    override fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int) =
        CGLContext.create(profile, shareWith, majorVersion, minorVersion)

    override fun createFromCurrentContext() =
        CGLContext.fromCurrent()

    override fun clearContext() =
        CGLContext.clearContext()

    override fun createWindow(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int): GLWindow {
        TODO("Not yet implemented")
    }

    override fun swapBuffers(window: GLWindow) {
        TODO("Not yet implemented")
    }

    override fun setSwapInterval(window: GLWindow, value: Int) {
        TODO("Not yet implemented")
    }

}