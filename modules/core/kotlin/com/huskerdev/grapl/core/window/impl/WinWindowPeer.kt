package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.window.WindowPeer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class WinWindowPeer(
    val hwnd: Long,
): WindowPeer() {

    companion object {
        @JvmStatic private external fun nHookWindow(hwnd: Long, callbackClass: Class<Callbacks>)
        @JvmStatic private external fun nPeekMessage(hwnd: Long)
        @JvmStatic private external fun nPostQuit(hwnd: Long)   // Pushes WM_QUIT

        @JvmStatic private external fun nSetVisible(hwnd: Long, value: Boolean)
        @JvmStatic private external fun nSetPosition(hwnd: Long, x: Int, y: Int)
        @JvmStatic private external fun nSetSize(hwnd: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetTitle(hwnd: Long, title: ByteBuffer)

        private val activeWindows = hashMapOf<Long, WinWindowPeer>()

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
        nHookWindow(hwnd, Callbacks::class.java)
        activeWindows[hwnd] = this
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

    class Callbacks {
        companion object {
            @JvmStatic fun onClose(hwnd: Long){
                activeWindows[hwnd]?.shouldClose = true
                activeWindows.remove(hwnd)
            }

            @JvmStatic fun onResize(hwnd: Long, width: Int, height: Int){
                activeWindows[hwnd]?.apply {
                    _width = width
                    _height = height
                }
            }

            @JvmStatic fun onMove(hwnd: Long, x: Int, y: Int){
                activeWindows[hwnd]?.apply {
                    _x = x
                    _y = y
                }
            }
        }
    }
}