package com.huskerdev.grapl.core.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

val String.c_str: ByteBuffer
    get() {
        val bytes = toByteArray(StandardCharsets.UTF_8)
        val cBytes = ByteArray(bytes.size + 2)
        System.arraycopy(bytes, 0, cBytes, 0, bytes.size)
        return ByteBuffer.allocateDirect(cBytes.size).order(ByteOrder.nativeOrder()).put(cBytes)
    }

val String.c_wstr: ByteBuffer
    get() {
        val bytes = toByteArray(StandardCharsets.UTF_16LE)
        val cBytes = ByteArray(bytes.size + 2)
        System.arraycopy(bytes, 0, cBytes, 0, bytes.size)
        return ByteBuffer.allocateDirect(cBytes.size).order(ByteOrder.nativeOrder()).put(cBytes)
    }