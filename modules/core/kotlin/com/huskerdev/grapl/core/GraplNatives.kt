package com.huskerdev.grapl.core

import com.huskerdev.grapl.core.util.PlatformUtils

class GraplNatives {

    companion object {
        private var libLoaded = false

        fun load(){
            if(libLoaded) return
            else libLoaded = true

            PlatformUtils.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.core.native",
                baseName = "lib",
                version = GraplInfo.VERSION
            )
        }
    }
}