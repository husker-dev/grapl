package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.input.*
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.util.ListenerArgSet
import com.huskerdev.grapl.core.util.ListenerSet

import com.huskerdev.grapl.core.x


abstract class Window(
    val peer: WindowPeer
) {
    companion object {
        fun peekMessages() = Platform.current.peekMessages()
        fun waitMessages(timeout: Int = -1) = Platform.current.waitMessages(timeout)
        fun postEmptyMessage() = Platform.current.postEmptyMessage()
    }

    var eventConsumer: WindowEventConsumer?
        get() = peer.eventConsumer
        set(value) { peer.eventConsumer = value }

    val moveListeners: ListenerSet
        get() = peer.positionProperty.listeners

    val resizeListeners: ListenerSet
        get() = peer.sizeProperty.listeners

    val visibleListeners: ListenerSet
        get() = peer.visibleProperty.listeners

    val displayListeners: ListenerSet
        get() = peer.displayProperty.listeners

    val displayStateListeners: ListenerSet
        get() = peer.displayStateProperty.listeners

    val focusedListener: ListenerSet
        get() = peer.focusProperty.listeners

    val pointerMoveListeners: ListenerArgSet<PointerMoveEvent>
        get() = peer.pointerMoveListeners

    val pointerDragListeners: ListenerArgSet<PointerMoveEvent>
        get() = peer.pointerDragListeners

    val pointerDownListeners: ListenerArgSet<PointerPressEvent>
        get() = peer.pointerPressListeners

    val pointerUpListeners: ListenerArgSet<PointerReleaseEvent>
        get() = peer.pointerReleaseListeners

    val pointerClickListeners: ListenerArgSet<PointerClickEvent>
        get() = peer.pointerClickListeners

    val pointerEnterListeners: ListenerArgSet<PointerEvent>
        get() = peer.pointerEnterListeners

    val pointerLeaveListeners: ListenerArgSet<PointerEvent>
        get() = peer.pointerLeaveListeners

    val pointerScrollListeners: ListenerArgSet<PointerScrollEvent>
        get() = peer.pointerScrollListeners

    val pointerZoomBeginListeners: ListenerArgSet<PointerZoomEvent>
        get() = peer.pointerZoomBeginListeners

    val pointerZoomListeners: ListenerArgSet<PointerZoomEvent>
        get() = peer.pointerZoomListeners

    val pointerZoomEndListeners: ListenerArgSet<PointerZoomEvent>
        get() = peer.pointerZoomEndListeners

    val pointerRotationBeginListeners: ListenerArgSet<PointerRotationEvent>
        get() = peer.pointerRotationBeginListeners

    val pointerRotationListeners: ListenerArgSet<PointerRotationEvent>
        get() = peer.pointerRotationListeners

    val pointerRotationEndListeners: ListenerArgSet<PointerRotationEvent>
        get() = peer.pointerRotationEndListeners

    val keyPressedListeners: ListenerArgSet<KeyEvent>
        get() = peer.keyPressedListeners
    val keyReleasedListeners: ListenerArgSet<KeyEvent>
        get() = peer.keyReleasedListeners
    val keyTypedListeners: ListenerArgSet<KeyEvent>
        get() = peer.keyTypedListeners

    val dpiChangedListener: ListenerSet
        get() = peer.dpiProperty.listeners

    val shouldClose: Boolean
        get() = peer.shouldClose

    var absoluteSize: Size
        get() = peer.sizeProperty.value
        set(value) { peer.sizeProperty.value = value }
    var absoluteWidth: Int
        set(value) { absoluteSize = absoluteSize.withWidth(value) }
        get() = absoluteSize.width.toInt()
    var absoluteHeight: Int
        set(value) { absoluteSize = absoluteSize.withHeight(value) }
        get() = absoluteSize.height.toInt()

    var absoluteMinSize: Size
        get() = peer.minSizeProperty.value
        set(value) { peer.minSizeProperty.value = value }
    var absoluteMinWidth: Int
        set(value) { absoluteMinSize = absoluteMinSize.withWidth(value) }
        get() = absoluteMinSize.width.toInt()
    var absoluteMinHeight: Int
        set(value) { absoluteMinSize = absoluteMinSize.withHeight(value) }
        get() = absoluteMinSize.height.toInt()

    var absoluteMaxSize: Size
        get() = peer.maxSizeProperty.value
        set(value) { peer.maxSizeProperty.value = value }
    var absoluteMaxWidth: Int
        set(value) { absoluteMaxSize = absoluteMaxSize.withWidth(value) }
        get() = absoluteMaxSize.width.toInt()
    var absoluteMaxHeight: Int
        set(value) { absoluteMaxSize = absoluteMaxSize.withHeight(value) }
        get() = absoluteMaxSize.height.toInt()

    var absolutePosition: Position
        get() = peer.positionProperty.value
        set(value) { peer.positionProperty.value = value }
    var absoluteX: Int
        set(value) { absolutePosition = absolutePosition.withX(value) }
        get() = absolutePosition.x.toInt()
    var absoluteY: Int
        set(value) { absolutePosition = absolutePosition.withY(value) }
        get() = absolutePosition.y.toInt()

    var size: Size
        set(value) { absoluteSize = value * dpi }
        get() = absoluteSize / dpi
    var width: Double
        set(value) { absoluteWidth = (value * dpi).toInt() }
        get() = absoluteWidth / dpi
    var height: Double
        set(value) { absoluteHeight = (value * dpi).toInt() }
        get() = absoluteHeight / dpi

    var minSize: Size
        set(value) { absoluteMinSize = value * dpi }
        get() = absoluteMinSize / dpi
    var minWidth: Double
        set(value) { absoluteMinWidth = (value * dpi).toInt() }
        get() = absoluteMinWidth / dpi
    var minHeight: Double
        set(value) { absoluteMinHeight = (value * dpi).toInt() }
        get() = absoluteMinHeight / dpi

    var maxSize: Size
        set(value) { absoluteMaxSize = value * dpi }
        get() = absoluteMaxSize / dpi
    var maxWidth: Double
        set(value) { absoluteMaxWidth = (value * dpi).toInt() }
        get() = absoluteMaxWidth / dpi
    var maxHeight: Double
        set(value) { absoluteMaxHeight = (value * dpi).toInt() }
        get() = absoluteMaxHeight / dpi

    var position: Position
        set(value) { absolutePosition = value * dpi }
        get() = absolutePosition / dpi
    var x: Double
        set(value) { absoluteX = (value * dpi).toInt() }
        get() = absoluteX / dpi
    var y: Double
        set(value) { absoluteY = (value * dpi).toInt() }
        get() = absoluteY / dpi

    open var displayState: WindowDisplayState
        get() = peer.displayStateProperty.value
        set(value) { peer.displayStateProperty.value = value }

    var title: String
        get() = peer.titleProperty.value
        set(value) { peer.titleProperty.value = value }

    var visible: Boolean
        get() = peer.visibleProperty.value
        set(value) { peer.visibleProperty.value = value }

    var cursor: Cursor
        get() = peer.cursorProperty.value
        set(value) { peer.cursorProperty.value = value }

    val display: Display
        get() = peer.displayProperty.value

    val dpi: Double
        get() = peer.dpiProperty.value

    var enabled: Boolean
        get() = peer.enabledProperty.value
        set(value) { peer.enabledProperty.value = value }

    val focused: Boolean
        get() = peer.focusProperty.value

    var maximizable: Boolean
        get() = peer.maximizable.value
        set(value) { peer.maximizable.value = value }

    var minimizable: Boolean
        get() = peer.minimizable.value
        set(value) { peer.minimizable.value = value }

    var closable: Boolean
        get() = peer.closable.value
        set(value) { peer.closable.value = value }

    var resizable: Boolean
        get() = peer.resizable.value
        set(value) { peer.resizable.value = value }

    val pointer: Set<Pointer>
        get() = peer.pointers.values.toSet()

    val keys: Set<Key>
        get() = peer.keys

    var style: WindowStyle
        get() = peer.styleProperty.value
        set(value) { peer.styleProperty.value = value }

    fun alignToCenter(){
        val displaySize = Display.primary.absoluteSize
        absolutePosition = Position((displaySize.width - absoluteWidth) / 2.0, (displaySize.height - absoluteHeight) / 2.0)
    }

    @JvmOverloads
    fun waitForDestroy(delay: Long = 0) = peer.waitForDestroy(delay)

    fun destroy() = peer.destroy()

    fun requestFocus() = peer.requestFocus()

    init {
        size = 800.0 x 600.0
    }
}