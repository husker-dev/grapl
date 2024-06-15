package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.input.*
import com.huskerdev.grapl.core.platform.Platform

import com.huskerdev.grapl.core.x


abstract class Window(
    val peer: WindowPeer
) {
    companion object {
        fun peekMessages() = Platform.current.peekMessages()
        fun waitMessages(timeout: Int = -1) = Platform.current.waitMessages(timeout)
        fun postEmptyMessage() = Platform.current.postEmptyMessage()
    }

    var onUpdate: () -> Unit
        get() = peer.onUpdate
        set(value) { peer.onUpdate = value }

    var onInit: () -> Unit
        get() = peer.onInit
        set(value) { peer.onInit = value }

    val moveListeners: HashSet<() -> Unit>
        get() = peer.positionProperty.listeners

    val resizeListeners: HashSet<() -> Unit>
        get() = peer.sizeProperty.listeners

    val visibleListeners: HashSet<() -> Unit>
        get() = peer.visibleProperty.listeners

    val displayStateListeners: HashSet<() -> Unit>
        get() = peer.displayStateProperty.listeners

    val focusedListener: HashSet<() -> Unit>
        get() = peer.focusProperty.listeners

    val pointerMoveListeners: HashSet<(PointerMoveEvent) -> Unit>
        get() = peer.pointerMoveListeners

    val pointerDragListeners: HashSet<(PointerMoveEvent) -> Unit>
        get() = peer.pointerDragListeners

    val pointerDownListeners: HashSet<(PointerEvent) -> Unit>
        get() = peer.pointerPressListeners

    val pointerUpListeners: HashSet<(PointerEvent) -> Unit>
        get() = peer.pointerReleaseListeners

    val pointerClickListeners: HashSet<(PointerClickEvent) -> Unit>
        get() = peer.pointerClickListeners

    val pointerEnterListeners: HashSet<(PointerEvent) -> Unit>
        get() = peer.pointerEnterListeners

    val pointerLeaveListeners: HashSet<(PointerEvent) -> Unit>
        get() = peer.pointerLeaveListeners

    val pointerScrollListeners: HashSet<(PointerScrollEvent) -> Unit>
        get() = peer.pointerScrollListeners

    val pointerZoomBeginListeners: HashSet<(PointerZoomEvent) -> Unit>
        get() = peer.pointerZoomBeginListeners

    val pointerZoomListeners: HashSet<(PointerZoomEvent) -> Unit>
        get() = peer.pointerZoomListeners

    val pointerZoomEndListeners: HashSet<(PointerZoomEvent) -> Unit>
        get() = peer.pointerZoomEndListeners

    val pointerRotationBeginListeners: HashSet<(PointerRotationEvent) -> Unit>
        get() = peer.pointerRotationBeginListeners

    val pointerRotationListeners: HashSet<(PointerRotationEvent) -> Unit>
        get() = peer.pointerRotationListeners

    val pointerRotationEndListeners: HashSet<(PointerRotationEvent) -> Unit>
        get() = peer.pointerRotationEndListeners

    val keyPressedListeners: HashSet<(KeyEvent) -> Unit>
        get() = peer.keyPressedListeners
    val keyReleasedListeners: HashSet<(KeyEvent) -> Unit>
        get() = peer.keyReleasedListeners
    val keyTypedListeners: HashSet<(KeyEvent) -> Unit>
        get() = peer.keyTypedListeners

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
        set(value) { absoluteSize = value * display.dpi }
        get() = absoluteSize / display.dpi
    var width: Double
        set(value) { absoluteWidth = (value * display.dpi).toInt() }
        get() = absoluteWidth / display.dpi
    var height: Double
        set(value) { absoluteHeight = (value * display.dpi).toInt() }
        get() = absoluteHeight / display.dpi

    var minSize: Size
        set(value) { absoluteMinSize = value * display.dpi }
        get() = absoluteMinSize / display.dpi
    var minWidth: Double
        set(value) { absoluteMinWidth = (value * display.dpi).toInt() }
        get() = absoluteMinWidth / display.dpi
    var minHeight: Double
        set(value) { absoluteMinHeight = (value * display.dpi).toInt() }
        get() = absoluteMinHeight / display.dpi

    var maxSize: Size
        set(value) { absoluteMaxSize = value * display.dpi }
        get() = absoluteMaxSize / display.dpi
    var maxWidth: Double
        set(value) { absoluteMaxWidth = (value * display.dpi).toInt() }
        get() = absoluteMaxWidth / display.dpi
    var maxHeight: Double
        set(value) { absoluteMaxHeight = (value * display.dpi).toInt() }
        get() = absoluteMaxHeight / display.dpi

    var position: Position
        set(value) { absolutePosition = value * display.dpi }
        get() = absolutePosition / display.dpi
    var x: Double
        set(value) { absoluteX = (value * display.dpi).toInt() }
        get() = absoluteX / display.dpi
    var y: Double
        set(value) { absoluteY = (value * display.dpi).toInt() }
        get() = absoluteY / display.dpi

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
        get() = peer.cursor.value
        set(value) { peer.cursor.value = value }

    val display: Display
        get() = peer.display

    val focused: Boolean
        get() = peer.focusProperty.value

    var maximizable: Boolean
        get() = peer.maximizable.value
        set(value) { peer.maximizable.value = value }

    var minimizable: Boolean
        get() = peer.minimizable.value
        set(value) { peer.minimizable.value = value }

    val pointer: Set<Pointer>
        get() = peer.pointers.values.toSet()

    val keys: Set<Key>
        get() = peer.keys

    fun alignToCenter(){
        val displaySize = Display.primary.absoluteSize
        absolutePosition = Position((displaySize.width - absoluteWidth) / 2.0, (displaySize.height - absoluteHeight) / 2.0)
    }

    fun destroy() = peer.destroy()

    init {
        size = 800.0 x 600.0
    }
}