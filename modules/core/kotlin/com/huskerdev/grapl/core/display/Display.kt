package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.x

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
    val absoluteX by peer.position::width
    val absoluteY by peer.position::height

    val physicalWidth by peer.physicalSize::width
    val physicalHeight by peer.physicalSize::height

    val size: Size<Double, Double>
        get() = width x height
    val width: Double
        get() = peer.size.width / peer.dpi
    val height: Double
        get() = peer.size.height / peer.dpi

    val position: Size<Double, Double>
        get() = x x y
    val x: Double
        get() = peer.position.width / peer.dpi
    val y: Double
        get() = peer.position.height / peer.dpi

    val dpi by peer::dpi

    val frequency by peer::frequency

    val name by peer::name

    val systemName by peer::systemName


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return peer == (other as Display).peer
    }

    override fun hashCode() = peer.hashCode()

}