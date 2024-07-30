package com.huskerdev.grapl.core.util

import java.util.function.Consumer


fun listenerSet() = ListenerSet()
fun <T> listenerSetOf() = ListenerArgSet<T>()


@Suppress("unused")
class ListenerSet {
    private val set = hashSetOf<Runnable>()

    fun dispatch() {
        set.forEach { it.run() }
    }

    fun add(listener: Runnable){
        set.add(listener)
    }

    operator fun plusAssign(listener: Runnable) {
        set.add(listener)
    }

    operator fun plusAssign(listener: () -> Unit) {
        set.add(listener)
    }

    fun remove(listener: Runnable){
        set.remove(listener)
    }

    operator fun minusAssign(listener: Runnable) {
        set.remove(listener)
    }

    operator fun minusAssign(listener: () -> Unit) {
        set.remove(listener)
    }
}


@Suppress("unused")
class ListenerArgSet<T> {
    private val set = hashSetOf<Consumer<T>>()

    fun dispatch(arg: T) {
        set.forEach { it.accept(arg) }
    }

    fun add(listener: Consumer<T>){
        set.add(listener)
    }

    operator fun plusAssign(listener: Consumer<T>) {
        set.add(listener)
    }

    operator fun plusAssign(listener: (T) -> Unit) {
        set.add(listener)
    }

    fun remove(listener: Consumer<T>){
        set.remove(listener)
    }

    operator fun minusAssign(listener: Consumer<T>) {
        set.remove(listener)
    }

    operator fun minusAssign(listener: (T) -> Unit) {
        set.remove(listener)
    }
}