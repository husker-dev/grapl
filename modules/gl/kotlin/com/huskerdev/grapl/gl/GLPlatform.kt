package com.huskerdev.grapl.gl

import com.huskerdev.grapl.GraplInfo
import com.huskerdev.grapl.core.platform.OS
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.window.WindowPeer
import com.huskerdev.grapl.gl.platforms.linux.LinuxGLPlatform
import com.huskerdev.grapl.gl.platforms.macos.MacGLPlatform
import com.huskerdev.grapl.gl.platforms.win.WinGLPlatform

abstract class GLPlatform {

    companion object {
        init {
            Platform.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.gl.native",
                baseName = "lib",
                version = GraplInfo.VERSION
            )
        }

        val current by lazy {
            when(Platform.os) {
                OS.Windows  -> WinGLPlatform()
                OS.Linux    -> LinuxGLPlatform()
                OS.MacOS    -> MacGLPlatform()
                else -> throw UnsupportedOperationException("Unsupported OS")
            }
        }
    }

    abstract fun createContext(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int): GLContext
    abstract fun createFromCurrentContext(): GLContext
    abstract fun clearContext(): Boolean

    abstract fun createGLWindowPeer(profile: GLProfile, shareWith: Long, majorVersion: Int, minorVersion: Int): WindowPeer

    abstract fun swapBuffers(window: GLWindow)
    abstract fun setSwapInterval(window: GLWindow, value: Int)
}