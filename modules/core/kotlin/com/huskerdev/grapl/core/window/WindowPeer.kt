package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.Dimension
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.DisplayMode
import com.huskerdev.grapl.core.input.*
import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.core.platform.BackgroundMessageHandler.Companion.useHandler
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.util.*
import kotlin.math.hypot


abstract class WindowPeer() {
    companion object {
        init {
            GraplNatives.load()
        }
    }

    var handle = 0L
        protected set

    private val closeNotifier = Object()

    var shouldClose = false
        set(value) {
            field = value
            if(!value)
                return
            if (useHandler)
                BackgroundMessageHandler.removePeer(this)
            synchronized(closeNotifier) {
                closeNotifier.notifyAll()
            }
            Platform.current.postEmptyMessage()
        }

    abstract val display: Display?

    val dpi: Double
        get() = display?.dpi ?: 1.0

    /**
     * Called only if useBackgroundMessageHandler is true
     */
    var eventConsumer: WindowEventConsumer? = null

    val pointerMoveListeners = listenerSetOf<PointerMoveEvent>()
    val pointerDragListeners = listenerSetOf<PointerMoveEvent>()

    val pointerPressListeners = listenerSetOf<PointerPressEvent>()
    val pointerReleaseListeners = listenerSetOf<PointerReleaseEvent>()
    val pointerClickListeners = listenerSetOf<PointerClickEvent>()

    val pointerEnterListeners = listenerSetOf<PointerEvent>()
    val pointerLeaveListeners = listenerSetOf<PointerEvent>()

    val pointerScrollListeners = listenerSetOf<PointerScrollEvent>()

    val pointerZoomBeginListeners = listenerSetOf<PointerZoomEvent>()
    val pointerZoomListeners = listenerSetOf<PointerZoomEvent>()
    val pointerZoomEndListeners = listenerSetOf<PointerZoomEvent>()

    val pointerRotationBeginListeners = listenerSetOf<PointerRotationEvent>()
    val pointerRotationListeners = listenerSetOf<PointerRotationEvent>()
    val pointerRotationEndListeners = listenerSetOf<PointerRotationEvent>()

    val keyPressedListeners = listenerSetOf<KeyEvent>()
    val keyReleasedListeners = listenerSetOf<KeyEvent>()
    val keyTypedListeners = listenerSetOf<KeyEvent>()


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

    val dimension: Dimension
        get() = Dimension(positionProperty.value, sizeProperty.value)

    val titleProperty = Property("", ::setTitleImpl)

    val visibleProperty = Property(false, ::setVisibleImpl)

    val focusProperty = ReadOnlyProperty(false)

    val viewportProperty = ReadOnlyProperty(Size.UNDEFINED)

    val cursorProperty = Property(Cursor.DEFAULT, ::setCursorImpl)

    val pointers = hashMapOf<Int, Pointer>()
    val keys = hashSetOf<Key>()

    val minimizable = Property(true, ::setMinimizableImpl)

    val maximizable = Property(true, ::setMaximizableImpl)

    constructor(handle: Long): this() {
        this.handle = handle
    }

    init {
        if(useHandler)
            BackgroundMessageHandler.addPeer(this)
    }

    protected fun isFullscreen() = displayStateProperty.value is WindowDisplayState.Fullscreen

    fun waitForDestroy(delay: Long = 0){
        synchronized(closeNotifier){
            closeNotifier.wait(delay)
        }
    }

    abstract fun destroy()

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

    open fun dispatchUpdate(){
        eventConsumer?.dispatchUpdate()
    }

