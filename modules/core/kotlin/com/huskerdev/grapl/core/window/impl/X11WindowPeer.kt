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
import java.nio.ByteBuffer

open class X11WindowPeer: WindowPeer {
    companion object {
        @JvmStatic private external fun nCreateWindow(display: Long): Long
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


    constructor(): super(){
        handle = nCreateWindow(xDisplay)
    }

    constructor(handle: Long): super(){
        this.handle = handle
    }

    override val display: Display?
        get() {
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

            return null
        }

    override fun destroy() = nDestroyWindow(xDisplay, handle)

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
}