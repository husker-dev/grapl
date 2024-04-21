package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.util.listenerObserver
import com.huskerdev.grapl.core.util.observer


abstract class WindowPeer {

    companion object {
        init {
            GraplNatives.load()
        }
    }

    val moveListeners = hashSetOf<() -> Unit>()
    val resizeListeners = hashSetOf<() -> Unit>()
    val visibleListeners = hashSetOf<() -> Unit>()

    protected var _position by listenerObserver(Pair(0, 0), moveListeners)
    var position: Pair<Int, Int> by observer(_position){
        setPositionImpl(it.first, it.second)
        _position = it
    }

    protected var _size by listenerObserver(Pair(100, 100), moveListeners)
    var size: Pair<Int, Int> by observer(_size){
        setSizeImpl(it.first, it.second)
        _size = it
    }

    protected var _title = ""
    var title: String by observer(_title){
        setTitleImpl(title)
        _title = it
    }

    protected var _visible by listenerObserver(false, visibleListeners)
    var visible: Boolean by observer(_visible){
        setVisibleImpl(visible)
        _visible = it
    }

    open var cursor = Cursor.DEFAULT


    abstract fun destroy()
    abstract fun peekMessages()
    abstract fun shouldClose(): Boolean

    fun runEventLoop(loopCallback: () -> Unit) {
        while (!shouldClose()) {
            loopCallback()
            peekMessages()
        }
    }

    protected abstract fun setPositionImpl(x: Int, y: Int)
    protected abstract fun setSizeImpl(width: Int, height: Int)
    protected abstract fun setTitleImpl(title: String)
    protected abstract fun setVisibleImpl(visible: Boolean)
}