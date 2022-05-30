package com.huskerdev.ojgl

import com.huskerdev.ojgl.utils.PlatformUtils
import com.huskerdev.ojgl.utils.*
import java.io.FileOutputStream

abstract class GLPlatform {

    companion object {
        init {
            val arch = System.getProperty("os.arch").lowercase().run {
                if (startsWith("aarch64") || startsWith("armv8")) "arm64"
                else if (startsWith("arm")) "arm32"
                else if ("64" in this) "x64"
                else "x86"
            }

            val basename = "ojgl"
            val fileName = when(PlatformUtils.os) {
                Windows -> "${basename}-${arch}.dll"
                Linux -> "${basename}-${arch}.so"
                MacOS -> "${basename}.dylib"
                else -> throw UnsupportedOperationException("Unsupported OS")
            }

            val tmpFileName = "${System.getProperty("java.io.tmpdir")}/$fileName"
            try {
                val inputStream = this::class.java.getResourceAsStream("/com/huskerdev/ojgl/natives/$fileName")!!
                FileOutputStream(tmpFileName).use {
                    inputStream.copyTo(it)
                }
            }catch (_: Exception){}
            System.load(tmpFileName)
        }
    }
}