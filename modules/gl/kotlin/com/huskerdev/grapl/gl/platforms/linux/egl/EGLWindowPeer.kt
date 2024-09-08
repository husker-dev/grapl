package com.huskerdev.grapl.gl.platforms.linux.egl

import com.huskerdev.grapl.core.window.impl.X11WindowPeer
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindowPeer

class EGLWindowPeer {

    class X11(
        profile: GLProfile,
        pixelFormat: GLPixelFormat,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ): X11WindowPeer(), GLWindowPeer{
        override var context: GLContext = EGLContext.createForWindow(
            display, handle,
            profile, pixelFormat, shareWith, majorVersion, minorVersion, debug
        )
    }
}