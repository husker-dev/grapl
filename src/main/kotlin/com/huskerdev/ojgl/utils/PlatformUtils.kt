package com.huskerdev.ojgl.utils

import java.io.File
import java.io.FileOutputStream

enum class OS(val displayName: String) {
    Other("unknown"),
    Windows("windows"),
    Linux("linux"),
    MacOS("macos");
    override fun toString() = displayName
}

enum class Architecture(val displayName: String) {
    ARM64("arm64"),
    ARM32("arm32"),
    X64("x64"),
    X86("x86");
    override fun toString() = displayName
}


class PlatformUtils {

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

        val dynamicLibExt = when(os){
            OS.Other -> throw UnsupportedOperationException("Can not get library extension for unsupported OS")
            OS.Windows -> "dll"
            OS.Linux -> "so"
            OS.MacOS -> "dylib"
        }

        fun loadLibraryFromResources(path: String) {
            var fileName = path.replace("/", "-")
            if(fileName.startsWith("-")) fileName = fileName.substring(1)

            val tmpFile = File(System.getProperty("java.io.tmpdir"), fileName)
            FileOutputStream(tmpFile).use {
                this::class.java.getResourceAsStream(path)!!.copyTo(it)
            }
            System.load(tmpFile.absolutePath)
        }
    }
}