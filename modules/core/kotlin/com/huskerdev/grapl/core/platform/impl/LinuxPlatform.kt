package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.platform.Platform

class LinuxPlatform: Platform() {
    override val dynamicLibExtension = "so"
    override val primaryDisplay: Display
        get() = TODO("Not yet implemented")
    override val displays: Array<Display>
        get() = TODO("Not yet implemented")

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