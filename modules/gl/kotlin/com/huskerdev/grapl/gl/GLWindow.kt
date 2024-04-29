package com.huskerdev.grapl.gl

import com.huskerdev.grapl.core.util.observer
import com.huskerdev.grapl.core.window.Window
import com.huskerdev.grapl.core.window.WindowPeer

class GLWindow(
    val context: GLContext,
    peer: WindowPeer
): Window(peer) {

    // TODO: Implement ScaledFullscreen via shader

    companion object {
        @JvmOverloads
        @JvmStatic
        fun create(
            shareWith: GLContext,
            profile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = GLPlatform.current.createWindow(profile, shareWith.handle, majorVersion, minorVersion)

        @JvmOverloads
        @JvmStatic
        fun create(
            shareWith: Long = 0L,
            profile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = GLPlatform.current.createWindow(profile, shareWith, majorVersion, minorVersion)
    }

    var swapInterval by observer(1) {
        GLPlatform.current.setSwapInterval(this, it)
    }

    fun swapBuffers() = GLPlatform.current.swapBuffers(this)
}