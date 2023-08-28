package com.huskerdev.ojgl

import java.nio.ByteBuffer

class GLMin {

    companion object {
        @JvmStatic external fun init()

        @JvmStatic external fun glDeleteFramebuffers(fbo: Int)
        @JvmStatic external fun glDeleteRenderbuffers(rbo: Int)
        @JvmStatic external fun glDeleteTextures(texture: Int)
        @JvmStatic external fun glGenFramebuffers(): Int
        @JvmStatic external fun glGenRenderbuffers(): Int
        @JvmStatic external fun glGenTextures(): Int
        @JvmStatic external fun glBindFramebuffer(target: Int, fbo: Int)
        @JvmStatic external fun glBindRenderbuffer(target: Int, rbo: Int)
        @JvmStatic external fun glBindTexture(target: Int, texture: Int)
        @JvmStatic external fun glFramebufferTexture2D(target: Int, attachment: Int, texture: Int, texId: Int, level: Int)
        @JvmStatic external fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)
        @JvmStatic external fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbufferTarget: Int, renderbuffer: Int)
        @JvmStatic external fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: ByteBuffer)
        @JvmStatic external fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Long)
        @JvmStatic external fun glTexParameteri(target: Int, pname: Int, param: Int)
        @JvmStatic external fun glViewport(x: Int, y: Int, w: Int, h: Int)
        @JvmStatic external fun glFinish()
    }
}