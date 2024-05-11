package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.WinDisplayPeer
import com.huskerdev.grapl.core.platform.Platform

class WinPlatform: Platform() {
    override val dynamicLibExtension = "dll"

    override val primaryDisplay: Display
        get() = Display(WinDisplayPeer.primary)

    override val displays: Array<Display>
        get() = WinDisplayPeer.list.map { Display(it) }.toTypedArray()

    override fun specifyLibName(libName: String) =
        "$libName-$arch.$dynamicLibExtension"

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