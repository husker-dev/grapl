package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.util.c_str
import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.MacDisplayPeer
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
        @JvmStatic private external fun nSetMinSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetMaxSize(windowPtr: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetCursor(windowPtr: Long, index: Int)
        @JvmStatic private external fun nGetScreen(windowPtr: Long): Long

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

    override fun setPositionImpl(x: Int, y: Int) =
        nSetPosition(windowPtr, (x / display.dpi).toInt(), (y / display.dpi).toInt())
    override fun setSizeImpl(width: Int, height: Int) =
        nSetSize(windowPtr, (width / display.dpi).toInt(), (height / display.dpi).toInt())
    override fun setMinSizeImpl(width: Int, height: Int) = nSetMinSize(windowPtr, width, height)
    override fun setMaxSizeImpl(width: Int, height: Int) = nSetMaxSize(windowPtr, width, height)
    override fun setTitleImpl(title: String) = nSetTitle(windowPtr, title.c_str)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(windowPtr, visible)

    override var cursor: Cursor
        get() = super.cursor
        set(value) {
            super.cursor = value
            // Mapped with nSetCursor in window.mm
            nSetCursor(windowPtr, value.ordinal)
        }

    override val display: Display
        get() = Display(MacDisplayPeer(nGetScreen(windowPtr)))

    fun onCloseCallback(){
        shouldClose = true
    }

    fun onResizeCallback(width: Double, height: Double){
        _size = Size(width.toInt(), height.toInt())
    }

    fun onMoveCallback(x: Double, y: Double){
        _position = Size(x.toInt(), y.toInt())
    }
}