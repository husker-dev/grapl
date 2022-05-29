package com.huskerdev.ojgl.utils

const val Other = -1
const val Windows = 0
const val Linux = 1
const val MacOS = 2

class PlatformUtils {

    companion object {
        val os = System.getProperty("os.name", "generic").run {
            return@run if ("mac" in this || "darwin" in this) MacOS
            else if ("win" in this) Windows
            else if ("nux" in this || "nix" in this || "aix" in this) Linux
            else Other
        }
    }

}