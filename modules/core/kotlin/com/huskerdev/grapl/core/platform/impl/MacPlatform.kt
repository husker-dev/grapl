package com.huskerdev.grapl.core.platform.impl

import com.huskerdev.grapl.core.Theme
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.display.impl.MacDisplayPeer
import com.huskerdev.grapl.core.platform.Platform

class MacPlatform: Platform() {

    companion object {
        @JvmStatic private external fun nInvokeOnMainThread(runnable: Runnable, wait: Boolean)
        @JvmStatic private external fun nPeekMessage()
        @JvmStatic private external fun nWaitMessage(timeout: Int)
        @JvmStatic private external fun nPostEmptyMessage()
        @JvmStatic private external fun nSetTheme(theme: Int)

        fun invokeOnMainThread(runnable: Runnable) = nInvokeOnMainThread(runnable, false)
        fun invokeOnMainThreadSync(runnable: Runnable) = nInvokeOnMainThread(runnable, true)
    }

    var theme: Theme = Theme.LIGHT
        set(value) {
            field = value
            nSetTheme(when(value){
                Theme.AUTO -> 0
                Theme.LIGHT -> 1
                Theme.DARK -> 2
            })
        }

    override val primaryDisplay: Display
        get() = Display(MacDisplayPeer.primary)

    override val displays: Array<Display>
        get() = MacDisplayPeer.list.map { Display(it) }.toTypedArray()

    override fun specifyLibName(libName: String) =
        "$libName.dylib"

    override fun peekMessages() = nPeekMessage()

    override fun waitMessages(timeout: Int) = nWaitMessage(timeout)

    override fun postEmptyMessage() = nPostEmptyMessage()
}