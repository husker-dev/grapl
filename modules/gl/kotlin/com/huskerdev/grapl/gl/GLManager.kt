package com.huskerdev.grapl.gl

import com.huskerdev.grapl.GraplInfo
import com.huskerdev.grapl.core.platform.OS
import com.huskerdev.grapl.core.platform.Platform
import com.huskerdev.grapl.core.window.WindowPeer
import com.huskerdev.grapl.gl.platforms.linux.egl.EGLManager
import com.huskerdev.grapl.gl.platforms.linux.glx.GLXManager
import com.huskerdev.grapl.gl.platforms.macos.MacGLManager
import com.huskerdev.grapl.gl.platforms.win.WGLManager

abstract class GLManager {

    companion object {
        @JvmStatic var preferGLX = true

        init {
            Platform.loadLibraryFromResources(
                classpath = "com.huskerdev.grapl.gl.native",
                baseName = "lib",
                version = GraplInfo.VERSION
            )
        }

        val current by lazy {
            when(Platform.os) {
                OS.Windows  -> WGLManager()
                OS.Linux    -> if(preferGLX) GLXManager() else EGLManager()
                OS.MacOS    -> MacGLManager()
                else -> throw UnsupportedOperationException("Unsupported OS")
            }
        }
    }

    abstract fun supportsDebug(): Boolean

    abstract fun createContext(
        profile: GLProfile,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ): GLContext

    abstract fun createFromCurrentContext(): GLContext
    abstract fun clearContext(): Boolean

    abstract fun createGLWindowPeer(
        profile: GLProfile,
        pixelFormat: GLPixelFormat,
        shareWith: Long,
        majorVersion: Int,
        minorVersion: Int,
        debug: Boolean
    ): WindowPeer

    abstract fun swapBuffers(window: GLWindow)
    abstract fun setSwapInterval(window: GLWindow, value: Int)
}