package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.window.Cursor
import com.huskerdev.grapl.core.window.WindowPeer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class WinWindowPeer(
    val hwnd: Long,
): WindowPeer() {

    companion object {
        @JvmStatic private external fun nHookWindow(hwnd: Long, callbackClass: Any)
        @JvmStatic private external fun nPeekMessage(hwnd: Long)
        @JvmStatic private external fun nPostQuit(hwnd: Long)   // Pushes WM_QUIT

        @JvmStatic private external fun nSetVisible(hwnd: Long, value: Boolean)
        @JvmStatic private external fun nSetPosition(hwnd: Long, x: Int, y: Int)
        @JvmStatic private external fun nSetSize(hwnd: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetTitle(hwnd: Long, title: ByteBuffer)

        private val String.c_wstr: ByteBuffer
            get() {
                val bytes = toByteArray(StandardCharsets.UTF_16LE)
                val cBytes = ByteArray(bytes.size + 2)
                System.arraycopy(bytes, 0, cBytes, 0, bytes.size)
                return ByteBuffer.allocateDirect(cBytes.size).order(ByteOrder.nativeOrder()).put(cBytes)
            }
    }

    private var shouldClose = false

    init {
        nHookWindow(hwnd, this)
    }

    override fun runEventLoop(loopCallback: () -> Unit) {
        while (!shouldClose) {
            loopCallback()
            nPeekMessage(hwnd)
        }
    }

    override fun destroy() = nPostQuit(hwnd)
    override fun setPositionImpl(x: Int, y: Int) = nSetPosition(hwnd, x, y)
    override fun setSizeImpl(width: Int, height: Int) = nSetSize(hwnd, width, height)
    override fun setTitleImpl(title: String) = nSetTitle(hwnd, title.c_wstr)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(hwnd, visible)


    fun onCloseCallback(){
        shouldClose = true
    }

    fun onResizeCallback(width: Int, height: Int){
        _width = width
        _height = height
    }

    fun onMoveCallback(x: Int, y: Int){
        _x = x
        _y = y
    }

    /** https://learn.microsoft.com/en-us/windows/win32/menurc/about-cursors */
    fun getCursorCallback() = when(cursor){
        Cursor.DEFAULT -> 32512         // IDC_ARROW
        Cursor.HAND -> 32649            // IDC_HAND
        Cursor.TEXT -> 32513            // IDC_IBEAM
        Cursor.WAIT -> 32514            // IDC_WAIT
        Cursor.PROGRESS -> 32650        // IDC_APPSTARTING
        Cursor.CROSSHAIR -> 32515       // IDC_CROSS
        Cursor.NOT_ALLOWED -> 32648     // IDC_NO
        Cursor.HELP -> 32651            // IDC_HELP
        Cursor.SIZE_HORIZONTAL -> 32644 // IDC_SIZEWE
        Cursor.SIZE_VERTICAL -> 32645   // IDC_SIZENS
        Cursor.SIZE_NE -> 32643         // IDC_SIZENESW
        Cursor.SIZE_SE -> 32642         // IDC_SIZENWSE
        Cursor.MOVE -> 32646            // IDC_SIZEALL
        Cursor.SCROLL_VERTICAL -> 32652
        Cursor.SCROLL_HORIZONTAL -> 32653
        Cursor.SCROLL_ALL -> 32654
        Cursor.SCROLL_UP -> 32655
        Cursor.SCROLL_DOWN -> 32656
        Cursor.SCROLL_LEFT -> 32657
        Cursor.SCROLL_RIGHT -> 32658
        Cursor.SCROLL_TOP_LEFT -> 32659
        Cursor.SCROLL_TOP_RIGHT -> 32660
        Cursor.SCROLL_BOTTOM_LEFT -> 32661
        Cursor.SCROLL_BOTTOM_RIGHT -> 32662
    }
}