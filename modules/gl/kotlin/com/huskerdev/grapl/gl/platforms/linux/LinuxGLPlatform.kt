package com.huskerdev.grapl.gl.platforms.linux

import com.huskerdev.grapl.core.window.WindowPeer
import com.huskerdev.grapl.gl.GLPlatform
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindow

class LinuxGLPlatform: GLPlatform() {
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
    ): WindowPeer {
        TODO("Not yet implemented")
    }

    override fun swapBuffers(window: GLWindow) {
        TODO("Not yet implemented")
    }

    override fun setSwapInterval(window: GLWindow, value: Int) {
        TODO("Not yet implemented")
    }
}