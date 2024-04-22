package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.MacDisplayPeer
import com.huskerdev.grapl.core.platform.Platform

class MacPlatform: Platform() {

    companion object {
        @JvmStatic private external fun nInvokeOnMainThread(runnable: Runnable, wait: Boolean)

        fun invokeOnMainThread(runnable: Runnable) = nInvokeOnMainThread(runnable, false)
        fun invokeOnMainThreadSync(runnable: Runnable) = nInvokeOnMainThread(runnable, true)
    }

    override val dynamicLibExtension = "dylib"

    override val primaryDisplay: Display
        get() = Display(MacDisplayPeer.primary)

    override val displays: Array<Display>
        get() = MacDisplayPeer.list.map { Display(it) }.toTypedArray()

    override fun specifyLibName(libName: String) =
        "$libName.$dynamicLibExtension"
}