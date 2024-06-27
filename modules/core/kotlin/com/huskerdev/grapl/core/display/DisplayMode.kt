package com.huskerdev.grapl.core.display

import com.huskerdev.grapl.core.Size

data class DisplayMode(
    val display: Display,
    val size: Size,
    val bits: Int,
    val frequency: Int,
): Comparable<DisplayMode>{
    override fun compareTo(other: DisplayMode): Int {
        var res = size.compareTo(other.size)
        if(res == 0) res = bits.compareTo(other.bits)
        if(res == 0) res = frequency.compareTo(frequency)
        return res
    }
}
