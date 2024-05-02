package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.DisplayMode
import com.huskerdev.grapl.core.input.Pointer
import com.huskerdev.grapl.core.input.Pointer.Companion.DOUBLE_CLICK_DELAY
import com.huskerdev.grapl.core.input.Pointer.Companion.DOUBLE_CLICK_RADIUS
import com.huskerdev.grapl.core.util.Property
import com.huskerdev.grapl.core.util.LinkedProperty
import com.huskerdev.grapl.core.util.ReadOnlyProperty
import kotlin.math.hypot


abstract class WindowPeer() {
    companion object {
        init {
            GraplNatives.load()
        }
    }

    var handle = 0L
        protected set

    protected var shouldClose = false

    constructor(handle: Long): this() {
        this.handle = handle
    }

    abstract val display: Display

    val dpi by display::dpi

    val pointerMoveListeners = hashSetOf<(Pointer.MoveEvent) -> Unit>()
    val pointerDragListeners = hashSetOf<(Pointer.MoveEvent) -> Unit>()

    val pointerPressListeners = hashSetOf<(Pointer.Event) -> Unit>()
    val pointerReleaseListeners = hashSetOf<(Pointer.Event) -> Unit>()
    val pointerClickListeners = hashSetOf<(Pointer.ClickEvent) -> Unit>()

    val pointerEnterListeners = hashSetOf<(Pointer.Event) -> Unit>()
    val pointerLeaveListeners = hashSetOf<(Pointer.Event) -> Unit>()

    val displayStateProperty = Property<WindowDisplayState>(WindowDisplayState.Windowed()) {
        setDisplayStateImpl(it)
        if(it !is WindowDisplayState.Fullscreen){
            setSizeImpl(it.size)
            setPositionImpl(it.position)
            setMaxSizeImpl(it.maxSize)
            setMinSizeImpl(it.minSize)
        }
    }

    val positionProperty = LinkedProperty(displayStateProperty.value::position) {
        if(!isFullscreen())
            setPositionImpl(it)
    }

    val sizeProperty = LinkedProperty(displayStateProperty.value::size){
        if(!isFullscreen())
            setSizeImpl(it)
    }

    val minSizeProperty = LinkedProperty(displayStateProperty.value::minSize){
        if(!isFullscreen())
            setMinSizeImpl(it)
    }

    val maxSizeProperty = LinkedProperty(displayStateProperty.value::maxSize){
        if(!isFullscreen())
            setMaxSizeImpl(it)
    }

    val titleProperty = Property("", ::setTitleImpl)

    val visibleProperty = Property(false, ::setVisibleImpl)

    val focusedProperty = ReadOnlyProperty(false)

    val viewportProperty = ReadOnlyProperty(Size.UNDEFINED)

    val cursor = Property(Cursor.DEFAULT, ::setCursorImpl)

    val pointers = hashMapOf<Int, Pointer>()

    val minimizable = Property(true, ::setMinimizableImpl)

    val maximizable = Property(true, ::setMaximizableImpl)


    fun runEventLoop(loopCallback: () -> Unit) {
        while (!shouldClose) {
            loopCallback()
            peekMessages()
        }
    }

    protected fun isFullscreen() = displayStateProperty.value is WindowDisplayState.Fullscreen

    abstract fun destroy()
    abstract fun peekMessages()

    protected abstract fun setTitleImpl(title: String)
    protected abstract fun setVisibleImpl(visible: Boolean)
    protected abstract fun setCursorImpl(cursor: Cursor)

    protected abstract fun setSizeImpl(size: Size)
    protected abstract fun setMinSizeImpl(size: Size)
    protected abstract fun setMaxSizeImpl(size: Size)
    protected abstract fun setPositionImpl(position: Position)
    protected abstract fun setDisplayStateImpl(state: WindowDisplayState)
    protected abstract fun setMinimizableImpl(value: Boolean)
    protected abstract fun setMaximizableImpl(value: Boolean)

