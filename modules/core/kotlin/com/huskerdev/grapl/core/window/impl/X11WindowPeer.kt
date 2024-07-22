package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.X11DisplayPeer
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.platform.impl.LinuxPlatform
import com.huskerdev.grapl.core.platform.impl.X11
import com.huskerdev.grapl.core.util.c_str
import com.huskerdev.grapl.core.window.WindowDisplayState
import com.huskerdev.grapl.core.window.WindowPeer
import com.huskerdev.grapl.core.window.WindowStyle
import java.nio.ByteBuffer
import kotlin.math.hypot

open class X11WindowPeer(
    handle: Long = nCreateWindow((LinuxPlatform.windowingSystem as X11).display)
): WindowPeer(handle) {
    companion object {
        @JvmStatic private external fun nCreateWindow(display: Long): Long
        @JvmStatic private external fun nHookWindow(display: Long, window: Long, callback: Any)
        @JvmStatic private external fun nDestroyWindow(display: Long, window: Long)
        @JvmStatic private external fun nSetTitle(display: Long, window: Long, title: ByteBuffer)
        @JvmStatic private external fun nSetVisible(display: Long, window: Long, isVisible: Boolean)
        @JvmStatic private external fun nSetCursor(display: Long, window: Long, cursor: Int)
        @JvmStatic private external fun nSetSize(display: Long, window: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetPosition(display: Long, window: Long, x: Int, y: Int)
        @JvmStatic private external fun nUpdateMinMax(display: Long, window: Long, minWidth: Int, minHeight: Int, maxWidth: Int, maxHeight: Int)
        @JvmStatic private external fun nUpdateActions(display: Long, window: Long, minimizable: Boolean, maximizable: Boolean)
    }

    val xDisplay = (LinuxPlatform.windowingSystem as X11).display

    init {
        nHookWindow(xDisplay, handle, X11WindowCallback())
    }

    override fun destroy() = nDestroyWindow(xDisplay, handle)
    override fun requestFocus() {
        TODO("Not yet implemented")
    }

    override fun setTitleImpl(title: String) = nSetTitle(xDisplay, handle, title.c_str)

    override fun setVisibleImpl(visible: Boolean) {
        nSetVisible(xDisplay, handle, visible)
        nSetSize(xDisplay, handle, sizeProperty.value.width.toInt(), sizeProperty.value.height.toInt())
        nSetPosition(xDisplay, handle, positionProperty.value.x.toInt(), positionProperty.value.y.toInt())
    }

    override fun setCursorImpl(cursor: Cursor) = nSetCursor(xDisplay, handle, when(cursor){
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
    })

    override fun setSizeImpl(size: Size) = nSetSize(xDisplay, handle, size.width.toInt(), size.height.toInt())

    override fun setMinSizeImpl(size: Size) = nUpdateMinMax(xDisplay, handle,
        minSizeProperty.value.width.toInt(), minSizeProperty.value.height.toInt(),
        maxSizeProperty.value.width.toInt(), maxSizeProperty.value.height.toInt(),
    )

    override fun setMaxSizeImpl(size: Size) = nUpdateMinMax(xDisplay, handle,
        minSizeProperty.value.width.toInt(), minSizeProperty.value.height.toInt(),
        maxSizeProperty.value.width.toInt(), maxSizeProperty.value.height.toInt(),
    )

    override fun setPositionImpl(position: Position) = nSetPosition(xDisplay, handle, position.x.toInt(), position.y.toInt())

    override fun setDisplayStateImpl(state: WindowDisplayState) {
        TODO("Not yet implemented")
    }

    override fun setMinimizableImpl(value: Boolean) = nUpdateActions(xDisplay, handle, value, maximizable.value)

    override fun setMaximizableImpl(value: Boolean) = nUpdateActions(xDisplay, handle, minimizable.value, value)
    override fun setClosable(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setResizable(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getDpiImpl() = displayProperty.value.dpi

    override fun getDisplayImpl(): Display {
        val displays = X11DisplayPeer.list(xDisplay)

        // Searching by center point
        val center = Position(
            positionProperty.value.x + sizeProperty.value.width / 2,
            positionProperty.value.y + sizeProperty.value.height / 2
        )
        displays.firstOrNull {
            center in it.dimension
        }?.let { return Display(it) }

        // Searching by intersection
        val windowDimension = this.dimension
        displays.firstOrNull {
            it.dimension.intersect(windowDimension)
        }?.let { return Display(it) }

        // Searching by minimum distance
        val x1 = windowDimension.x
        val y1 = windowDimension.y
        val x2 = windowDimension.x + windowDimension.width
        val y2 = windowDimension.y + windowDimension.height
        return Display(displays.minBy {
            val dimension = it.dimension
            val x1b = dimension.x
            val y1b = dimension.y
            val x2b = dimension.x + dimension.width
            val y2b = dimension.y + dimension.height

            val left = x2b < x1
            val right = x1b < x2
            val bottom = y2b < y1
            val top = y1b < y2

            if(top && left)          hypot(x1 - x2b, y2 - y1b)
            else if(left && bottom)  hypot(x1 - x2b, y1 - y2b)
            else if(bottom && right) hypot(x1b - x2, y1 - y2b)
            else if(right && top)    hypot(x1b - x2, y1b - y2)
            else if(left)            x1 - x2b
            else if(right)           x2 - x1b
            else if(bottom)          y1 - y2b
            else                     y2 - y1b
        })
    }

    override fun setEnabledImpl(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setStyle(style: WindowStyle) {
        TODO("Not yet implemented")
    }

    inner class X11WindowCallback: DefaultWindowCallback(){
        private var lastDisplay = getDisplayImpl()

        override fun onCloseCallback() {
            super.onCloseCallback()
            nDestroyWindow(xDisplay, handle)
        }

        override fun onMoveCallback(x: Int, y: Int) {
            if(lastDisplay != getDisplayImpl()){
                lastDisplay = getDisplayImpl()
                onDisplayChanged()
            }
            super.onMoveCallback(x, y)
        }
    }
}