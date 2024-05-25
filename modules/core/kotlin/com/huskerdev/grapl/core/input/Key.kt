package com.huskerdev.grapl.core.input

data class Key(
    val code: Int,
    val nativeCode: Int,
    val char: Char?
)

open class KeyEvent(
    val key: Key,
    modifiers: Int
): InputEvent(modifiers)