    open inner class DefaultWindowCallback {

        inner class WrappedPointer(id: Int): Pointer(id) {
            var lastReleaseTime = 0L
            var lastButton: Button? = null
            var lastButtonX = 0
            var lastButtonY = 0
            var clicks = 0

            var _absoluteX: Int = 0
            override var absoluteX by ::_absoluteX

            var _absoluteY: Int = 0
            override var absoluteY by ::_absoluteY

            var _x: Double = 0.0
            override var x by ::_x

            var _y: Double = 0.0
            override var y by ::_y

            var _buttons: Set<Button> = hashSetOf()
            override var buttons by ::_buttons
        }

        open fun onCloseCallback(){
            shouldClose = true
        }

        open fun onResizeCallback(width: Int, height: Int){
            sizeProperty.internalValue = Size(width, height)
            viewportProperty.internalValue = if(isFullscreen()) displayStateProperty.value.size else sizeProperty.value
        }

        open fun onMoveCallback(x: Int, y: Int){
            positionProperty.internalValue = Position(x, y)
        }

        open fun onFocusCallback(focused: Boolean){
            focusedProperty.internalValue = focused
        }

        open fun onPointerMoveCallback(pointerId: Int, x: Int, y: Int){
            if(pointerId !in pointers) {
                if(x < 0 || y < 0 || x > sizeProperty.value.width || y > sizeProperty.value.height)
                    return
                else
                    onPointerEnterCallback(pointerId, x, y)
            }

            val pointer = pointers[pointerId] as WrappedPointer
            if(x == pointer.absoluteX && y == pointer.absoluteY)
                return

            val dpi = display.dpi
            val oldX = pointer.absoluteX.toDouble()
            val oldY = pointer.absoluteY.toDouble()

            pointer.apply {
                this.absoluteX = x
                this.absoluteY = y
                this.x = absoluteX / dpi
                this.y = absoluteY / dpi
            }

            val event = Pointer.MoveEvent(pointer, oldX / dpi, oldY / dpi, oldX, oldY)
            if(pointer.buttons.isNotEmpty())
                pointerDragListeners.forEach { it(event) }
            else
                pointerMoveListeners.forEach { it(event) }
        }

        open fun onPointerDownCallback(pointerId: Int, x: Int, y: Int, button: Int){
            if(pointerId !in pointers)
                onPointerEnterCallback(pointerId, x, y)
            val pointer = pointers[pointerId] as WrappedPointer

            val dpi = display.dpi
            val currentTime = System.currentTimeMillis()
            val isClick = (pointer.lastButton == Pointer.Button.of(button)) &&
                    (currentTime - pointer.lastReleaseTime <= DOUBLE_CLICK_DELAY) &&
                    hypot(
                        x - pointer.lastButtonX.toDouble(),
                        y - pointer.lastButtonY.toDouble()
                    ) <= DOUBLE_CLICK_RADIUS

            pointer.apply {
                this.buttons += Pointer.Button.of(button)
                this.absoluteX = x
                this.absoluteY = y
                this.x = x / dpi
                this.y = y / dpi
                lastButton = Pointer.Button.of(button)
                lastButtonX = x
                lastButtonY = y
                clicks = if(isClick) clicks + 1 else 1
            }

            Pointer.Event(pointer).apply { pointerPressListeners.forEach { it(this) } }

            if(isClick)
                Pointer.ClickEvent(pointer, pointer.clicks).apply { pointerClickListeners.forEach { it(this) } }
        }

        open fun onPointerUpCallback(pointerId: Int, x: Int, y: Int, button: Int){
            if(pointerId !in pointers)
                onPointerEnterCallback(pointerId, x, y)
            val pointer = pointers[pointerId] as WrappedPointer

            val dpi = display.dpi
            val isClick = (pointer.lastButton == Pointer.Button.of(button)) &&
                    (pointer.clicks == 1) &&
                    hypot(
                        x - pointer.lastButtonX.toDouble(),
                        y - pointer.lastButtonY.toDouble()
                    ) <= DOUBLE_CLICK_RADIUS

            pointer.apply {
                this.absoluteX = x
                this.absoluteY = y
                this.x = x / dpi
                this.y = y / dpi
                lastButtonX = x
                lastButtonY = y
                lastReleaseTime = System.currentTimeMillis()
            }

            Pointer.Event(pointer).apply { pointerReleaseListeners.forEach { it(this) } }

            if(isClick)
                Pointer.ClickEvent(pointer, pointer.clicks).apply { pointerClickListeners.forEach { it(this) } }

            pointer.buttons -= Pointer.Button.of(button)

            if(x < 0 || y < 0 || x > sizeProperty.value.width || y > sizeProperty.value.height)
                onPointerLeaveCallback(pointerId, x, y)
        }

        open fun onPointerEnterCallback(pointerId: Int, x: Int, y: Int){
            if(pointerId in pointers)
                return
            val dpi = display.dpi
            val pointer = WrappedPointer(pointerId).apply {
                this.absoluteX = x
                this.absoluteY = y
                this.x = x / dpi
                this.y = y / dpi
            }
            pointers[pointerId] = pointer

            Pointer.Event(pointer).apply { pointerEnterListeners.forEach { it(this) } }
        }

        open fun onPointerLeaveCallback(pointerId: Int, x: Int, y: Int){
            val pointer = pointers[pointerId]!!
            if(pointer.buttons.isNotEmpty())
                return
            pointers.remove(pointerId)

            Pointer.Event(pointer).apply { pointerLeaveListeners.forEach { it(this) } }
        }
    }

    class DisplayModeChangingException(
        displayMode: DisplayMode?,
        errorText: String
    ): UnsupportedOperationException(
        "Unable to set display mode " +
                "${displayMode?.size?.width?.toInt()}x${displayMode?.size?.height?.toInt()}x${displayMode?.bits} " +
                "@${displayMode?.frequency} - " +
                errorText
    )
}