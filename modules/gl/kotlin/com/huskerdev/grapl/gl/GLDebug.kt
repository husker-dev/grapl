package com.huskerdev.grapl.gl

import java.text.SimpleDateFormat
import java.util.*


data class GLDebugEvent(
    val source: GLDebugSource,
    val type: GLDebugType,
    val id: Int,
    val severity: GLDebugSeverity,
    val message: String,
    val date: Long = System.currentTimeMillis()
) {
    override fun toString() =
        SimpleDateFormat("HH:mm:ss.SSS").format(Date(date)) +
                " $severity [OpenGL $source $type]: $message (error id: $id)"
}


enum class GLDebugSource {
    API,
    SHADER_COMPILER,
    WINDOW_SYSTEM,
    THIRD_PARTY,
    APPLICATION,
    OTHER;

    companion object {
        fun of(value: Int) = when(value){
            0x8246 -> API             // GL_DEBUG_SOURCE_API_ARB
            0x8248 -> SHADER_COMPILER // GL_DEBUG_SOURCE_SHADER_COMPILER_ARB
            0x8247 -> WINDOW_SYSTEM   // GL_DEBUG_SOURCE_WINDOW_SYSTEM_ARB
            0x8249 -> THIRD_PARTY     // GL_DEBUG_SOURCE_THIRD_PARTY_ARB
            0x824A -> APPLICATION     // GL_DEBUG_SOURCE_APPLICATION_ARB
            else -> OTHER
        }
    }
}

enum class GLDebugType {
    ERROR,
    DEPRECATED_BEHAVIOR,
    UNDEFINED_BEHAVIOR,
    PERFORMANCE,
    PORTABILITY,
    OTHER;

    companion object {
        fun of(value: Int) = when(value){
            0x824C -> ERROR               // GL_DEBUG_TYPE_ERROR_ARB
            0x824D -> DEPRECATED_BEHAVIOR // GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR_ARB
            0x824E -> UNDEFINED_BEHAVIOR  // GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR_ARB
            0x8250 -> PERFORMANCE         // GL_DEBUG_TYPE_PERFORMANCE_ARB
            0x824F -> PORTABILITY         // GL_DEBUG_TYPE_PORTABILITY_ARB
            else -> OTHER
        }
    }
}

enum class GLDebugSeverity {
    HIGH,
    MEDIUM,
    LOW;

    companion object {
        fun of(value: Int) = when(value){
            0x9146 -> HIGH   // GL_DEBUG_SEVERITY_HIGH_ARB
            0x9147 -> MEDIUM // GL_DEBUG_SEVERITY_MEDIUM_ARB
            0x9148 -> LOW    // GL_DEBUG_SEVERITY_LOW_ARB
            else -> MEDIUM
        }
    }
}