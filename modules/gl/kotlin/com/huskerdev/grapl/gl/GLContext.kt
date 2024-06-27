package com.huskerdev.grapl.gl


abstract class GLContext(
    val handle: Long,
    val majorVersion: Int,
    val minorVersion: Int
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: GLContext,
            coreProfile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = create(shareWith.handle, coreProfile, majorVersion, minorVersion)

        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: Long = 0L,
            coreProfile: GLProfile = GLProfile.CORE,
            majorVersion: Int = -1,
            minorVersion: Int = -1,
        ) = GLPlatform.current.createContext(coreProfile, shareWith, majorVersion, minorVersion)

        @JvmStatic
        fun current() = GLPlatform.current.createFromCurrentContext()

        @JvmStatic
        fun clear() = GLPlatform.current.clearContext()
    }

    abstract fun makeCurrent(): Boolean
    abstract fun delete()
}