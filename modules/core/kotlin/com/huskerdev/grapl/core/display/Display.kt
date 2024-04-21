package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.core.platform.Platform

class Display(
    val peer: DisplayPeer
) {

    companion object {
        val primary by Platform.current::primaryDisplay
        val list by Platform.current::displays
    }

    val absoluteSize by peer::size
    val absoluteWidth by peer.size::first
    val absoluteHeight by peer.size::second

    val absolutePosition by peer::position
    val absoluteX by peer.position::first
    val absoluteY by peer.position::second

    val physicalWidth by peer.physicalSize::first
    val physicalHeight by peer.physicalSize::second

    val size: Pair<Double, Double>
        get() = width to height
    val width: Double
        get() = peer.size.first / peer.dpi
    val height: Double
        get() = peer.size.second / peer.dpi

    val position: Pair<Double, Double>
        get() = x to y
    val x: Double
        get() = peer.position.first / peer.dpi
    val y: Double
        get() = peer.position.second / peer.dpi

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