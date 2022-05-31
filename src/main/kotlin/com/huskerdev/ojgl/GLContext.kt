package com.huskerdev.ojgl


abstract class GLContext(
    val handle: Long
) {

    companion object {

        @JvmField var CORE_PROFILE = true
        @JvmField var COMPATIBILITY_PROFILE = false

        @JvmStatic
        fun createNew(
            shareWith: GLContext,
            profile: Boolean = COMPATIBILITY_PROFILE
        ) = createNew(shareWith.handle, profile)

        @JvmStatic
        @JvmOverloads
        fun createNew(
            shareWith: Long = 0L,
            profile: Boolean = COMPATIBILITY_PROFILE
        ) = GLPlatform.current.createContext(profile, shareWith)

        @JvmStatic
        fun fromCurrent() = GLPlatform.current.createFromCurrent()

        @JvmStatic
        fun clearCurrent() = GLPlatform.current.makeCurrent(null)
    }

    fun makeCurrent() = GLPlatform.current.makeCurrent(this)
}