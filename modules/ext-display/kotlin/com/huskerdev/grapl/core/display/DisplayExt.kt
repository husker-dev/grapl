package com.huskerdev.grapl.core.display

import kotlin.experimental.and


val Display.manufacturerID: String
    get() = edid.run {
        val third = edid[9] and 0b00011111
        val second = (edid[8].toInt() and 0b00000011 shl 2) or (edid[9].toInt() and 0b11100000 shr 5)
        val first = (edid[8].toInt() and 0b01111100 shr 2)

        return@run "${(first + 64).toChar()}${(second + 64).toChar()}${(third + 64).toChar()}"
    }

/**
 * https://edid.tv/manufacturer/
 */
val Display.manufacturerName: String
    get() = when(manufacturerID) {
        "AAA" -> "Avolites Ltd"
        "ABC" -> "AboCom System Inc"
        "ACB" -> "Aculab Ltd"
        "ACI" -> "Ancor Communications Inc"
        "ACL" -> "Apricot Computers"
        "ACR" -> "Acer Technologies"
        "ACT" -> "Applied Creative Technology"
        "ADA" -> "Addi-Data GmbH"
        "ADI" -> "ADI Systems Inc"
        "AGO" -> "AlgolTek, Inc."
        "AJA" -> "AJA Video Systems, Inc."
        "AMI" -> "American Megatrends Inc"
        "AML" -> "Anderson Multimedia Communications (HK) Limited"
        "AMT" -> "AMT International Industry"
        "ANW" -> "Analog Way SAS"
        "ANX" -> "Acer Netxus Inc"
        "API" -> "A Plus Info Corporation"
        "APP" -> "Apple Computer Inc"
        "ARD" -> "AREC Inc."
        "ART" -> "Corion Industrial Corporation"
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""
        "" -> ""

        else -> "Unknown"
    }