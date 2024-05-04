package com.huskerdev.grapl.core.input

abstract class Pointer(
    open val id: Int
) {
    companion object {
        var DOUBLE_CLICK_DELAY = 400
        var DOUBLE_CLICK_RADIUS = 5
    }

    abstract val absoluteX: Int
    abstract val absoluteY: Int
    abstract val x: Double
    abstract val y: Double
    abstract val buttons: Set<Button>

    enum class Button {
        NONE,
        LEFT,
        MIDDLE,
        RIGHT,
        MOUSE_BACK,
        MOUSE_FORWARD;

        companion object {
            fun of(index: Int) = values()[index]
        }
    }

    open class Event(
        val pointer: Pointer,

        /**
         * Alt on Linux/Windows
         * Cmd on macOS
         */
        val isAltDown: Boolean,
        val isCtrlDown: Boolean,
        val isShiftDown: Boolean,
        val isOptionDown: Boolean
    )

    open class ClickEvent(
        pointer: Pointer,
        isAltDown: Boolean,
        isCtrlDown: Boolean,
        isShiftDown: Boolean,
        isOptionDown: Boolean,
        val clicks: Int
    ): Event(pointer, isAltDown, isCtrlDown, isShiftDown, isOptionDown)

    class MoveEvent(
        pointer: Pointer,
        isAltDown: Boolean,
        isCtrlDown: Boolean,
        isShiftDown: Boolean,
        isOptionDown: Boolean,
        val oldX: Double,
        val oldY: Double,
        val oldAbsoluteX: Double,
        val oldAbsoluteY: Double,
    ): Event(pointer, isAltDown, isCtrlDown, isShiftDown, isOptionDown) {
        val deltaX = pointer.x - oldX
        val deltaY = pointer.y - oldY
        val deltaAbsoluteX = pointer.x - oldX
        val deltaAbsoluteY = pointer.y - oldY
    }

    class WheelEvent(
        pointer: Pointer,
        isAltDown: Boolean,
        isCtrlDown: Boolean,
        isShiftDown: Boolean,
        isOptionDown: Boolean,
        val deltaX: Double,
        val deltaY: Double
    ): Event(pointer, isAltDown, isCtrlDown, isShiftDown, isOptionDown)
}