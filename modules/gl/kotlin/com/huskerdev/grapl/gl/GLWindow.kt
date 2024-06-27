package com.huskerdev.grapl.gl

import com.huskerdev.grapl.core.util.observer
import com.huskerdev.grapl.core.window.Window

class GLWindow(
    shareWith: Long = 0L,
    profile: GLProfile = GLProfile.CORE,
    majorVersion: Int = -1,
    minorVersion: Int = -1
): Window(
    GLPlatform.current.createGLWindowPeer(profile, shareWith, majorVersion, minorVersion),
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
        ) = GLPlatform.current.createGLWindowPeer(profile, shareWith.handle, majorVersion, minorVersion)

        @JvmOverloads
        @JvmStatic
        fun create(
            shareWith: Long = 0L,
            profile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = GLPlatform.current.createGLWindowPeer(profile, shareWith, majorVersion, minorVersion)
    }

    var swapInterval = 0
        set(value) {
            GLPlatform.current.setSwapInterval(this, value)
            field = value
        }

    fun swapBuffers() = GLPlatform.current.swapBuffers(this)
}