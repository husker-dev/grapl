package com.huskerdev.grapl.core.window

abstract class WindowEventConsumer {
    private var isInitialized = false

    fun dispatchUpdate(){
        if(!isInitialized) {
            isInitialized = true
            onInit()
        }
        onUpdate()
    }

    abstract fun onInit()
    abstract fun onUpdate()

    class Lambda: WindowEventConsumer() {
        private var initBlock: (() -> Unit)? = null
        private var updateBlock: (() -> Unit)? = null

        override fun onInit() {
            initBlock?.invoke()
        }

        override fun onUpdate() {
            updateBlock?.invoke()
        }

        fun onInit(block: () -> Unit){
            initBlock = block
        }

        fun onUpdate(block: () -> Unit){
            updateBlock = block
        }
    }

}

fun windowEventConsumer(block: WindowEventConsumer.Lambda.() -> Unit) =
    WindowEventConsumer.Lambda().apply(block)
