package com.huskerdev.grapl.gl


@Suppress("unused")
abstract class GLContext(
    val handle: Long,
    val majorVersion: Int,
    val minorVersion: Int,
    val profile: GLProfile,
    val debug: Boolean
) {
    companion object {
        private val DEFAULT_PROFILE             = GLProfile.CORE
        private const val DEFAULT_MAJOR_VERSION = -1
        private const val DEFAULT_MINOR_VERSION = -1
        private const val DEFAULT_DEBUG         = false

        private val debugCallbacks = hashMapOf<Long, (GLDebugEvent) -> Unit>()

        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: GLContext,
            profile: GLProfile      = DEFAULT_PROFILE,
            majorVersion: Int       = DEFAULT_MAJOR_VERSION,
            minorVersion: Int       = DEFAULT_MINOR_VERSION,
            debug: Boolean          = DEFAULT_DEBUG
        ) = create(shareWith.handle, profile, majorVersion, minorVersion, debug)

        @JvmStatic
        @JvmOverloads
        fun create(
            shareWith: Long = 0L,
            profile: GLProfile      = DEFAULT_PROFILE,
            majorVersion: Int       = DEFAULT_MAJOR_VERSION,
            minorVersion: Int       = DEFAULT_MINOR_VERSION,
            debug: Boolean          = DEFAULT_DEBUG
        ) = GLManager.current.createContext(profile, shareWith, majorVersion, minorVersion, debug)

        @JvmStatic
        fun current() = GLManager.current.createFromCurrentContext()

        @JvmStatic
        fun clear() = GLManager.current.clearContext()

        fun bindDebugCallback(callback: (GLDebugEvent) -> Unit){
            val context = current()
            if(context.debug && GLManager.current.supportsDebug()){
                debugCallbacks[context.handle] = callback
                context.bindDebugCallback()
            }
        }

        @JvmStatic
        fun unbindDebugCallback(){
            debugCallbacks.remove(current().handle)
        }

        @JvmStatic
        fun dispatchDebug(handle: Long, source: Int, type: Int, id: Int, severity: Int, message: String){
            debugCallbacks[handle]?.invoke(GLDebugEvent(
                GLDebugSource.of(source),
                GLDebugType.of(type),
                id,
                GLDebugSeverity.of(severity),
                message
            ))
        }
    }

    abstract fun makeCurrent(): Boolean
    abstract fun delete()

    protected abstract fun bindDebugCallback()
}