    open inner class DefaultWindowCallback {

        inner class WrappedPointer(id: Int): Pointer(id) {
            override var absoluteX = 0
            override var absoluteY = 0
            override var x = 0.0
            override var y = 0.0
            override var buttons = hashSetOf<Int>()

            var lastReleaseTime = 0L
            var lastButton: Int? = null
            var lastButtonX = 0
            var lastButtonY = 0
            var clicks = 0

            var lastZoom = 0.0
            var lastAngle = 0.0

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
            focusProperty.internalValue = focused
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

            val dpi = dpi
            val oldX = pointer.absoluteX.toDouble()
            val oldY = pointer.absoluteY.toDouble()

            pointer.updatePosition(x, y)

            val event = PointerMoveEvent(pointer, modifiers, oldX / dpi, oldY / dpi, oldX, oldY)
            if(pointer.buttons.isNotEmpty())
                pointerDragListeners.dispatch(event)
            else
                pointerMoveListeners.dispatch(event)
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
            val isClick = (pointer.lastButton == button) &&
                    (currentTime - pointer.lastReleaseTime <= DOUBLE_CLICK_DELAY) &&
                    hypot(
                        x - pointer.lastButtonX.toDouble(),
                        y - pointer.lastButtonY.toDouble()
                    ) <= DOUBLE_CLICK_RADIUS

            pointer.apply {
                updatePosition(x, y)
                this.buttons += button
                lastButton = button
                lastButtonX = x
                lastButtonY = y
                clicks = if(isClick) clicks + 1 else 1
            }

            PointerPressEvent(pointer, modifiers, button).apply { pointerPressListeners.dispatch(this) }

            if(isClick)
                PointerClickEvent(pointer, modifiers, button, pointer.clicks).apply { pointerClickListeners.dispatch(this) }
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

            val isClick = (pointer.lastButton == button) &&
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

            PointerReleaseEvent(pointer, modifiers, button)
                .apply { pointerReleaseListeners.dispatch(this) }

            if(isClick)
                PointerClickEvent(pointer, modifiers, button, pointer.clicks)
                    .apply { pointerClickListeners.dispatch(this) }

            pointer.buttons -= button

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

            PointerEvent(pointer, modifiers)
                .apply { pointerEnterListeners.dispatch(this) }
        }

        open fun onPointerLeaveCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            if(pointer.buttons.isNotEmpty())
                return
            pointers.remove(pointerId)

            PointerEvent(pointer, modifiers)
                .apply { pointerLeaveListeners.dispatch(this) }
        }

        open fun onPointerScrollCallback(
            pointerId: Int,
            x: Int,
            y: Int,
            deltaX: Double,
            deltaY: Double,
            modifiers: Int
        ){
            val pointer = pointers[pointerId] ?: return
            (pointer as WrappedPointer).updatePosition(x, y)

            PointerScrollEvent(pointer, modifiers, deltaX, deltaY)
                .apply { pointerScrollListeners.dispatch(this) }
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
            PointerZoomEvent(pointer, modifiers, 0.0, 0.0)
                .apply { pointerZoomBeginListeners.dispatch(this) }
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

            PointerZoomEvent(pointer, modifiers, pointer.lastZoom, 0.0)
                .apply { pointerZoomEndListeners.dispatch(this) }
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

            PointerZoomEvent(pointer, modifiers, pointer.lastZoom, delta)
                .apply { pointerZoomListeners.dispatch(this) }
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
            PointerRotationEvent(pointer, modifiers, 0.0, 0.0)
                .apply { pointerRotationBeginListeners.dispatch(this) }
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

            PointerRotationEvent(pointer, modifiers, pointer.lastAngle, 0.0)
                .apply { pointerRotationEndListeners.dispatch(this) }
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

            PointerRotationEvent(pointer, modifiers, pointer.lastAngle, delta)
                .apply { pointerRotationListeners.dispatch(this) }
        }

        open fun onKeyDownCallback(
            keyCode: Int,
            nativeCode: Int,
            unicodeChars: String,
            modifiers: Int
        ){
            val char = if(unicodeChars.isEmpty()) null else unicodeChars[0]
            val key = Key(keyCode, nativeCode, char)
            val event = KeyEvent(key, modifiers)

            keys.add(key)

            keyPressedListeners.dispatch(event)
            if(unicodeChars.isNotEmpty())
                keyTypedListeners.dispatch(event)
        }

        open fun onKeyUpCallback(
            keyCode: Int,
            nativeCode: Int,
            unicodeChars: String,
            modifiers: Int
        ){
            val char = if(unicodeChars.isEmpty()) null else unicodeChars[0]
            val key = Key(keyCode, nativeCode, char)
            val event = KeyEvent(key, modifiers)

            keys.remove(key)

            keyReleasedListeners.dispatch(event)
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