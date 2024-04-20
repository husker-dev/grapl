package com.huskerdev.grapl.core

import com.huskerdev.grapl.core.util.PlatformUtils

class GraplNatives {

    companion object {
        fun load(){
            PlatformUtils.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.core.native",
                baseName = "lib",
                version = GraplInfo.VERSION
            )
        }
    }
}