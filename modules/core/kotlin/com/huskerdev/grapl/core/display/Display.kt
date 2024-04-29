package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.platform.Platform

class Display(
    val peer: DisplayPeer
) {

    companion object {
        val primary by Platform.current::primaryDisplay
        val list by Platform.current::displays
    }

    val absoluteSize by peer::size
    val absoluteWidth by peer.size::width
    val absoluteHeight by peer.size::height

    val absolutePosition by peer::position
    val absoluteX by peer.position::x
    val absoluteY by peer.position::y

    val physicalWidth by peer.physicalSize::width
    val physicalHeight by peer.physicalSize::height

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

    val dpi by peer::dpi

    val frequency by peer::frequency

    val name by peer::name

    val systemName by peer::systemName

    val modes by peer::modes

    val mode by peer::mode

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return peer == (other as Display).peer
    }

    override fun hashCode() = peer.hashCode()

}