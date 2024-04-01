package com.huskerdev.grapl.core.window


open class Window(
    val peer: WindowPeer
) {
    val moveListeners by peer::moveListeners
    val resizeListeners by peer::resizeListeners
    val visibleListeners by peer::visibleListeners

    var x by peer::x
    var y by peer::y
    var position: Pair<Int, Int>
        get() = Pair(x, y)
        set(value) {
            x = value.first
            y = value.second
        }

    var width by peer::width
    var height by peer::height
    var size: Pair<Int, Int>
        get() = Pair(width, height)
        set(value) {
            width = value.first
            height = value.second
        }

    var title by peer::title

    var visible by peer::visible

    fun runEventLoop(loopCallback: () -> Unit = {}) = peer.runEventLoop(loopCallback)
    fun destroy() = peer.destroy()

    init {
        size = 800 x 600
    }
}

infix fun Int.x(exponent: Int): Pair<Int, Int> = Pair(this, exponent)