package com.huskerdev.grapl.core.input


var DOUBLE_CLICK_DELAY = 400
var DOUBLE_CLICK_RADIUS = 5

abstract class Pointer(
    open val id: Int
) {
    abstract val absoluteX: Int
    abstract val absoluteY: Int
    abstract val x: Double
    abstract val y: Double
    abstract val buttons: Set<Int>

    override fun toString(): String {
        return "Pointer(id=$id, absoluteX=$absoluteX, absoluteY=$absoluteY, x=$x, y=$y, buttons=$buttons)"
    }
}

open class PointerEvent(
    val pointer: Pointer,
    modifiers: Int
): InputEvent(modifiers)

open class PointerPressEvent(
    pointer: Pointer,
    modifiers: Int,
    val button: Int
): PointerEvent(pointer, modifiers)

open class PointerReleaseEvent(
    pointer: Pointer,
    modifiers: Int,
    val button: Int
): PointerEvent(pointer, modifiers)

open class PointerClickEvent(
    pointer: Pointer,
    modifiers: Int,
    val button: Int,
    val clicks: Int
): PointerEvent(pointer, modifiers)

open class PointerMoveEvent(
    pointer: Pointer,
    modifiers: Int,
    val oldX: Double,
    val oldY: Double,
    val oldAbsoluteX: Double,
    val oldAbsoluteY: Double,
): PointerEvent(pointer, modifiers) {
    val deltaX = pointer.x - oldX
    val deltaY = pointer.y - oldY
    val deltaAbsoluteX = pointer.x - oldX
    val deltaAbsoluteY = pointer.y - oldY
}

open class PointerScrollEvent(
    pointer: Pointer,
    modifiers: Int,
    val deltaX: Double,
    val deltaY: Double
): PointerEvent(pointer, modifiers)

open class PointerZoomEvent(
    pointer: Pointer,
    modifiers: Int,
    val zoom: Double,
    val deltaZoom: Double
): PointerEvent(pointer, modifiers)

open class PointerRotationEvent(
    pointer: Pointer,
    modifiers: Int,
    val angle: Double,
    val deltaAngle: Double
): PointerEvent(pointer, modifiers)