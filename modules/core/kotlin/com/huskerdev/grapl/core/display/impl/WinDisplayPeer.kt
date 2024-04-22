package com.huskerdev.grapl.core.display.impl

import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.DisplayPeer

class WinDisplayPeer(
    monitor: Long
): DisplayPeer(monitor) {
    companion object {
        @JvmStatic private external fun nGetPrimaryMonitor(): Long
        @JvmStatic private external fun nGetAllMonitors(): LongArray
        @JvmStatic private external fun nGetSize(monitor: Long): IntArray
        @JvmStatic private external fun nGetPosition(monitor: Long): IntArray
        @JvmStatic private external fun nGetDpi(monitor: Long): Double
        @JvmStatic private external fun nGetFrequency(monitor: Long): Int
        @JvmStatic private external fun nGetName(monitor: Long): String
        @JvmStatic private external fun nGetSystemName(monitor: Long): String
        @JvmStatic private external fun nGetPhysicalSize(monitor: Long): IntArray


        val primary: DisplayPeer
            get() = WinDisplayPeer(nGetPrimaryMonitor())

        val list: Array<DisplayPeer>
            get() = nGetAllMonitors().map { WinDisplayPeer(it) }.toTypedArray()
    }

    override val size: Size
        get() = nGetSize(handle).run { Size(this[0], this[1]) }
    override val position: Size
        get() = nGetPosition(handle).run { Size(this[0], this[1]) }
    override val physicalSize: Size
        get() = nGetPhysicalSize(handle).run { Size(this[0], this[1]) }

    override val dpi: Double
        get() = nGetDpi(handle)
    override val frequency: Int
        get() = nGetFrequency(handle)

    override val name = nGetName(handle)
    override val systemName = nGetSystemName(handle)
}