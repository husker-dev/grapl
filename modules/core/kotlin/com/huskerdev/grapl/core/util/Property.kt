package com.huskerdev.grapl.core.util

import kotlin.reflect.KMutableProperty0

open class ReadOnlyProperty<T>(
    defaultValue: T
) {
    val listeners = listenerSet()

    open var internalValue: T = defaultValue
        set(value) {
            field = value
            listeners.dispatch()
        }

    open val value: T
        get() = internalValue
}


open class Property<T>(
    defaultValue: T,
    private val onExternalSet: (T) -> Unit
): ReadOnlyProperty<T>(defaultValue) {
    override var value: T
        get() = super.value
        set(value) {
            internalValue = value
            onExternalSet(value)
        }
}

open class LinkedProperty<T>(
    private val property: KMutableProperty0<T>,
    onExternalSet: (T) -> Unit
): Property<T>(property.get(), onExternalSet){

    override var internalValue: T
        get() = property.get()
        set(value) {
            property.set(value)
            listeners.dispatch()
        }
}