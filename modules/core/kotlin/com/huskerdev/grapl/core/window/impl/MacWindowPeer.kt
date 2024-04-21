package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.util.c_str
import com.huskerdev.grapl.core.Cursor
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
        @JvmStatic private external fun nSetPosition(windowPtr: Long, x: Int, y: Int)
        @JvmStatic private external fun nSetSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetCursor(windowPtr: Long, index: Int)

        fun create() = MacWindowPeer()

        init {
            nInitApplication()
        }
    }

    val windowPtr = nCreateWindow(this)
    private var shouldClose = false

    override fun destroy() = nCloseWindow(windowPtr)
    override fun peekMessages() = nPeekMessage()
    override fun shouldClose() = shouldClose

    override fun setPositionImpl(x: Int, y: Int) = nSetPosition(windowPtr, x, y)
    override fun setSizeImpl(width: Int, height: Int) = nSetSize(windowPtr, width, height)
    override fun setTitleImpl(title: String) = nSetTitle(windowPtr, title.c_str)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(windowPtr, visible)

    override var cursor: Cursor
        get() = super.cursor
        set(value) {
            super.cursor = value
            // Mapped with nSetCursor in window.mm
            nSetCursor(windowPtr, when (value) {
                Cursor.DEFAULT              -> 0
                Cursor.HAND                 -> 1
                Cursor.TEXT                 -> 2
                Cursor.WAIT                 -> 3
                Cursor.PROGRESS             -> 4
                Cursor.CROSSHAIR            -> 5
                Cursor.NOT_ALLOWED          -> 6
                Cursor.HELP                 -> 7
                Cursor.SIZE_HORIZONTAL      -> 8
                Cursor.SIZE_VERTICAL        -> 9
                Cursor.SIZE_NE              -> 10
                Cursor.SIZE_SE              -> 11
                Cursor.MOVE                 -> 12
                Cursor.SCROLL_VERTICAL      -> 13
                Cursor.SCROLL_HORIZONTAL    -> 14
                Cursor.SCROLL_ALL           -> 15
                Cursor.SCROLL_UP            -> 16
                Cursor.SCROLL_DOWN          -> 17
                Cursor.SCROLL_LEFT          -> 18
                Cursor.SCROLL_RIGHT         -> 19
                Cursor.SCROLL_TOP_LEFT      -> 20
                Cursor.SCROLL_TOP_RIGHT     -> 21
                Cursor.SCROLL_BOTTOM_LEFT   -> 22
                Cursor.SCROLL_BOTTOM_RIGHT  -> 23
            })
        }

    fun onCloseCallback(){
        shouldClose = true
    }

    fun onResizeCallback(width: Double, height: Double){
        _size = Pair(width.toInt(), height.toInt())
    }

    fun onMoveCallback(x: Double, y: Double){
        _position = Pair(x.toInt(), y.toInt())
    }
}