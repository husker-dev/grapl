package com.huskerdev.grapl.core.platform

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.platform.impl.LinuxPlatform
import com.huskerdev.grapl.core.platform.impl.MacPlatform
import com.huskerdev.grapl.core.platform.impl.WinPlatform
import java.io.File
import java.io.FileOutputStream


abstract class Platform {
    companion object {

        val os = System.getProperty("os.name", "generic").lowercase().run {
            return@run if ("mac" in this || "darwin" in this) OS.MacOS
            else if ("win" in this) OS.Windows
            else if ("nux" in this || "nix" in this || "aix" in this) OS.Linux
            else OS.Other
        }

        val arch = System.getProperty("os.arch").lowercase().run {
            if (startsWith("aarch64") || startsWith("armv8")) Architecture.ARM64
            else if (startsWith("arm")) Architecture.ARM32
            else if ("64" in this) Architecture.X64
            else Architecture.X86
        }

        val current = when(os) {
            OS.Windows -> WinPlatform()
            OS.Linux -> LinuxPlatform()
            OS.MacOS -> MacPlatform()
            OS.Other -> throw UnsupportedOperationException("Unsupported platform")
        }

        private val loadedLibs = hashSetOf<String>()

        fun loadLibraryFromResources(path: String) {
            if(path in loadedLibs)
                return

            var fileName = path.replace("/", "-")
            if(fileName.startsWith("-"))
                fileName = fileName.substring(1)

            val tmpFile = File(System.getProperty("java.io.tmpdir"), fileName)
            try {
                FileOutputStream(tmpFile).use {
                    this::class.java.getResourceAsStream(path)!!.copyTo(it)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            tmpFile.deleteOnExit()
            System.load(tmpFile.absolutePath)
            loadedLibs.add(path)
        }

        fun loadLibraryFromResources(classpath: String, baseName: String, version: String) {
            val fileName = current.specifyLibName("$baseName-$version")
            val path = classpath.replace(".", "/")
            loadLibraryFromResources("/$path/$fileName")
        }

        init {
            GraplNatives.load()
        }
    }

    abstract val dynamicLibExtension: String

    abstract val primaryDisplay: Display

    abstract val displays: Array<Display>

    internal abstract fun specifyLibName(libName: String): String

    abstract fun peekMessages()
    abstract fun waitMessages(timeout: Int = -1)
    abstract fun postEmptyMessage()
}