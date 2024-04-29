package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.DisplayMode

abstract class WindowDisplayState private constructor(
    open var position: Position,
    open var size: Size,
    open var minSize: Size,
    open var maxSize: Size
){

    open class Windowed(
        position: Position = Position.ZERO,
        size: Size = Size(300, 400),
        minSize: Size = Size.UNDEFINED,
        maxSize: Size = Size.UNDEFINED
    ): WindowDisplayState(position, size, minSize, maxSize)


    open class Fullscreen(
        val displayMode: DisplayMode = Display.primary.mode
    ): WindowDisplayState(
        Position.ZERO,
        displayMode.size,
        Size.UNDEFINED,
        Size.UNDEFINED
    ) {
        override var position: Position
            get() = super.position
            set(value) {}
        override var size: Size
            get() = super.size
            set(value) {}
        override var minSize: Size
            get() = super.minSize
            set(value) {}
        override var maxSize: Size
            get() = super.maxSize
            set(value) {}
    }

    /**
     * Opens the window at the maximum native resolution, but renders at the specified size.
     * Helps avoid problems with switching between different monitor resolutions.
     */
    open class ScaledFullscreen(
        displayMode: DisplayMode = Display.primary.mode
    ): Fullscreen(displayMode) {
        override var size: Size
            get() = displayMode.size
            set(value) {}
    }
}