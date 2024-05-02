package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.util.c_str
import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.MacDisplayPeer
import com.huskerdev.grapl.core.window.WindowDisplayState
import com.huskerdev.grapl.core.window.WindowPeer
import java.nio.ByteBuffer

class MacWindowPeer : WindowPeer() {

    companion object {
        @JvmStatic private external fun nInitApplication()
        @JvmStatic private external fun nCreateWindow(callbackClass: Any): Long

        @JvmStatic private external fun nPeekMessage()
        @JvmStatic private external fun nCloseWindow(windowPtr: Long)
        @JvmStatic private external fun nSetVisible(windowPtr: Long, value: Boolean)
        @JvmStatic private external fun nSetTitle(windowPtr: Long, title: ByteBuffer)
        @JvmStatic private external fun nSetPosition(windowPtr: Long, x: Double, y: Double)
        @JvmStatic private external fun nSetSize(windowPtr: Long, width: Double, height: Double)
        @JvmStatic private external fun nSetMinSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetMaxSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetCursor(windowPtr: Long, index: Int)
        @JvmStatic private external fun nGetScreen(windowPtr: Long): Long

        fun create() = MacWindowPeer()

        init {
            nInitApplication()
        }
    }

    init {
        handle = nCreateWindow(MacWindowCallback())
    }

    override fun destroy() = nCloseWindow(handle)
    override fun peekMessages() = nPeekMessage()

    override fun setTitleImpl(title: String) = nSetTitle(handle, title.c_str)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(handle, visible)
    override fun setCursorImpl(cursor: Cursor) = nSetCursor(handle, cursor.ordinal) // Mapped with nSetCursor in window.mm
    override fun setSizeImpl(size: Size) = nSetSize(handle, size.width / dpi, size.height / dpi)
    override fun setMinSizeImpl(size: Size) = nSetMinSize(handle, size.width.toInt(), size.height.toInt())
    override fun setMaxSizeImpl(size: Size) = nSetMaxSize(handle, size.width.toInt(), size.height.toInt())
    override fun setPositionImpl(position: Position) = nSetPosition(handle, position.x / dpi, position.y / dpi)

    override fun setDisplayStateImpl(state: WindowDisplayState) {
        TODO("Not yet implemented")
    }


    override val display: Display
        get() = Display(MacDisplayPeer(nGetScreen(handle)))

    inner class MacWindowCallback: DefaultWindowCallback(){
        fun onResizeCallback(width: Double, height: Double) =
            super.onResizeCallback((width * dpi).toInt(), (height * dpi).toInt())

        fun onMoveCallback(x: Double, y: Double) =
            super.onMoveCallback((x * dpi).toInt(), (y * dpi).toInt())

        fun onPointerMoveCallback(pointerId: Int, x: Double, y: Double) =
            super.onPointerMoveCallback(pointerId, (x * dpi).toInt(), (y * dpi).toInt())

        fun onPointerDownCallback(pointerId: Int, x: Double, y: Double, button: Int) =
            super.onPointerDownCallback(pointerId, (x * dpi).toInt(), (y * dpi).toInt(), button)

        fun onPointerUpCallback(pointerId: Int, x: Double, y: Double, button: Int) =
            super.onPointerUpCallback(pointerId, (x * dpi).toInt(), (y * dpi).toInt(), button)

        fun onPointerEnterCallback(pointerId: Int, x: Double, y: Double) =
            super.onPointerEnterCallback(pointerId, (x * dpi).toInt(), (y * dpi).toInt())

        fun onPointerLeaveCallback(pointerId: Int, x: Double, y: Double) =
            super.onPointerLeaveCallback(pointerId, (x * dpi).toInt(), (y * dpi).toInt())
    }
}