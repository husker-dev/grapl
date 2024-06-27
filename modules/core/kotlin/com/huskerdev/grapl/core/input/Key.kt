package com.huskerdev.grapl.core.input

data class Key(
    val code: Int,
    val nativeCode: Int,
    val char: Char?
)

class KeyEvent(
    val key: Key,
    modifiers: Int
): InputEvent(modifiers) {
    override fun toString(): String {
        return "KeyEvent[key:${key}, modifiers:${modifiers}]"
    }
}