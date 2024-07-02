package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.util.c_str
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.MacDisplayPeer
import com.huskerdev.grapl.core.window.WindowDisplayState
import com.huskerdev.grapl.core.window.WindowPeer
import java.nio.ByteBuffer

open class MacWindowPeer : WindowPeer() {

    companion object {
        @JvmStatic private external fun nInitApplication()
        @JvmStatic private external fun nCreateWindow(callbackClass: Any): Long

        @JvmStatic private external fun nCloseWindow(windowPtr: Long)
        @JvmStatic private external fun nSetVisible(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetTitle(windowPtr: Long, title: ByteBuffer)
        @JvmStatic private external fun nSetPosition(windowPtr: Long, x: Double, y: Double)
        @JvmStatic private external fun nSetSize(windowPtr: Long, width: Double, height: Double)
        @JvmStatic private external fun nSetMinSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetMaxSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetCursor(windowPtr: Long, index: Int)
        @JvmStatic private external fun nGetScreen(windowPtr: Long): Long
        @JvmStatic private external fun nSetMinimizable(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetMaximizable(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nGetDpi(windowPtr: Long): Float

        fun create() = MacWindowPeer()

        init {
            nInitApplication()
        }
    }

    init {
        handle = nCreateWindow(DefaultWindowCallback())
    }

    override fun destroy() = nCloseWindow(handle)

    override fun setTitleImpl(title: String) = nSetTitle(handle, title.c_str)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(handle, visible)
    override fun setCursorImpl(cursor: Cursor) = nSetCursor(handle, cursor.ordinal) // Mapped with nSetCursor in window.mm
    override fun setSizeImpl(size: Size) = nSetSize(handle, size.width / dpiProperty.value, size.height / dpiProperty.value)
    override fun setMinSizeImpl(size: Size) = nSetMinSize(handle, size.width.toInt(), size.height.toInt())
    override fun setMaxSizeImpl(size: Size) = nSetMaxSize(handle, size.width.toInt(), size.height.toInt())
    override fun setPositionImpl(position: Position) = nSetPosition(handle, position.x / dpiProperty.value, position.y / dpiProperty.value)

    override fun setDisplayStateImpl(state: WindowDisplayState) {
        TODO("Not yet implemented")
    }

    override fun setMinimizableImpl(value: Boolean) = nSetMinimizable(handle, value)
    override fun setMaximizableImpl(value: Boolean) = nSetMaximizable(handle, value)
    override fun getDpiImpl() = nGetDpi(handle).toDouble()

    override val display: Display
        get() = Display(MacDisplayPeer(nGetScreen(handle)))
}