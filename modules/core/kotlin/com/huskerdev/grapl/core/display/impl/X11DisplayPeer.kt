package com.huskerdev.grapl.core.display.impl

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.DisplayMode
import com.huskerdev.grapl.core.display.DisplayPeer

class X11DisplayPeer(
    val display: Long,
    screen: Long
): DisplayPeer(screen) {

    companion object {
        @JvmStatic private external fun nGetPrimaryScreen(display: Long): Long
        @JvmStatic private external fun nGetAllScreens(display: Long): LongArray
        @JvmStatic private external fun nGetSize(display: Long, screen: Long): IntArray
        @JvmStatic private external fun nGetPosition(display: Long, screen: Long): IntArray
        @JvmStatic private external fun nGetDpi(display: Long, screen: Long): Double
        @JvmStatic private external fun nGetFrequency(display: Long, screen: Long): Int
        @JvmStatic private external fun nGetSystemName(display: Long, screen: Long): String
        @JvmStatic private external fun nGetDisplayModes(display: Long, screen: Long): IntArray
        @JvmStatic private external fun nGetCurrentDisplayMode(display: Long, screen: Long): IntArray
        @JvmStatic private external fun nGetEDID(display: Long, screen: Long): ByteArray

        fun primary(display: Long) = X11DisplayPeer(display, nGetPrimaryScreen(display))

        fun list(display: Long) = nGetAllScreens(display).map { X11DisplayPeer(display, it) }.toTypedArray()
    }

    override val size: Size
        get() = nGetSize(display, handle).run { Size(this[0], this[1]) }

    override val position: Position
        get() = nGetPosition(display, handle).run { Position(this[0], this[1]) }

    override val dpi: Double
        get() = nGetDpi(display, handle)

    override val frequency: Int
        get() = nGetFrequency(display, handle)

    override val systemName: String
        get() = nGetSystemName(display, handle)

    override val modes: Array<DisplayMode>
        get() = nGetDisplayModes(display, handle).asList()
            .windowed(4, 4)
            .map {
                DisplayMode(
                    Display(this),
                    Size(it[0], it[1]),
                    it[2],
                    it[3]
                )
            }.sorted().toTypedArray()

    override val mode: DisplayMode
        get() = nGetCurrentDisplayMode(display, handle).run {
            DisplayMode(
                Display(this@X11DisplayPeer),
                Size(this[0], this[1]),
                this[2],
                this[3]
            )
        }

    @ExperimentalUnsignedTypes
    override val edid: UByteArray
        get() = nGetEDID(display, handle).toUByteArray()

}