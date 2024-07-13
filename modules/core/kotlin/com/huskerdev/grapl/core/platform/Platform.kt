package com.huskerdev.grapl.core.platform

import com.huskerdev.grapl.GraplNatives
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.input.*

import com.huskerdev.grapl.core.platform.impl.LinuxPlatform
import com.huskerdev.grapl.core.platform.impl.MacPlatform
import com.huskerdev.grapl.core.platform.impl.WinPlatform
import java.io.File
import java.io.FileOutputStream


abstract class Platform {
    companion object {
        val os = System.getProperty("os.name", "generic").lowercase().run {
            when {
                "mac" in this || "darwin" in this -> OS.MacOS
                "win" in this -> OS.Windows
                "nux" in this || "nix" in this || "aix" in this -> OS.Linux
                else -> OS.Other
            }
        }

        val arch = System.getProperty("os.arch").lowercase().run {
            when {
                startsWith("aarch64") || startsWith("armv8") -> Architecture.ARM64
                startsWith("arm") -> Architecture.ARM32
                "64" in this -> Architecture.X64
                else -> Architecture.X86
            }
        }

        val current = when(os) {
            OS.Windows -> WinPlatform()
            OS.Linux -> LinuxPlatform()
            OS.MacOS -> MacPlatform()
            else -> throw UnsupportedOperationException("Unsupported platform")
        }

        private val loadedLibs = hashSetOf<String>()

        fun loadLibraryFromResources(path: String) {
            if(path in loadedLibs)
                return

            var fileName = path.replace("/", "-")
            if(fileName.startsWith("-"))
                fileName = fileName.substring(1)
            fileName = System.currentTimeMillis().toString() + "#" + fileName

            val tmpFile = File(System.getProperty("java.io.tmpdir"), fileName)
            try {
                FileOutputStream(tmpFile).use {
                    this::class.java.getResourceAsStream(path)!!.copyTo(it)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            tmpFile.deleteOnExit()
            System.load(tmpFile.absolutePath)
            loadedLibs.add(path)
        }

        fun loadLibraryFromResources(
            classpath: String,
            baseName: String,
            version: String
        ) {
            val fileName = current.specifyLibName("$baseName-$version")
            val path = classpath.replace(".", "/")
            loadLibraryFromResources("/$path/$fileName")
        }

        init {
            GraplNatives.load()
        }
    }

    abstract val primaryDisplay: Display
    abstract val displays: Array<Display>

    internal abstract fun specifyLibName(libName: String): String

    abstract fun peekMessages()
    abstract fun waitMessages(timeout: Int = -1)
    abstract fun postEmptyMessage()

    open fun getMouseButtonName(button: Int) = when(button){
        MB_LEFT -> "left mouse button"
        MB_MIDDLE -> "middle mouse button"
        MB_RIGHT -> "right mouse button"
        MB_BACK -> "back mouse button"
        MB_FORWARD -> "forward mouse button"
        else -> "unknown"
    }

    open fun getVirtualKeyName(keyCode: Int) = when(keyCode) {
        VK_UNKNOWN -> "unknown"

        VK_SPACE -> "space"
        VK_APOSTROPHE -> "'"
        VK_COMMA -> ","
        VK_MINUS -> "-"
        VK_PERIOD -> "."
        VK_SLASH -> "/"

        VK_0 -> "0"
        VK_1 -> "1"
        VK_2 -> "2"
        VK_3 -> "3"
        VK_4 -> "4"
        VK_5 -> "5"
        VK_6 -> "6"
        VK_7 -> "7"
        VK_8 -> "8"
        VK_9 -> "9"

        VK_SEMICOLON -> ";"
        VK_EQUAL -> "="

        VK_A -> "a"
        VK_B -> "b"
        VK_C -> "c"
        VK_D -> "d"
        VK_E -> "e"
        VK_F -> "f"
        VK_G -> "g"
        VK_H -> "h"
        VK_I -> "i"
        VK_J -> "j"
        VK_K -> "k"
        VK_L -> "l"
        VK_M -> "m"
        VK_N -> "n"
        VK_O -> "o"
        VK_P -> "p"
        VK_Q -> "q"
        VK_R -> "r"
        VK_S -> "s"
        VK_T -> "t"
        VK_U -> "u"
        VK_V -> "v"
        VK_W -> "w"
        VK_X -> "x"
        VK_Y -> "y"
        VK_Z -> "z"

        VK_LEFT_BRACKET -> "["
        VK_BACKSLASH -> "\\"
        VK_RIGHT_BRACKET -> "]"
        VK_GRAVE_ACCENT -> "`"
        VK_ESCAPE -> "escape"
        VK_ENTER -> "enter"
        VK_TAB -> "tab"
        VK_BACKSPACE -> "backspace"
        VK_INSERT -> "insert"
        VK_DELETE -> "delete"

        VK_RIGHT -> "right"
        VK_LEFT -> "left"
        VK_DOWN -> "down"
        VK_UP -> "up"

        VK_PAGE_UP -> "page up"
        VK_PAGE_DOWN -> "page down"
        VK_HOME -> "home"
        VK_END -> "end"
        VK_CAPS_LOCK -> "caps lock"
        VK_SCROLL_LOCK -> "scroll lock"
        VK_NUM_LOCK -> "num lock"
        VK_PRINT_SCREEN -> "print screen"

        VK_F1 -> "f1"
        VK_F2 -> "f2"
        VK_F3 -> "f3"
        VK_F4 -> "f4"
        VK_F5 -> "f5"
        VK_F6 -> "f6"
        VK_F7 -> "f7"
        VK_F8 -> "f8"
        VK_F9 -> "f9"
        VK_F10 -> "f10"
        VK_F11 -> "f11"
        VK_F12 -> "f12"
        VK_F13 -> "f13"
        VK_F14 -> "f14"
        VK_F15 -> "f15"
        VK_F16 -> "f16"
        VK_F17 -> "f17"
        VK_F18 -> "f18"
        VK_F19 -> "f19"
        VK_F20 -> "f20"
        VK_F21 -> "f21"
        VK_F22 -> "f22"
        VK_F23 -> "f23"
        VK_F24 -> "f24"
        VK_F25 -> "f25"

        VK_KP_0 -> "keypad 0"
        VK_KP_1 -> "keypad 1"
        VK_KP_2 -> "keypad 2"
        VK_KP_3 -> "keypad 3"
        VK_KP_4 -> "keypad 4"
        VK_KP_5 -> "keypad 5"
        VK_KP_6 -> "keypad 6"
        VK_KP_7 -> "keypad 7"
        VK_KP_8 -> "keypad 8"
        VK_KP_9 -> "keypad 9"
        VK_KP_DECIMAL -> "keypad ."
        VK_KP_DIVIDE -> "keypad /"
        VK_KP_MULTIPLY -> "keypad *"
        VK_KP_SUBTRACT -> "keypad -"
        VK_KP_ADD -> "keypad +"
        VK_KP_ENTER -> "keypad enter"
        VK_KP_EQUAL -> "keypad ="

        VK_LEFT_SHIFT -> "shift"
        VK_LEFT_CONTROL -> "control"
        VK_LEFT_ALT -> "alt"
        VK_LEFT_SUPER -> "super"

        VK_RIGHT_SHIFT -> "right shift"
        VK_RIGHT_CONTROL -> "right control"
        VK_RIGHT_ALT -> "right alt"
        VK_RIGHT_SUPER -> "right super"

        VK_LEFT_COMMAND -> "command"
        VK_RIGHT_COMMAND -> "right command"

        VK_MEDIA_PREVIOUS -> "previous media"
        VK_MEDIA_NEXT -> "next media"
        VK_MEDIA_PAUSE -> "play/pause media"

        VK_VOLUME_UP -> "volume up"
        VK_VOLUME_DOWN -> "volume down"
        VK_VOLUME_MUTE -> "volume mute"
        else -> "unnamed"
    }
}