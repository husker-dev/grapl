package com.huskerdev.grapl.core.window

import com.huskerdev.grapl.core.GraplInfo
import com.huskerdev.grapl.core.util.PlatformUtils
import com.huskerdev.grapl.core.util.listenerObserver
import com.huskerdev.grapl.core.util.observer


abstract class WindowPeer {

    companion object {
        init {
            PlatformUtils.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.core.native",
                baseName = "lib",
                version = GraplInfo.VERSION
            )
        }
    }

    val moveListeners = hashSetOf<() -> Unit>()
    val resizeListeners = hashSetOf<() -> Unit>()
    val visibleListeners = hashSetOf<() -> Unit>()

    protected var _x by listenerObserver(Integer.MAX_VALUE, moveListeners)
    var x: Int by observer(_x){
        setPositionImpl(x, y)
        _x = it
    }

    protected var _y by listenerObserver(Integer.MAX_VALUE, moveListeners)
    var y: Int by observer(_y){
        setPositionImpl(x, y)
        _y = it
    }

    protected var _width by listenerObserver(0, resizeListeners)
    var width: Int by observer(_width){
        setSizeImpl(width, height)
        _width = it
    }

    protected var _height by listenerObserver(0, resizeListeners)
    var height: Int by observer(_height){
        setSizeImpl(width, height)
        _height = it
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

    var cursor = Cursor.DEFAULT

    abstract fun runEventLoop(loopCallback: () -> Unit)
    abstract fun destroy()

    protected abstract fun setPositionImpl(x: Int, y: Int)
    protected abstract fun setSizeImpl(width: Int, height: Int)
    protected abstract fun setTitleImpl(title: String)
    protected abstract fun setVisibleImpl(visible: Boolean)
}