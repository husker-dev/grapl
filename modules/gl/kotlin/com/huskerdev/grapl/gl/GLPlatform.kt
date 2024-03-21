package com.huskerdev.grapl.gl

import com.huskerdev.offgraph.OS
import com.huskerdev.offgraph.PlatformUtils
import com.huskerdev.grapl.gl.platforms.LinuxGLPlatform
import com.huskerdev.grapl.gl.platforms.MacGLPlatform
import com.huskerdev.grapl.gl.platforms.WinGLPlatform

abstract class GLPlatform {

    companion object {
        init {
            val basename = "lib"
            val fileName = when(PlatformUtils.os) {
                OS.Windows, OS.Linux    -> "$basename-${PlatformUtils.arch}.${PlatformUtils.dynamicLibExt}"
                OS.MacOS                -> "$basename.dylib"
                else -> throw UnsupportedOperationException("Unsupported OS")
            }
            PlatformUtils.loadLibraryFromResources("/com/huskerdev/grapl/gl/native/$fileName")
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