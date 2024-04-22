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
        get() = peer.size.width
    var absoluteHeight: Int
        set(value) { peer.size = Size(peer.size.width, value) }
        get() = peer.size.height

    var absolutePosition by peer::position
    var absoluteX: Int
        set(value) { peer.position = Size(value, peer.position.height) }
        get() = peer.position.width
    var absoluteY: Int
        set(value) { peer.position = Size(peer.position.width, value) }
        get() = peer.position.height


    var position: Size<Double, Double>
        set(value) {
            peer.position = (value.width * display.dpi).toInt() x (value.height * display.dpi).toInt()
        }
        get() = x x y
    var x: Double
        set(value) {
            peer.position = Size((value * display.dpi).toInt(), peer.position.height)
        }
        get() = peer.position.width / display.dpi
    var y: Double
        set(value) {
            peer.position = Size(peer.position.width, (value * display.dpi).toInt())
        }
        get() = peer.position.height / display.dpi

    var size: Size<Double, Double>
        set(value) {
            peer.size = (value.width * display.dpi).toInt() x (value.height * display.dpi).toInt()
        }
        get() = width x height
    var width: Double
        set(value) {
            peer.size = Size((value * display.dpi).toInt(), peer.size.height)
        }
        get() = peer.size.width / display.dpi
    var height: Double
        set(value) {
            peer.size = Size(peer.size.width, (value * display.dpi).toInt())
        }
        get() = peer.size.height / display.dpi

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