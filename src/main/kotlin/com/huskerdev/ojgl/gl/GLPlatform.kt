package com.huskerdev.ojgl.gl

import com.huskerdev.ojgl.gl.platforms.LinuxGLPlatform
import com.huskerdev.ojgl.gl.platforms.MacGLPlatform
import com.huskerdev.ojgl.gl.platforms.WinGLPlatform
import com.huskerdev.ojgl.gl.utils.OS
import com.huskerdev.ojgl.gl.utils.PlatformUtils

abstract class GLPlatform {

    companion object {
        init {
            val basename = "offscreen-jgl"
            val fileName = when(PlatformUtils.os) {
                OS.Windows, OS.Linux    -> "$basename-${PlatformUtils.arch}.${PlatformUtils.dynamicLibExt}"
                OS.MacOS                -> "$basename.dylib"
                else -> throw UnsupportedOperationException("Unsupported OS")
            }
            PlatformUtils.loadLibraryFromResources("/com/huskerdev/ojgl/natives/$fileName")
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