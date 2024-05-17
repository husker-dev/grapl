package com.huskerdev.grapl.core.input

class Key {
    companion object {
        const val VK_SPACE = 32
        const val VK_APOSTROPHE = 39
        const val VK_COMMA = 44
        const val VK_MINUS = 45
        const val VK_PERIOD = 46
        const val VK_SLASH = 47

        const val VK_0 = 48
        const val VK_1 = 49
        const val VK_2 = 50
        const val VK_3 = 51
        const val VK_4 = 52
        const val VK_5 = 53
        const val VK_6 = 54
        const val VK_7 = 55
        const val VK_8 = 56
        const val VK_9 = 57

        const val VK_SEMICOLON = 59
        const val VK_EQUAL = 61

        const val VK_A = 65
        const val VK_B = 66
        const val VK_C = 67
        const val VK_D = 68
        const val VK_E = 69
        const val VK_F = 70
        const val VK_G = 71
        const val VK_H = 72
        const val VK_I = 73
        const val VK_J = 74
        const val VK_K = 75
        const val VK_L = 76
        const val VK_M = 77
        const val VK_N = 78
        const val VK_O = 79
        const val VK_P = 80
        const val VK_Q = 81
        const val VK_R = 82
        const val VK_S = 83
        const val VK_T = 84
        const val VK_U = 85
        const val VK_V = 86
        const val VK_W = 87
        const val VK_X = 88
        const val VK_Y = 89
        const val VK_Z = 90

        const val VK_LEFT_BRACKET = 91
        const val VK_BACKSLASH = 92
        const val VK_RIGHT_BRACKET = 93
        const val VK_GRAVE_ACCENT = 96
        const val VK_ESCAPE = 256
        const val VK_ENTER = 257
        const val VK_TAB = 258
        const val VK_BACKSPACE = 259
        const val VK_INSERT = 260
        const val VK_DELETE = 261

        const val VK_RIGHT = 262
        const val VK_LEFT = 263
        const val VK_DOWN = 264
        const val VK_UP = 265

        const val VK_PAGE_UP = 266
        const val VK_PAGE_DOWN = 267
        const val VK_HOME = 268
        const val VK_END = 269
        const val VK_CAPS_LOCK = 280
        const val VK_SCROLL_LOCK = 281
        const val VK_NUM_LOCK = 282
        const val VK_PRINT_SCREEN = 283

        const val VK_F1 = 290
        const val VK_F2 = 291
        const val VK_F3 = 292
        const val VK_F4 = 293
        const val VK_F5 = 294
        const val VK_F6 = 295
        const val VK_F7 = 296
        const val VK_F8 = 297
        const val VK_F9 = 298
        const val VK_F10 = 299
        const val VK_F11 = 300
        const val VK_F12 = 301
        const val VK_F13 = 302
        const val VK_F14 = 303
        const val VK_F15 = 304
        const val VK_F16 = 305
        const val VK_F17 = 306
        const val VK_F18 = 307
        const val VK_F19 = 308
        const val VK_F20 = 309
        const val VK_F21 = 310
        const val VK_F22 = 311
        const val VK_F23 = 312
        const val VK_F24 = 313
        const val VK_F25 = 314

        const val VK_KP_0 = 320
        const val VK_KP_1 = 321
        const val VK_KP_2 = 322
        const val VK_KP_3 = 323
        const val VK_KP_4 = 324
        const val VK_KP_5 = 325
        const val VK_KP_6 = 326
        const val VK_KP_7 = 327
        const val VK_KP_8 = 328
        const val VK_KP_9 = 329
        const val VK_KP_DECIMAL = 330
        const val VK_KP_DIVIDE = 331
        const val VK_KP_MULTIPLY = 332
        const val VK_KP_SUBTRACT = 333
        const val VK_KP_ADD = 334
        const val VK_KP_ENTER = 335
        const val VK_KP_EQUAL = 336
        const val VK_LEFT_SHIFT = 340
        const val VK_LEFT_CONTROL = 341
        const val VK_LEFT_ALT = 342
        const val VK_COMMAND = 343

        const val VK_RIGHT_SHIFT = 344
        const val VK_RIGHT_CONTROL = 345
        const val VK_RIGHT_ALT = 346
        const val VK_RIGHT_COMMAND = 347

        fun name(code: Int) = when(code) {
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
            VK_COMMAND -> "command"

            VK_RIGHT_SHIFT -> "right shift"
            VK_RIGHT_CONTROL -> "right control"
            VK_RIGHT_ALT -> "right alt"
            VK_RIGHT_COMMAND -> "right command"
            else -> "unknown"
        }
    }
}