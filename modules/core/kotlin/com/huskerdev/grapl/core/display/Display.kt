package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.platform.Platform

class Display(
    val peer: DisplayPeer
) {

    companion object {
        val primary: Display
            get() = Platform.current.primaryDisplay
        val list: Array<Display>
            get() = Platform.current.displays
    }

    val absoluteSize: Size
        get() = peer.size
    val absoluteWidth: Double
        get() = peer.size.width
    val absoluteHeight: Double
        get() = peer.size.height

    val absolutePosition: Position
        get() = peer.position
    val absoluteX: Double
        get() = peer.position.x
    val absoluteY: Double
        get() = peer.position.y

    val size: Size
        get() = absoluteSize / peer.dpi
    val width: Double
        get() = absoluteWidth / peer.dpi
    val height: Double
        get() = absoluteHeight / peer.dpi

    val position: Position
        get() = absolutePosition / peer.dpi
    val x: Double
        get() = absoluteX / peer.dpi
    val y: Double
        get() = absoluteY / peer.dpi

    val dpi: Double
        get() = peer.dpi

    val frequency: Int
        get() = peer.frequency

    val systemName: String
        get() = peer.systemName

    val modes: Array<DisplayMode>
        get() = peer.modes

    val mode: DisplayMode
        get() = peer.mode

    @ExperimentalUnsignedTypes
    val edid: UByteArray
        get() = peer.edid

    override fun toString(): String {
        return "Display[" +
                "absoluteSize:$absoluteSize, " +
                "absolutePosition:$absolutePosition, " +
                "size:$size, " +
                "position:$position, " +
                "dpi:$dpi, " +
                "frequency:$frequency, " +
                "systemName:\"$systemName\"" +
                "]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return peer == (other as Display).peer
    }

    override fun hashCode() = peer.hashCode()

}