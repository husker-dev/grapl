package com.huskerdev.grapl.gl

import com.huskerdev.grapl.core.window.Window

class GLWindow(
    profile: GLProfile         = GLProfile.CORE,
    pixelFormat: GLPixelFormat = GLPixelFormat.DEFAULT,
    shareWith: Long            = 0L,
    majorVersion: Int          = -1,
    minorVersion: Int          = -1,
    debug: Boolean             = false
): Window(
    GLManager.current.createGLWindowPeer(profile, pixelFormat, shareWith, majorVersion, minorVersion, debug),
) {

    val context: GLContext
        get() = (peer as GLWindowPeer).context

    var swapInterval = 0
        set(value) {
            GLManager.current.setSwapInterval(this, value)
            field = value
        }

    fun swapBuffers() = GLManager.current.swapBuffers(this)
}