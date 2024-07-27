package com.huskerdev.grapl.core.display.impl

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.DisplayMode
import com.huskerdev.grapl.core.display.DisplayPeer

class MacDisplayPeer(
    screen: Long
): DisplayPeer(screen) {

    companion object {
        @JvmStatic private external fun nGetMainScreen(): Long
        @JvmStatic private external fun nGetScreens(): LongArray

        @JvmStatic private external fun nGetSize(screen: Long): DoubleArray
        @JvmStatic private external fun nGetPosition(screen: Long): DoubleArray
        @JvmStatic private external fun nGetDpi(screen: Long): Double
        @JvmStatic private external fun nGetFrequency(screen: Long): Int
        @JvmStatic private external fun nGetIndex(screen: Long): Int
        @JvmStatic private external fun nGetDisplayModes(screen: Long): IntArray
        @JvmStatic private external fun nGetCurrentDisplayMode(screen: Long): IntArray
        @JvmStatic private external fun nGetEDID(screen: Long): ByteArray

        val primary: DisplayPeer
            get() = MacDisplayPeer(nGetMainScreen())

        val list: Array<DisplayPeer>
            get() = nGetScreens().map { MacDisplayPeer(it) }.toTypedArray()
    }

    override val size: Size
        get() = nGetSize(handle).run { Size(this[0], this[1]) }
    override val position: Position
        get() = nGetPosition(handle).run { Position(this[0], this[1]) }

    override val dpi: Double
        get() = nGetDpi(handle)

    override val frequency: Int
        get() = nGetFrequency(handle)

    override val systemName = "Screen ${nGetIndex(handle)}"

    override val modes: Array<DisplayMode>
        get() = nGetDisplayModes(handle).asList()
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
        get() = nGetCurrentDisplayMode(handle).run {
            DisplayMode(
                Display(this@MacDisplayPeer),
                Size(this[0], this[1]),
                this[2],
                this[3]
            )
        }

    @ExperimentalUnsignedTypes
    override val edid: UByteArray
        get() = nGetEDID(handle).toUByteArray()
}