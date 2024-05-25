package com.huskerdev.grapl.core.input

import com.huskerdev.grapl.core.platform.Platform


fun getVirtualKeyName(code: Int) = Platform.current.getVirtualKeyName(code)

const val VK_UNKNOWN = -1

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
const val VK_LEFT_SUPER = 343

const val VK_RIGHT_SHIFT = 344
const val VK_RIGHT_CONTROL = 345
const val VK_RIGHT_ALT = 346
const val VK_RIGHT_SUPER = 347

const val VK_LEFT_COMMAND = 349
const val VK_RIGHT_COMMAND = 350

const val VK_MEDIA_PREVIOUS = 360
const val VK_MEDIA_NEXT = 361
const val VK_MEDIA_PAUSE = 362

const val VK_VOLUME_UP = 370
const val VK_VOLUME_DOWN = 371
const val VK_VOLUME_MUTE = 372