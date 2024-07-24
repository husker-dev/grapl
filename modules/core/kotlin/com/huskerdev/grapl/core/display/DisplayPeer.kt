package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.Dimension
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size

abstract class DisplayPeer(
    val handle: Long
) {
    companion object {
        init {
            GraplNatives.load()
        }
    }

    abstract val size: Size
    abstract val position: Position
    abstract val physicalSize: Size
    abstract val dpi: Double
    abstract val frequency: Int
    abstract val systemName: String
    abstract val modes: Array<DisplayMode>
    abstract val mode: DisplayMode

    @OptIn(ExperimentalUnsignedTypes::class)
    abstract val edid: UByteArray

    val dimension: Dimension
        get() = Dimension(position, size)
}