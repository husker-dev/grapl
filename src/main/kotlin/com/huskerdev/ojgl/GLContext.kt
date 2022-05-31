package com.huskerdev.ojgl


abstract class GLContext(
    val handle: Long
) {

    companion object {

        @JvmStatic
        fun create(
            shareWith: GLContext,
            coreProfile: Boolean = false
        ) = create(shareWith.handle, coreProfile)

        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: Long = 0L,
            coreProfile: Boolean
        ) = GLPlatform.current.createContext(coreProfile, shareWith)

        @JvmStatic
        fun current() = GLPlatform.current.createFromCurrent()

        @JvmStatic
        fun clear() = GLPlatform.current.makeCurrent(null)
    }

    fun makeCurrent() = GLPlatform.current.makeCurrent(this)
}