package com.huskerdev.grapl.core.input

enum class Cursor {
    DEFAULT,
    HAND,
    TEXT,
    WAIT,
    PROGRESS,
    CROSSHAIR,
    NOT_ALLOWED,
    HELP,

    SIZE_VERTICAL_DOUBLE,
    SIZE_HORIZONTAL_DOUBLE,
    SIZE_W,
    SIZE_E,
    SIZE_N,
    SIZE_S,
    SIZE_NE, // /
    SIZE_SE, // \
    MOVE,

    SCROLL_ALL,
    SCROLL_UP,
    SCROLL_DOWN,
    SCROLL_LEFT,
    SCROLL_RIGHT,
    SCROLL_TOP_LEFT,
    SCROLL_TOP_RIGHT,
    SCROLL_BOTTOM_LEFT,
    SCROLL_BOTTOM_RIGHT,
}