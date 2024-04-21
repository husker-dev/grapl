package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.platform.Platform

class MacPlatform: Platform() {
    override val dynamicLibExtension = "dylib"
    override val primaryDisplay: Display
        get() = TODO("Not yet implemented")
    override val displays: Array<Display>
        get() = TODO("Not yet implemented")

    override fun specifyLibName(libName: String) =
        "$libName.$dynamicLibExtension"
}