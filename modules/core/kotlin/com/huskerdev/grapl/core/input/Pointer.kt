package com.huskerdev.grapl.core.input

abstract class Pointer(
    open val id: Int
) {
    companion object {
        var DOUBLE_CLICK_DELAY = 400
        var DOUBLE_CLICK_RADIUS = 5

        var MASK_MODIFIER_ALT    = 0x00000001
        var MASK_MODIFIER_CTRL   = 0x00000002
        var MASK_MODIFIER_SHIFT  = 0x00000004
        var MASK_MODIFIER_OPTION = 0x00000008
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
        BACK,
        FORWARD;

        companion object {
            fun of(index: Int) = values()[index]
        }
    }

    open class Event(
        val pointer: Pointer,
        val modifiers: Int
    ) {
        /**
         * Alt on Linux/Windows,
         * Cmd on macOS
         */
        val isAltDown = modifiers and MASK_MODIFIER_ALT == MASK_MODIFIER_ALT
        val isCtrlDown = modifiers and MASK_MODIFIER_CTRL == MASK_MODIFIER_CTRL
        val isShiftDown = modifiers and MASK_MODIFIER_SHIFT == MASK_MODIFIER_SHIFT

        /**
         * Only for macOS
         */
        val isOptionDown = modifiers and MASK_MODIFIER_OPTION == MASK_MODIFIER_OPTION
    }

    open class ClickEvent(
        pointer: Pointer,
        modifiers: Int,
        val clicks: Int
    ): Event(pointer, modifiers)

    class MoveEvent(
        pointer: Pointer,
        modifiers: Int,
        val oldX: Double,
        val oldY: Double,
        val oldAbsoluteX: Double,
        val oldAbsoluteY: Double,
    ): Event(pointer, modifiers) {
        val deltaX = pointer.x - oldX
        val deltaY = pointer.y - oldY
        val deltaAbsoluteX = pointer.x - oldX
        val deltaAbsoluteY = pointer.y - oldY
    }

    class WheelEvent(
        pointer: Pointer,
        modifiers: Int,
        val deltaX: Double,
        val deltaY: Double
    ): Event(pointer, modifiers)

    class ZoomEvent(
        pointer: Pointer,
        modifiers: Int,
        val zoom: Double,
        val deltaZoom: Double
    ): Event(pointer, modifiers)

    class RotationEvent(
        pointer: Pointer,
        modifiers: Int,
        val angle: Double,
        val deltaAngle: Double
    ): Event(pointer, modifiers)
}