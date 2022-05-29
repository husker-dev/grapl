package com.huskerdev.ojgl


import com.huskerdev.ojgl.platforms.*
import com.huskerdev.ojgl.utils.*
import com.huskerdev.ojgl.utils.PlatformUtils

abstract class GLContext(
    val context: Long
) {

    companion object {

        @JvmField var CORE_PROFILE = true
        @JvmField var COMPATIBILITY_PROFILE = false

        @JvmStatic
        fun createNew(profile: Boolean, shareWith: GLContext) = createNew(profile, shareWith.context)

        @JvmStatic
        @JvmOverloads
        fun createNew(profile: Boolean, shareWith: Long = 0L): GLContext = when(PlatformUtils.os){
            Windows -> WinGLPlatform.createContext(profile, shareWith)
            Linux -> LinuxGLPlatform.createContext(profile, shareWith)
            MacOS -> MacGLPlatform.createContext(profile, shareWith)
            else -> throw UnsupportedOperationException("Unsupported OS")
        }

        @JvmStatic
        fun fromCurrent() = when(PlatformUtils.os){
            Windows -> WinGLPlatform.fromCurrent()
            Linux -> LinuxGLPlatform.fromCurrent()
            MacOS -> MacGLPlatform.fromCurrent()
            else -> throw UnsupportedOperationException("Unsupported OS")
        }

        @JvmStatic
        fun clearCurrent() = when(PlatformUtils.os){
            Windows -> WinGLPlatform.clearCurrent()
            Linux -> LinuxGLPlatform.clearCurrent()
            MacOS -> MacGLPlatform.clearCurrent()
            else -> throw UnsupportedOperationException("Unsupported OS")
        }
    }

    abstract fun makeCurrent(): Boolean
}