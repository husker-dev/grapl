package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.X11DisplayPeer
import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.window.WindowPeer
import com.huskerdev.grapl.core.window.impl.X11WindowPeer


class LinuxPlatform: Platform() {
    companion object {
        val windowingSystem: WindowingSystem by lazy {
            if ("WAYLAND_DISPLAY" in System.getenv())
                X11() else X11()
        }
    }

    override fun specifyLibName(libName: String) =
        "$libName-$arch.so"

    override fun createWindowPeer() =
        windowingSystem.createWindowPeer()

    override val primaryDisplay: Display
        get() = windowingSystem.primaryDisplay
    override val displays: Array<Display>
        get() = windowingSystem.displays

    override fun peekMessages() = windowingSystem.peekMessages()
    override fun waitMessages(timeout: Int) = windowingSystem.waitMessages(timeout)
    override fun postEmptyMessage() = windowingSystem.postEmptyMessage()
}

interface WindowingSystem {
    val primaryDisplay: Display
    val displays: Array<Display>

    fun createWindowPeer(): LinuxWindowPeer

    fun peekMessages()
    fun waitMessages(timeout: Int)
    fun postEmptyMessage()
}

abstract class LinuxWindowPeer: WindowPeer() {
    var display: Long = 0L
        protected set
}

class X11: WindowingSystem {
    companion object {
        @JvmStatic private external fun nXOpenDisplay(): Long

        @JvmStatic private external fun nPeekMessage(display: Long)
        @JvmStatic private external fun nWaitMessage(display: Long, timeout: Int)
        @JvmStatic private external fun nPostEmptyMessage(display: Long, window: Long)
    }

    val display = nXOpenDisplay()

    override val primaryDisplay = Display(X11DisplayPeer.primary(display))
    override val displays = X11DisplayPeer.list(display).map { Display(it) }.toTypedArray()

    override fun createWindowPeer() = BackgroundMessageHandler.invokeWaiting {
        X11WindowPeer()
    }

    override fun peekMessages() = nPeekMessage(display)
    override fun waitMessages(timeout: Int) = nWaitMessage(display, timeout)
    override fun postEmptyMessage() {
        if(BackgroundMessageHandler.activePeers.isNotEmpty())
            nPostEmptyMessage(display, BackgroundMessageHandler.activePeers.iterator().next().handle)
    }
}

class Wayland: WindowingSystem {
    companion object {
        @JvmStatic private external fun nConnectDisplay(): Long
    }

    val display = nConnectDisplay()

    init {

    }

    override val primaryDisplay: Display
        get() = TODO("Not yet implemented")
    override val displays: Array<Display>
        get() = TODO("Not yet implemented")

    override fun createWindowPeer(): LinuxWindowPeer {
        TODO("Not yet implemented")
    }

    override fun peekMessages() {
        TODO("Not yet implemented")
    }

    override fun waitMessages(timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun postEmptyMessage() {
        TODO("Not yet implemented")
    }

}