package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.Size

abstract class DisplayPeer(
    val handle: Long
) {
    companion object {
        init {
            GraplNatives.load()
        }
    }

    abstract val size: Size<Int, Int>
    abstract val position: Size<Int, Int>
    abstract val physicalSize: Size<Int, Int>
    abstract val dpi: Double
    abstract val frequency: Int
    abstract val name: String
    abstract val systemName: String
}