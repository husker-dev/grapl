package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.DisplayMode
import com.huskerdev.grapl.core.util.Property
import com.huskerdev.grapl.core.util.LinkedProperty
import com.huskerdev.grapl.core.util.ReadOnlyProperty


abstract class WindowPeer(handle: Long) {
    companion object {
        init {
            GraplNatives.load()
        }
    }

    var handle: Long = handle
        protected set

    abstract val display: Display

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

    open var cursor = Cursor.DEFAULT

    protected var shouldClose = false


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

    protected abstract fun setSizeImpl(size: Size)
    protected abstract fun setMinSizeImpl(size: Size)
    protected abstract fun setMaxSizeImpl(size: Size)
    protected abstract fun setPositionImpl(position: Position)
    protected abstract fun setDisplayStateImpl(state: WindowDisplayState)

    open inner class DefaultWindowCallback {
        fun onCloseCallback(){
            shouldClose = true
        }

        fun onResizeCallback(width: Int, height: Int){
            sizeProperty.internalValue = Size(width, height)
            viewportProperty.internalValue = if(isFullscreen()) displayStateProperty.value.size else sizeProperty.value
        }

        fun onMoveCallback(x: Int, y: Int){
            positionProperty.internalValue = Position(x, y)
        }

        fun onFocusCallback(focused: Boolean){
            focusedProperty.internalValue = focused
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