package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.core.Position
import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display

import com.huskerdev.grapl.core.x


abstract class Window(
    val peer: WindowPeer
) {
    val moveListeners by peer.positionProperty::listeners
    val resizeListeners by peer.sizeProperty::listeners
    val visibleListeners by peer.visibleProperty::listeners
    val displayStateListeners by peer.displayStateProperty::listeners
    val focusedListener by peer.focusedProperty::listeners

    val pointerMoveListeners by peer::pointerMoveListeners
    val pointerDragListeners by peer::pointerDragListeners
    val pointerDownListeners by peer::pointerPressListeners
    val pointerUpListeners by peer::pointerReleaseListeners
    val pointerClickListeners by peer::pointerClickListeners


    var absoluteSize by peer.sizeProperty::value
    var absoluteWidth: Int
        set(value) { absoluteSize = absoluteSize.withWidth(value) }
        get() = absoluteSize.width.toInt()
    var absoluteHeight: Int
        set(value) { absoluteSize = absoluteSize.withHeight(value) }
        get() = absoluteSize.height.toInt()

    var absoluteMinSize by peer.minSizeProperty::value
    var absoluteMinWidth: Int
        set(value) { absoluteMinSize = absoluteMinSize.withWidth(value) }
        get() = absoluteMinSize.width.toInt()
    var absoluteMinHeight: Int
        set(value) { absoluteMinSize = absoluteMinSize.withHeight(value) }
        get() = absoluteMinSize.height.toInt()

    var absoluteMaxSize by peer.maxSizeProperty::value
    var absoluteMaxWidth: Int
        set(value) { absoluteMaxSize = absoluteMaxSize.withWidth(value) }
        get() = absoluteMaxSize.width.toInt()
    var absoluteMaxHeight: Int
        set(value) { absoluteMaxSize = absoluteMaxSize.withHeight(value) }
        get() = absoluteMaxSize.height.toInt()

    var absolutePosition by peer.positionProperty::value
    var absoluteX: Int
        set(value) { absolutePosition = absolutePosition.withX(value) }
        get() = absolutePosition.x.toInt()
    var absoluteY: Int
        set(value) { absolutePosition = absolutePosition.withY(value) }
        get() = absolutePosition.y.toInt()


    var size: Size
        set(value) {
            absoluteSize = value * display.dpi
        }
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

    open var displayState by peer.displayStateProperty::value

    var title by peer.titleProperty::value

    var visible by peer.visibleProperty::value

    var cursor by peer.cursor::value

    val display by peer::display

    val focused by peer.focusedProperty::value

    fun alignToCenter(){
        val displaySize = Display.primary.absoluteSize
        absolutePosition = Position((displaySize.width - absoluteWidth) / 2.0, (displaySize.height - absoluteHeight) / 2.0)
    }

    fun runEventLoop(loopCallback: () -> Unit = {}) = peer.runEventLoop(loopCallback)
    fun destroy() = peer.destroy()

    init {
        size = 800.0 x 600.0
    }
}