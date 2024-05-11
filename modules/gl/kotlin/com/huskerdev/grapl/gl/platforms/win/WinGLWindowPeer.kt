package com.huskerdev.grapl.gl.platforms.win

import com.huskerdev.grapl.core.window.impl.WinWindowPeer
import com.huskerdev.grapl.gl.GLWindowPeer

class WinGLWindowPeer(
    hwnd: Long,
    override val context: WGLContext
): WinWindowPeer(hwnd), GLWindowPeer