package com.huskerdev.grapl.core

import java.io.Serializable

data class Size(
    val width: Double,
    val height: Double
): Serializable {
    constructor(width: Int, height: Int): this(width.toDouble(), height.toDouble())
    constructor(width: Double, height: Int): this(width, height.toDouble())
    constructor(width: Int, height: Double): this(width.toDouble(), height)

    operator fun plus(other: Size) =
        Size(width + other.width, height + other.height)

    operator fun times(other: Size) =
        Size(width * other.width, height * other.height)

    operator fun times(multiplier: Double) =
        Size(width * multiplier, height * multiplier)

    operator fun div(other: Size) =
        Size(width / other.width, height / other.height)

    operator fun div(divider: Double) =
        Size(width / divider, height / divider)

    override fun toString(): String = "($width, $height)"
}


infix fun Double.x(that: Double) = Size(this, that)

infix fun Int.x(that: Int) = Size(this, that)