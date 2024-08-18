package com.huskerdev.grapl.gl

import com.huskerdev.grapl.core.window.Window

class GLWindow(
    shareWith: Long = 0L,
    profile: GLProfile = GLProfile.CORE,
    majorVersion: Int = -1,
    minorVersion: Int = -1,
    debug: Boolean = false
): Window(
    GLManager.current.createGLWindowPeer(profile, shareWith, majorVersion, minorVersion, debug),
) {

    val context by (peer as GLWindowPeer)::context

    // TODO: Implement ScaledFullscreen via shader

    companion object {
        @JvmOverloads
        @JvmStatic
        fun create(
            shareWith: GLContext,
            profile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
            debug: Boolean
        ) = GLManager.current.createGLWindowPeer(profile, shareWith.handle, majorVersion, minorVersion, debug)

        @JvmOverloads
        @JvmStatic
        fun create(
            shareWith: Long = 0L,
            profile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
            debug: Boolean
        ) = GLManager.current.createGLWindowPeer(profile, shareWith, majorVersion, minorVersion, debug)
    }

    var swapInterval = 0
        set(value) {
            GLManager.current.setSwapInterval(this, value)
            field = value
        }

    fun swapBuffers() = GLManager.current.swapBuffers(this)
}