package com.huskerdev.grapl.core

data class Dimension(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
) {

    constructor(position: Position, size: Size):
            this(position.x, position.y, size.width, size.height)

    operator fun contains(point: Position) =
        point.x > x &&
        point.y > y &&
        point.x < x + width &&
        point.y < y + height

    fun intersect(other: Dimension) =
        x < (other.x + other.width) && (x + width) > other.x &&
        y < (other.y + other.height) && (y + height) > other.y
}