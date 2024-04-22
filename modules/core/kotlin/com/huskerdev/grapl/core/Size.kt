package com.huskerdev.grapl.core

import java.io.Serializable

data class Size<out A, out B>(
    val width: A,
    val height: B
): Serializable {
    override fun toString(): String = "($width, $height)"
}

infix fun <A, B> A.x(that: B) = Size(this, that)