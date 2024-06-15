package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.platform.impl.LinuxPlatform
import com.huskerdev.grapl.core.platform.impl.X11
import com.huskerdev.grapl.core.window.WindowDisplayState
import com.huskerdev.grapl.core.window.WindowPeer

class X11WindowPeer: WindowPeer() {
    companion object {
        @JvmStatic private external fun nCreateWindow(display: Long): Long
    }

    private val xDisplay = (LinuxPlatform.windowingSystem as X11).display

    init {
        handle = nCreateWindow(xDisplay)
    }

    override val display: Display
        get() = TODO("Not yet implemented")

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun setTitleImpl(title: String) {
        TODO("Not yet implemented")
    }

    override fun setVisibleImpl(visible: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setCursorImpl(cursor: Cursor) {
        TODO("Not yet implemented")
    }

    override fun setSizeImpl(size: Size) {
        TODO("Not yet implemented")
    }

    override fun setMinSizeImpl(size: Size) {
        TODO("Not yet implemented")
    }

    override fun setMaxSizeImpl(size: Size) {
        TODO("Not yet implemented")
    }

    override fun setPositionImpl(position: Position) {
        TODO("Not yet implemented")
    }

    override fun setDisplayStateImpl(state: WindowDisplayState) {
        TODO("Not yet implemented")
    }

    override fun setMinimizableImpl(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setMaximizableImpl(value: Boolean) {
        TODO("Not yet implemented")
    }
}