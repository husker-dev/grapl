package com.huskerdev.grapl.gl.platforms.linux.glx

import com.huskerdev.grapl.core.window.impl.X11WindowPeer
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindowPeer

class GLXWindowPeer(
    profile: GLProfile,
    pixelFormat: GLPixelFormat,
    shareWith: Long,
    majorVersion: Int,
    minorVersion: Int,
    debug: Boolean
): X11WindowPeer(), GLWindowPeer {

    override val context = GLXContext.createForWindow(
        display, handle,
        profile, pixelFormat, shareWith, majorVersion, minorVersion, debug
    )

    override fun dispatchUpdate() {
        context.makeCurrent()
        super.dispatchUpdate()
    }
}