package com.huskerdev.grapl.core.util

import com.huskerdev.grapl.core.GraplNatives

class MacOSUtils {

    companion object {
        @JvmStatic private external fun nInvokeOnMainThread(runnable: Runnable, wait: Boolean)

        init {
            GraplNatives.load()
        }

        fun invokeOnMainThread(runnable: Runnable) = nInvokeOnMainThread(runnable, false)
        fun invokeOnMainThreadSync(runnable: Runnable) = nInvokeOnMainThread(runnable, true)
    }
}