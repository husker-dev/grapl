package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.GraplNatives

abstract class DisplayPeer(
    val handle: Long
) {
    companion object {
        init {
            GraplNatives.load()
        }
    }

    abstract val size: Pair<Int, Int>
    abstract val position: Pair<Int, Int>
    abstract val physicalSize: Pair<Int, Int>
    abstract val dpi: Double
    abstract val frequency: Int
    abstract val name: String
    abstract val systemName: String
}