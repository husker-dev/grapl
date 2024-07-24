@file:OptIn(ExperimentalUnsignedTypes::class)

package com.huskerdev.grapl.ext.display

import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.ext.display.utils.intAt
import java.time.LocalDate
import java.time.Month

enum class VideoInterface {
    UNDEFINED,
    DVI,
    HDMI,
    MDDI,
    DISPLAY_PORT
}


val manufacturers by lazy {
    object {}::class.java.getResourceAsStream("/com/huskerdev/grapl/ext/display/manufacturers")
        ?.reader()?.readLines()?.associate { line ->
            val key = line.split(" ")[0]
            key to line.replace("$key ", "")
        } ?: emptyMap()
}

val Display.manufacturerId: String
    get() = edid.run {
        val third = (this[9] and 0b00011111u).toInt()
        val second = (this[8].toInt() and 0b00000011 shl 3) or (this[9].toInt() and 0b11100000 shr 5)
        val first = (this[8].toInt() and 0b01111100 shr 2)

        return@run "${(first + 64).toChar()}${(second + 64).toChar()}${(third + 64).toChar()}"
    }

val Display.manufacturerName: String?
    get() = manufacturers[this.manufacturerId]

val Display.manufactureYear: Int
    get() = edid[17].toInt() + 1990

val Display.manufactureWeek: Int
    get() = edid[16].toInt()

val Display.manufactureDate: LocalDate
    get() = edid.run {
        return@run LocalDate.of(this[17].toInt() + 1990, Month.JANUARY, 1)
            .plusWeeks(this[16].toLong())
    }

val Display.productId: String
    get() = edid.run {
        return@run ((this[11].toLong() shl 8) or this[10].toLong())
            .toString(16).padStart(4, '0')
    }

val Display.serialNumber: String
    get() = edid.run {
        return@run (this[15].toString(16) +
                   this[14].toString(16) +
                   this[13].toString(16) +
                   this[12].toString(16)).padStart(8, '0')
    }

val Display.isDigital: Boolean
    get() = edid[20] and 0b10000000.toUByte() == 0b10000000.toUByte()

val Display.videoInterface: VideoInterface
    get() = if(isDigital) edid.run {
        when(intAt(20) and 0b00001111) {
            1 -> VideoInterface.DVI
            2, 3 -> VideoInterface.HDMI
            4 -> VideoInterface.MDDI
            5 -> VideoInterface.DISPLAY_PORT
            else -> {
                // Iterate over extensions
                for(i in 1..intAt(126)){
                    val o = i * 128
                    // If extension is CTA v3
                    if(intAt(o) == 2 && intAt(o+1) == 3){
                        var r = 4
                        val dtdEnd = intAt(o+2)
                        while(r < dtdEnd){
                            // If block is Vendor Specific Data Block
                            if((intAt(o+r) and 0b11100000 shr 5) == 3){
                                val k3 = intAt(o+r+1)
                                val k2 = intAt(o+r+2)
                                val k1 = intAt(o+r+3)
                                // If IEEE Registration Identifier is:
                                // HDMI Licensing, LLC (00 0C 03)
                                // or
                                // HDMI Forum          (C4 5D D8)
                                if((k1 == 0x00 && k2 == 0x0C && k3 == 0x03) ||
                                   (k1 == 0xC4 && k2 == 0x5D && k3 == 0xD8)
                                ) return@run VideoInterface.HDMI
                            }
                            r += 1 + (intAt(o+r) and 0b00011111)
                        }
                    }
                }
                VideoInterface.UNDEFINED
            }
        }
    } else VideoInterface.UNDEFINED


val Display.edidVersion: String
    get() = edid.run { "${this[18]}.${this[19]}" }

val Display.mmSize: Size
    get() = edid.run {
        if(this[54].toInt() != 0 && this[55].toInt() != 0)
            Size(intAt(54 + 12) or (intAt(54 + 14) and 0b11110000 shl 4),
                 intAt(54 + 13) or (intAt(54 + 14) and 0b00001111 shl 8))
        else
            Size(this[21].toInt() * 10,
                 this[22].toInt() * 10)
    }

val Display.name: String
    get() = edid.run {
        for(i in 54..108 step 18){
            if(intAt(i) == 0 && intAt(i + 1) == 0 && intAt(i + 2) == 0 &&
                (intAt(i + 3) == 0xFC || intAt(i + 3) == 0xFE)
            ){
                var name = ""
                var r = i + 5
                while(intAt(r) != 10 && r < i + 18){
                    name += intAt(r).toChar()
                    r++
                }
                return@run name
            }
        }
        return@run "Display"
    }