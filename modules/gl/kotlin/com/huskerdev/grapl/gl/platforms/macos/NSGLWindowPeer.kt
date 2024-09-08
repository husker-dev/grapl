package com.huskerdev.grapl.gl.platforms.macos

import com.huskerdev.grapl.core.window.impl.NSWindowPeer
import com.huskerdev.grapl.gl.GLPixelFormat
import com.huskerdev.grapl.gl.GLProfile
import com.huskerdev.grapl.gl.GLWindowPeer

class NSGLWindowPeer(
    profile: GLProfile,
    pixelFormat: GLPixelFormat,
    shareWith: Long,
    majorVersion: Int,
    minorVersion: Int,
    debug: Boolean
): NSWindowPeer(), GLWindowPeer {
    override val context = NSGLContext.createAttached(this, profile, pixelFormat, shareWith, majorVersion, minorVersion, debug)

    init {
        sizeProperty.listeners += {
            context.setBackingSize(
                sizeProperty.value.width.toInt(),
                sizeProperty.value.height.toInt())
        }
    }

    override fun dispatchUpdate() {
        context.makeCurrent()
        context.lock()
        super.dispatchUpdate()
        context.unlock()
    }
}