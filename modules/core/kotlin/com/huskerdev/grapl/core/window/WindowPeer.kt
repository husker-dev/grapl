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

    val pointerWheelListeners = hashSetOf<(Pointer.WheelEvent) -> Unit>()

    val pointerZoomBeginListeners = hashSetOf<(Pointer.ZoomEvent) -> Unit>()
    val pointerZoomListeners = hashSetOf<(Pointer.ZoomEvent) -> Unit>()
    val pointerZoomEndListeners = hashSetOf<(Pointer.ZoomEvent) -> Unit>()

    val pointerRotationBeginListeners = hashSetOf<(Pointer.RotationEvent) -> Unit>()
    val pointerRotationListeners = hashSetOf<(Pointer.RotationEvent) -> Unit>()
    val pointerRotationEndListeners = hashSetOf<(Pointer.RotationEvent) -> Unit>()


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

            var lastZoom = 0.0
            var lastAngle = 0.0

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

            fun updatePosition(absoluteX: Int, absoluteY: Int){
                this.absoluteX = absoluteX
                this.absoluteY = absoluteX
                this.x = absoluteX / dpi
                this.y = absoluteY / dpi
            }
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

        open fun onPointerMoveCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            if(pointerId !in pointers) {
                if(x < 0 || y < 0 || x > sizeProperty.value.width || y > sizeProperty.value.height)
                    return
                else onPointerEnterCallback(pointerId, x, y, modifiers)
            }

            val pointer = pointers[pointerId] as WrappedPointer
            if(x == pointer.absoluteX && y == pointer.absoluteY)
                return

            val dpi = display.dpi
            val oldX = pointer.absoluteX.toDouble()
            val oldY = pointer.absoluteY.toDouble()

            pointer.updatePosition(x, y)

            val event = Pointer.MoveEvent(pointer, modifiers, oldX / dpi, oldY / dpi, oldX, oldY)
            if(pointer.buttons.isNotEmpty())
                pointerDragListeners.forEach { it(event) }
            else
                pointerMoveListeners.forEach { it(event) }
        }

        open fun onPointerDownCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            button: Int,
            modifiers: Int
        ){
            if(pointerId !in pointers)
                onPointerEnterCallback(pointerId, x, y, modifiers)
            val pointer = pointers[pointerId] as WrappedPointer

            val currentTime = System.currentTimeMillis()
            val isClick = (pointer.lastButton == Pointer.Button.of(button)) &&
                    (currentTime - pointer.lastReleaseTime <= DOUBLE_CLICK_DELAY) &&
                    hypot(
                        x - pointer.lastButtonX.toDouble(),
                        y - pointer.lastButtonY.toDouble()
                    ) <= DOUBLE_CLICK_RADIUS

            pointer.apply {
                updatePosition(x, y)
                this.buttons += Pointer.Button.of(button)
                lastButton = Pointer.Button.of(button)
                lastButtonX = x
                lastButtonY = y
                clicks = if(isClick) clicks + 1 else 1
            }

            Pointer.Event(pointer, modifiers).apply { pointerPressListeners.forEach { it(this) } }

            if(isClick)
                Pointer.ClickEvent(pointer, modifiers, pointer.clicks).apply { pointerClickListeners.forEach { it(this) } }
        }

        open fun onPointerUpCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            button: Int,
            modifiers: Int
        ){
            if(pointerId !in pointers)
                onPointerEnterCallback(pointerId, x, y, modifiers)
            val pointer = pointers[pointerId] as WrappedPointer

            val isClick = (pointer.lastButton == Pointer.Button.of(button)) &&
                    (pointer.clicks == 1) &&
                    hypot(
                        x - pointer.lastButtonX.toDouble(),
                        y - pointer.lastButtonY.toDouble()
                    ) <= DOUBLE_CLICK_RADIUS

            pointer.apply {
                updatePosition(x, y)
                lastButtonX = x
                lastButtonY = y
                lastReleaseTime = System.currentTimeMillis()
            }

            Pointer.Event(pointer, modifiers)
                .apply { pointerReleaseListeners.forEach { it(this) } }

            if(isClick)
                Pointer.ClickEvent(pointer, modifiers, pointer.clicks)
                    .apply { pointerClickListeners.forEach { it(this) } }

            pointer.buttons -= Pointer.Button.of(button)

            if(x < 0 || y < 0 || x > sizeProperty.value.width || y > sizeProperty.value.height)
                onPointerLeaveCallback(pointerId, x, y, modifiers)
        }

        open fun onPointerEnterCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            if(pointerId in pointers)
                return
            val pointer = WrappedPointer(pointerId)
            pointer.updatePosition(x, y)
            pointers[pointerId] = pointer

            Pointer.Event(pointer, modifiers)
                .apply { pointerEnterListeners.forEach { it(this) } }
        }

        open fun onPointerLeaveCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            val pointer = pointers[pointerId]!!
            if(pointer.buttons.isNotEmpty())
                return
            pointers.remove(pointerId)

            Pointer.Event(pointer, modifiers)
                .apply { pointerLeaveListeners.forEach { it(this) } }
        }

        open fun onPointerWheelCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            deltaX: Double,
            deltaY: Double,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            (pointer as WrappedPointer).updatePosition(x, y)

            Pointer.WheelEvent(pointer, modifiers, deltaX, deltaY)
                .apply { pointerWheelListeners.forEach { it(this) } }
        }

        open fun onPointerZoomBeginCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            (pointer as WrappedPointer).apply {
                updatePosition(x, y)
                lastZoom = 0.0
            }
            Pointer.ZoomEvent(pointer, modifiers, 0.0, 0.0)
                .apply { pointerZoomBeginListeners.forEach { it(this) } }
        }

        open fun onPointerZoomEndCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            pointer as WrappedPointer
            pointer.updatePosition(x, y)

            Pointer.ZoomEvent(pointer, modifiers, pointer.lastZoom, 0.0)
                .apply { pointerZoomEndListeners.forEach { it(this) } }
        }

        open fun onPointerZoomCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            zoom: Double,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            pointer as WrappedPointer
            pointer.updatePosition(x, y)

            val delta = zoom - pointer.lastZoom
            pointer.lastZoom = zoom

            Pointer.ZoomEvent(pointer, modifiers, pointer.lastZoom, delta)
                .apply { pointerZoomListeners.forEach { it(this) } }
        }

        open fun onPointerRotationBeginCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            (pointer as WrappedPointer).apply {
                updatePosition(x, y)
                lastAngle = 0.0
            }
            Pointer.RotationEvent(pointer, modifiers, 0.0, 0.0)
                .apply { pointerRotationBeginListeners.forEach { it(this) } }
        }

        open fun onPointerRotationEndCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            pointer as WrappedPointer
            pointer.updatePosition(x, y)

            Pointer.RotationEvent(pointer, modifiers, pointer.lastAngle, 0.0)
                .apply { pointerRotationEndListeners.forEach { it(this) } }
        }

        open fun onPointerRotationCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            angle: Double,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            pointer as WrappedPointer
            pointer.updatePosition(x, y)

            val delta = angle - pointer.lastAngle
            pointer.lastAngle = angle

            Pointer.RotationEvent(pointer, modifiers, pointer.lastAngle, delta)
                .apply { pointerRotationListeners.forEach { it(this) } }
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