package com.huskerdev.grapl.gl.platforms.linux

import com.huskerdev.grapl.core.window.impl.X11WindowPeer
import com.huskerdev.grapl.gl.GLWindowPeer

class GLXWindowPeer(handle: Long): X11WindowPeer(handle), GLWindowPeer {
    override lateinit var context: GLXContext
}