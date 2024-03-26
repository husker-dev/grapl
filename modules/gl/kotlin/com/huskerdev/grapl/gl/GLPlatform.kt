package com.huskerdev.grapl.gl

import com.huskerdev.grapl.GraplProperties
import com.huskerdev.grapl.OS
import com.huskerdev.grapl.PlatformUtils
import com.huskerdev.grapl.gl.platforms.LinuxGLPlatform
import com.huskerdev.grapl.gl.platforms.MacGLPlatform
import com.huskerdev.grapl.gl.platforms.WinGLPlatform

abstract class GLPlatform {

    companion object {
        init {
            PlatformUtils.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.gl.native",
                baseName = "lib",
                version = GraplProperties.version
            )
        }

        val current by lazy {
            when(PlatformUtils.os) {
                OS.Windows  -> WinGLPlatform()
                OS.Linux    -> LinuxGLPlatform()
                OS.MacOS    -> MacGLPlatform()
                else -> throw UnsupportedOperationException("Unsupported OS")
            }
        }
    }

    abstract fun createContext(profile: Boolean, shareWith: Long, majorVersion: Int, minorVersion: Int): GLContext
    abstract fun createFromCurrent(): GLContext
    abstract fun makeCurrent(context: GLContext?): Boolean
    abstract fun delete(context: GLContext)
}