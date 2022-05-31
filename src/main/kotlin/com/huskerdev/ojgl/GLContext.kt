package com.huskerdev.ojgl


abstract class GLContext(
    val handle: Long
) {

    companion object {

        @JvmField var CORE_PROFILE = true
        @JvmField var COMPATIBILITY_PROFILE = false

        @JvmStatic
        fun create(
            shareWith: GLContext,
            profile: Boolean = COMPATIBILITY_PROFILE
        ) = create(shareWith.handle, profile)

        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: Long = 0L,
            profile: Boolean = COMPATIBILITY_PROFILE
        ) = GLPlatform.current.createContext(profile, shareWith)

        @JvmStatic
        fun current() = GLPlatform.current.createFromCurrent()

        @JvmStatic
        fun clear() = GLPlatform.current.makeCurrent(null)
    }

    fun makeCurrent() = GLPlatform.current.makeCurrent(this)
}