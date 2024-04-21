package com.huskerdev.grapl

import com.huskerdev.grapl.core.platform.Platform

class GraplNatives {

    companion object {
        fun load(){
            Platform.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.core.native",
                baseName = "lib",
                version = GraplInfo.VERSION
            )
        }
    }
}