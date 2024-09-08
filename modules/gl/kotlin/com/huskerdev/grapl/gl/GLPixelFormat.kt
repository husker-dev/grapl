package com.huskerdev.grapl.gl

data class GLPixelFormat(
    val msaa: Int                = 0,
    val doubleBuffering: Boolean = true,
    val redBits: Int             = 8,
    val greenBits: Int           = 8,
    val blueBits: Int            = 8,
    val alphaBits: Int           = 8,
    val depthBits: Int           = 24,
    val stencilBits: Int         = 8,
    val transparency: Boolean    = false
){
    companion object {
        @JvmStatic val DEFAULT = GLPixelFormat()
    }
}