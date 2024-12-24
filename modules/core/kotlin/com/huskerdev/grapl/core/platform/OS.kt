package com.huskerdev.grapl.core.platform

enum class OS(val displayName: String, val shortName: String) {
    Other("unknown", "unknown"),
    Windows("windows", "win"),
    Linux("linux", "linux"),
    MacOS("macos", "macos");
    override fun toString() = displayName
}