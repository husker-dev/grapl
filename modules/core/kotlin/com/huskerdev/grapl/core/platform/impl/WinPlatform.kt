package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.WinDisplayPeer
import com.huskerdev.grapl.core.input.*
import com.huskerdev.grapl.core.platform.BackgroundMessageHandler
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.window.impl.WinWindowPeer

class WinPlatform: Platform() {
    companion object {
        @JvmStatic private external fun nPeekMessage()
        @JvmStatic private external fun nWaitMessage(timeout: Int)
        @JvmStatic private external fun nPostEmptyMessage()
    }

    override val primaryDisplay: Display
        get() = Display(WinDisplayPeer.primary)

    override val displays: Array<Display>
        get() = WinDisplayPeer.list.map { Display(it) }.toTypedArray()

    override fun specifyLibName(libName: String) =
        "$libName-$arch.dll"

    override fun createWindowPeer() = BackgroundMessageHandler.invokeWaiting {
        WinWindowPeer()
    }

    override fun peekMessages() = nPeekMessage()

    override fun waitMessages(timeout: Int) = nWaitMessage(timeout)

    override fun postEmptyMessage() = nPostEmptyMessage()

    override fun getVirtualKeyName(keyCode: Int) = when(keyCode){
        VK_LEFT_SUPER -> "win"
        VK_RIGHT_SUPER -> "right win"
        else -> super.getVirtualKeyName(keyCode)
    }
}