package com.huskerdev.ojgl.gl


abstract class GLContext(
    val handle: Long,
    val majorVersion: Int,
    val minorVersion: Int
) {

    companion object {
        @JvmStatic
        fun create(
            shareWith: GLContext,
            coreProfile: Boolean = false,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = create(shareWith.handle, coreProfile, majorVersion, minorVersion)

        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: Long = 0L,
            coreProfile: Boolean = false,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = GLPlatform.current.createContext(coreProfile, shareWith, majorVersion, minorVersion)

        @JvmStatic
        fun current() = GLPlatform.current.createFromCurrent()

        @JvmStatic
        fun clear() = GLPlatform.current.makeCurrent(null)

        @JvmStatic
        fun delete(context: GLContext) = GLPlatform.current.delete(context)
    }

    fun makeCurrent() = GLPlatform.current.makeCurrent(this)
}