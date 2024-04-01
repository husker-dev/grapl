package com.huskerdev.grapl.core.util

import kotlin.reflect.KProperty

fun <T> observer(defaultValue: T, onChange: (T) -> Unit) = ObservableDelegate(defaultValue, onChange)

fun <T> listenerObserver(defaultValue: T, listeners: HashSet<() -> Unit>) = ObservableDelegate(defaultValue){
    listeners.forEach { it() }
}

class ObservableDelegate<T>(
    private var value: T,
    private val onChange: (T) -> Unit
) {
    operator fun getValue(thisRef: Any, property: KProperty<*>) = value

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if(this.value != value){
            this.value = value
            onChange(value)
        }
    }
}