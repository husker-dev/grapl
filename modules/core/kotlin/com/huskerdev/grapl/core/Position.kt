package com.huskerdev.grapl.core

import java.io.Serializable

data class Position(
    val x: Double,
    val y: Double
): Serializable {
    companion object {
        val ZERO = Position(0, 0)
    }

    constructor(x: Int, y: Int): this(x.toDouble(), y.toDouble())
    constructor(x: Double, y: Int): this(x, y.toDouble())
    constructor(x: Int, y: Double): this(x.toDouble(), y)

    fun withX(newX: Double) = Position(newX, y)
    fun withX(newX: Int) = Position(newX, y)

    fun withY(newY: Double) = Position(x, newY)
    fun withY(newY: Int) = Position(x, newY)

    operator fun plus(other: Position) =
        Position(x + other.x, y + other.y)

    operator fun times(other: Position) =
        Position(x * other.x, y * other.y)

    operator fun times(multiplier: Double) =
        Position(x * multiplier, y * multiplier)

    operator fun div(other: Position) =
        Position(x / other.x, y / other.y)

    operator fun div(divider: Double) =
        Position(x / divider, y / divider)

    override fun toString(): String = "($x, $y)"
}