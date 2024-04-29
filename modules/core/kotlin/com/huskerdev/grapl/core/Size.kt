package com.huskerdev.grapl.core

import java.io.Serializable

data class Size(
    val width: Double,
    val height: Double
): Serializable, Comparable<Size> {
    companion object {
        val ZERO = Size(0, 0)
        val UNDEFINED = Size(-1, -1)
    }

    constructor(width: Int, height: Int): this(width.toDouble(), height.toDouble())
    constructor(width: Double, height: Int): this(width, height.toDouble())
    constructor(width: Int, height: Double): this(width.toDouble(), height)

    fun withWidth(newWidth: Double) = Size(newWidth, height)
    fun withWidth(newWidth: Int) = Size(newWidth, height)

    fun withHeight(newHeight: Double) = Size(width, newHeight)
    fun withHeight(newHeight: Int) = Size(width, newHeight)

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

    override fun compareTo(other: Size) =
        (width * height).compareTo(other.width * other.height)

    override fun toString(): String = "($width, $height)"
}


infix fun Double.x(that: Double) = Size(this, that)

infix fun Int.x(that: Int) = Size(this, that)