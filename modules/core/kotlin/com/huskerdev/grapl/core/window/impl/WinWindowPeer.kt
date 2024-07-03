package com.huskerdev.grapl.core.window.impl

import com.huskerdev.grapl.core.util.c_wstr
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.WinDisplayPeer
import com.huskerdev.grapl.core.exceptions.DisplayModeChangingException
import com.huskerdev.grapl.core.window.WindowDisplayState
import com.huskerdev.grapl.core.window.WindowPeer
import java.nio.ByteBuffer

open class WinWindowPeer(
    hwnd: Long,
): WindowPeer(hwnd) {
    companion object {
        @JvmStatic private external fun nHookWindow(hwnd: Long, callbackObject: Any)
        @JvmStatic private external fun nPostQuit(hwnd: Long)   // Pushes WM_QUIT

        @JvmStatic private external fun nSetVisible(hwnd: Long, value: Boolean)
        @JvmStatic private external fun nSetCursor(hwnd: Long, cursor: Int)
        @JvmStatic private external fun nSetPosition(hwnd: Long, x: Int, y: Int)
        @JvmStatic private external fun nSetSize(hwnd: Long, width: Int, height: Int)
        @JvmStatic private external fun nSetMinSize(hwnd: Long, minWidth: Int, minHeight: Int)
        @JvmStatic private external fun nSetMaxSize(hwnd: Long, maxWidth: Int, maxHeight: Int)
        @JvmStatic private external fun nSetTitle(hwnd: Long, title: ByteBuffer)
        @JvmStatic private external fun nGetMonitor(hwnd: Long): Long
        @JvmStatic private external fun nUpdateDisplayState(hwnd: Long, fullscreen: Boolean, monitor: Long, width: Int, height: Int, bits: Int, frequency: Int): Int
        @JvmStatic private external fun nSetMinimizable(hwnd: Long, value: Boolean)
        @JvmStatic private external fun nSetMaximizable(hwnd: Long, value: Boolean)
        @JvmStatic private external fun nGetDpi(hwnd: Long): Float
    }

    init {
        nHookWindow(hwnd, WinWindowCallback())
    }

    override fun destroy() = nPostQuit(handle)

    override fun setTitleImpl(title: String) = nSetTitle(handle, title.c_wstr)
    override fun setVisibleImpl(visible: Boolean) = nSetVisible(handle, visible)
    override fun setCursorImpl(cursor: Cursor) = nSetCursor(handle, cursor.toWin32())
    override fun setSizeImpl(size: Size) = nSetSize(handle, size.width.toInt(), size.height.toInt())
    override fun setMinSizeImpl(size: Size) = nSetMinSize(handle, size.width.toInt(), size.height.toInt())
    override fun setMaxSizeImpl(size: Size) = nSetMaxSize(handle, size.width.toInt(), size.height.toInt())
    override fun setPositionImpl(position: Position) = nSetPosition(handle, position.x.toInt(), position.y.toInt())
    override fun setMinimizableImpl(value: Boolean) = nSetMinimizable(handle, value)
    override fun setMaximizableImpl(value: Boolean) = nSetMaximizable(handle, value)
    override fun getDpiImpl() = nGetDpi(handle).toDouble()
    override fun getDisplayImpl() = Display(WinDisplayPeer(nGetMonitor(handle)))

    override fun setDisplayStateImpl(state: WindowDisplayState) {
        val mode = when(state) {
            is WindowDisplayState.Windowed          -> null
            is WindowDisplayState.ScaledFullscreen  -> state.displayMode.display.mode
            is WindowDisplayState.Fullscreen        -> state.displayMode
            else -> throw UnsupportedOperationException("Unsupported display state")
        }

        val result = if(mode != null)
            nUpdateDisplayState(
                handle, true, mode.display.peer.handle,
                mode.size.width.toInt(), mode.size.height.toInt(),
                mode.bits, mode.frequency
            )
        else nUpdateDisplayState(handle, false, 0, 0, 0, 0, 0)

        if(result != 0){
            throw DisplayModeChangingException(mode, when(result) {
                1 -> "The settings change was unsuccessful because the system is DualView capable (DISP_CHANGE_BADDUALVIEW)"
                2 -> "An invalid set of flags was passed in (DISP_CHANGE_BADFLAGS)"
                3 -> "The graphics mode is not supported (DISP_CHANGE_BADMODE)"
                4 -> "An invalid parameter was passed in. This can include an invalid flag or combination of flags (DISP_CHANGE_BADPARAM)"
                5 -> "The display driver failed the specified graphics mode (DISP_CHANGE_FAILED)"
                6 -> "Unable to write settings to the registry (DISP_CHANGE_NOTUPDATED)"
                7 -> "The computer must be restarted for the graphics mode to work (DISP_CHANGE_RESTART)"
                else -> ""
            })
        }
    }

    inner class WinWindowCallback: DefaultWindowCallback(){
        override fun onResizeCallback(width: Int, height: Int) {
            super.onResizeCallback(width, height)
            dispatchUpdate()
        }
    }

    private fun Cursor.toWin32() = when(this){
        Cursor.DEFAULT                -> 32512 // IDC_ARROW
        Cursor.HAND                   -> 32649 // IDC_HAND
        Cursor.TEXT                   -> 32513 // IDC_IBEAM
        Cursor.WAIT                   -> 32514 // IDC_WAIT
        Cursor.PROGRESS               -> 32650 // IDC_APPSTARTING
        Cursor.CROSSHAIR              -> 32515 // IDC_CROSS
        Cursor.NOT_ALLOWED            -> 32648 // IDC_NO
        Cursor.HELP                   -> 32651 // IDC_HELP
        Cursor.SIZE_W,
        Cursor.SIZE_E,
        Cursor.SIZE_HORIZONTAL_DOUBLE -> 32644 // IDC_SIZEWE
        Cursor.SIZE_N,
        Cursor.SIZE_S,
        Cursor.SIZE_VERTICAL_DOUBLE   -> 32645 // IDC_SIZENS
        Cursor.SIZE_NE                -> 32643 // IDC_SIZENESW
        Cursor.SIZE_SE                -> 32642 // IDC_SIZENWSE
        Cursor.MOVE                   -> 32646 // IDC_SIZEALL
        Cursor.SCROLL_ALL             -> 32654
        Cursor.SCROLL_UP              -> 32655
        Cursor.SCROLL_DOWN            -> 32656
        Cursor.SCROLL_LEFT            -> 32657
        Cursor.SCROLL_RIGHT           -> 32658
        Cursor.SCROLL_TOP_LEFT        -> 32659
        Cursor.SCROLL_TOP_RIGHT       -> 32660
        Cursor.SCROLL_BOTTOM_LEFT     -> 32661
        Cursor.SCROLL_BOTTOM_RIGHT    -> 32662
    }
}