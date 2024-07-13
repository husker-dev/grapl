package com.huskerdev.grapl.ext.display.utils


@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.intAt(i: Int) = this[i].toInt()