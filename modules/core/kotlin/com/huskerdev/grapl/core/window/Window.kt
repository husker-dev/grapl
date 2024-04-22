package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.core.Size
import com.huskerdev.grapl.core.display.Display

import com.huskerdev.grapl.core.x


open class Window(
    val peer: WindowPeer
) {
    val moveListeners by peer::moveListeners
    val resizeListeners by peer::resizeListeners
    val visibleListeners by peer::visibleListeners


    var absoluteSize by peer::size
    var absoluteWidth: Int
        set(value) { peer.size = Size(value, peer.size.height) }
        get() = peer.size.width.toInt()
    var absoluteHeight: Int
        set(value) { peer.size = Size(peer.size.width, value) }
        get() = peer.size.height.toInt()

    var absoluteMinSize by peer::minSize
    var absoluteMinWidth: Int
        set(value) { peer.minSize = Size(value, peer.minSize.height) }
        get() = peer.minSize.width.toInt()
    var absoluteMinHeight: Int
        set(value) { peer.minSize = Size(peer.minSize.width, value) }
        get() = peer.minSize.height.toInt()

    var absoluteMaxSize by peer::maxSize
    var absoluteMaxWidth: Int
        set(value) { peer.maxSize = Size(value, peer.maxSize.height) }
        get() = peer.maxSize.width.toInt()
    var absoluteMaxHeight: Int
        set(value) { peer.maxSize = Size(peer.maxSize.width, value) }
        get() = peer.maxSize.height.toInt()

    var absolutePosition by peer::position
    var absoluteX: Int
        set(value) { peer.position = Size(value, peer.position.height) }
        get() = peer.position.width.toInt()
    var absoluteY: Int
        set(value) { peer.position = Size(peer.position.width, value) }
        get() = peer.position.height.toInt()


    var size: Size
        set(value) { peer.size = value * display.dpi }
        get() = peer.size / display.dpi
    var width: Double
        set(value) { peer.size = Size(value * display.dpi, peer.size.height) }
        get() = peer.size.width / display.dpi
    var height: Double
        set(value) { peer.size = Size(peer.size.width, value * display.dpi) }
        get() = peer.size.height / display.dpi

    var minSize: Size
        set(value) { peer.minSize = value * display.dpi }
        get() = peer.minSize / display.dpi
    var minWidth: Double
        set(value) { peer.minSize = Size(value * display.dpi, peer.minSize.height) }
        get() = peer.minSize.width / display.dpi
    var minHeight: Double
        set(value) { peer.minSize = Size(peer.minSize.width, value * display.dpi) }
        get() = peer.minSize.height / display.dpi

    var maxSize: Size
        set(value) { peer.maxSize = value * display.dpi }
        get() = peer.maxSize / display.dpi
    var maxWidth: Double
        set(value) { peer.maxSize = Size(value * display.dpi, peer.maxSize.height) }
        get() = peer.maxSize.width / display.dpi
    var maxHeight: Double
        set(value) { peer.maxSize = Size(peer.maxSize.width, value * display.dpi) }
        get() = peer.maxSize.height / display.dpi

    var position: Size
        set(value) { peer.position = value * display.dpi }
        get() = peer.position / display.dpi
    var x: Double
        set(value) { peer.position = Size(value * display.dpi, peer.position.height) }
        get() = peer.position.width / display.dpi
    var y: Double
        set(value) { peer.position = Size(peer.position.width, value * display.dpi) }
        get() = peer.position.height / display.dpi

    var title by peer::title

    var visible by peer::visible

    var cursor by peer::cursor

    val display by peer::display

    fun alignToCenter(){
        val displaySize = Display.primary.size
        position = (displaySize.width - width) / 2.0 x (displaySize.height - height) / 2.0
    }

    fun runEventLoop(loopCallback: () -> Unit = {}) = peer.runEventLoop(loopCallback)
    fun destroy() = peer.destroy()

    init {
        size = 800.0 x 600.0
    }
}