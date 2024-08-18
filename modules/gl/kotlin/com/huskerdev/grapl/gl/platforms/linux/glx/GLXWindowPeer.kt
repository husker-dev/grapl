package com.huskerdev.grapl.gl.platforms.linux.glx

import com.huskerdev.grapl.core.window.impl.X11WindowPeer
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindowPeer

class GLXWindowPeer(
    profile: GLProfile,
    shareWith: Long,
    majorVersion: Int,
    minorVersion: Int,
    debug: Boolean
): X11WindowPeer(), GLWindowPeer {

    override val context = GLXContext.createForWindow(
        display, handle,
        profile, shareWith, majorVersion, minorVersion, debug
    )

    override fun dispatchUpdate() {
        context.makeCurrent()
        super.dispatchUpdate()
    }
}