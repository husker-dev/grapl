package com.huskerdev.grapl.core.platform

enum class OS(val displayName: String) {
    Other("unknown"),
    Windows("windows"),
    Linux("linux"),
    MacOS("macos");
    override fun toString() = displayName
}