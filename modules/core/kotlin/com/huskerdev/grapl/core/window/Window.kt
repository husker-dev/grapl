package com.huskerdev.grapl.core.window


open class Window(
    val peer: WindowPeer
) {
    val moveListeners by peer::moveListeners
    val resizeListeners by peer::resizeListeners
    val visibleListeners by peer::visibleListeners

    var position by peer::position
    var x: Int
        set(value) { position = Pair(value, y) }
        get() = position.first
    var y: Int
        set(value) { position = Pair(x, value) }
        get() = position.second

    var size by peer::size
    var width: Int
        set(value) { size = Pair(value, height) }
        get() = size.first
    var height: Int
        set(value) { size = Pair(width, value) }
        get() = size.second

    var title by peer::title

    var visible by peer::visible

    var cursor by peer::cursor

    fun runEventLoop(loopCallback: () -> Unit = {}) = peer.runEventLoop(loopCallback)
    fun destroy() = peer.destroy()

    init {
        size = 800 to 600
    }
}