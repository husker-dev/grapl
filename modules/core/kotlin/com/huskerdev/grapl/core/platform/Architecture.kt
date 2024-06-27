package com.huskerdev.grapl.core.platform

enum class Architecture(val displayName: String) {
    ARM64("arm64"),
    ARM32("arm32"),
    X64("x64"),
    X86("x86");
    override fun toString() = displayName
}