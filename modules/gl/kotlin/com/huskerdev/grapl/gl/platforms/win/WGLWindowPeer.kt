package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.core.window.impl.WinWindowPeer
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindowPeer

class WGLWindowPeer(
    profile: GLProfile,
    pixelFormat: GLPixelFormat,
    shareWith: Long,
    majorVersion: Int,
    minorVersion: Int,
    debug: Boolean
): WinWindowPeer(), GLWindowPeer {

    override var context = WGLContext.createForWindow(handle, profile, pixelFormat, shareWith, majorVersion, minorVersion, debug)

    override fun dispatchUpdate() {
        context.makeCurrent()
        super.dispatchUpdate()
    }
}