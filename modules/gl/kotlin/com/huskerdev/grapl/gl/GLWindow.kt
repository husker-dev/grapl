package com.huskerdev.grapl.gl

import com.huskerdev.grapl.core.window.Window

class GLWindow(
    shareWith: Long = 0L,
    profile: GLProfile = GLProfile.CORE,
    majorVersion: Int = -1,
    minorVersion: Int = -1,
    debug: Boolean = false
): Window(
    GLPlatform.current.createGLWindowPeer(profile, shareWith, majorVersion, minorVersion, debug),
) {

    val context by (peer as GLWindowPeer)::context

    constructor(profile: GLProfile): this(0L, profile)

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
        ) = GLPlatform.current.createGLWindowPeer(profile, shareWith.handle, majorVersion, minorVersion, debug)

        @JvmOverloads
        @JvmStatic
        fun create(
            shareWith: Long = 0L,
            profile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
            debug: Boolean
        ) = GLPlatform.current.createGLWindowPeer(profile, shareWith, majorVersion, minorVersion, debug)
    }

    var swapInterval = 0
        set(value) {
            GLPlatform.current.setSwapInterval(this, value)
            field = value
        }

    fun swapBuffers() = GLPlatform.current.swapBuffers(this)
}