package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.util.c_str
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.MacDisplayPeer
import com.huskerdev.grapl.core.window.WindowDisplayState
import com.huskerdev.grapl.core.window.WindowPeer
import com.huskerdev.grapl.core.window.WindowStyle
import java.nio.ByteBuffer

open class MacWindowPeer : WindowPeer() {

    companion object {
        @JvmStatic private external fun nInitApplication()
        @JvmStatic private external fun nCreateWindow(callbackClass: Any): Long

        @JvmStatic private external fun nCloseWindow(windowPtr: Long)
        @JvmStatic private external fun nSetVisible(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetTitle(windowPtr: Long, title: ByteBuffer)
        @JvmStatic private external fun nSetPosition(windowPtr: Long, x: Double, y: Double)
        @JvmStatic private external fun nSetFocused(windowPtr: Long)
        @JvmStatic private external fun nSetSize(windowPtr: Long, width: Double, height: Double)
        @JvmStatic private external fun nSetMinSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetMaxSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetCursor(windowPtr: Long, index: Int)
        @JvmStatic private external fun nGetScreen(windowPtr: Long): Long
        @JvmStatic private external fun nSetMinimizable(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetMaximizable(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetClosable(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetResizable(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nGetDpi(windowPtr: Long): Float
        @JvmStatic private external fun nSetEnabled(windowPtr: Long, enabled: Boolean)
        @JvmStatic private external fun nSetStyle(windowPtr: Long, style: Int)

        fun create() = MacWindowPeer()

        init {
            nInitApplication()
        }
    }

    init {
        handle = nCreateWindow(DefaultWindowCallback())
    }

    override fun destroy() = nCloseWindow(handle)
    override fun requestFocus() = nSetFocused(handle)

    override fun setTitleImpl(title: String) = nSetTitle(handle, title.c_str)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(handle, visible)
    override fun setCursorImpl(cursor: Cursor) = nSetCursor(handle, cursor.toNative())
    override fun setSizeImpl(size: Size) = nSetSize(handle, size.width, size.height)
    override fun setMinSizeImpl(size: Size) = nSetMinSize(handle, size.width.toInt(), size.height.toInt())
    override fun setMaxSizeImpl(size: Size) = nSetMaxSize(handle, size.width.toInt(), size.height.toInt())
    override fun setPositionImpl(position: Position) = nSetPosition(handle, position.x, position.y)

    override fun setDisplayStateImpl(state: WindowDisplayState) {
        TODO("Not yet implemented")
    }

    override fun setMinimizableImpl(value: Boolean) = nSetMinimizable(handle, value)
    override fun setMaximizableImpl(value: Boolean) = nSetMaximizable(handle, value)
    override fun setClosable(value: Boolean) = nSetClosable(handle, value)
    override fun setResizable(value: Boolean) = nSetResizable(handle, value)

    override fun getDpiImpl() = nGetDpi(handle).toDouble()
    override fun getDisplayImpl() = Display(MacDisplayPeer(nGetScreen(handle)))
    override fun setEnabledImpl(enabled: Boolean) = nSetEnabled(handle, enabled)

    override fun setStyle(style: WindowStyle) = nSetStyle(handle, style.toNative())

    private fun WindowStyle.toNative() = when (this) {
        WindowStyle.DEFAULT     -> 0
        WindowStyle.UNDECORATED -> 1
        WindowStyle.NO_TITLEBAR -> 2
    }

    private fun Cursor.toNative() = when (this) {
        Cursor.DEFAULT -> 0
        Cursor.HAND -> 1
        Cursor.TEXT -> 2
        Cursor.WAIT -> 3
        Cursor.PROGRESS -> 4
        Cursor.CROSSHAIR -> 5
        Cursor.NOT_ALLOWED -> 6
        Cursor.HELP -> 7
        Cursor.SIZE_HORIZONTAL_DOUBLE -> 8
        Cursor.SIZE_VERTICAL_DOUBLE -> 9
        Cursor.SIZE_W -> 10
        Cursor.SIZE_E -> 11
        Cursor.SIZE_N -> 12
        Cursor.SIZE_S -> 13
        Cursor.SIZE_NE -> 14
        Cursor.SIZE_SE -> 15
        Cursor.MOVE -> 16
        Cursor.SCROLL_ALL -> 17
        Cursor.SCROLL_UP -> 18
        Cursor.SCROLL_DOWN -> 19
        Cursor.SCROLL_LEFT -> 20
        Cursor.SCROLL_RIGHT -> 21
        Cursor.SCROLL_TOP_LEFT -> 22
        Cursor.SCROLL_TOP_RIGHT -> 23
        Cursor.SCROLL_BOTTOM_LEFT -> 24
        Cursor.SCROLL_BOTTOM_RIGHT -> 25
    }
}