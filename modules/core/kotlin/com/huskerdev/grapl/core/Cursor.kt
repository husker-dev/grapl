package com.huskerdev.grapl.core

enum class Cursor {
    DEFAULT,
    HAND,
    TEXT,
    WAIT,
    PROGRESS,
    CROSSHAIR,
    NOT_ALLOWED,
    HELP,

    SIZE_HORIZONTAL,    // -
    SIZE_VERTICAL,      // |
    SIZE_NE,            // /
    SIZE_SE,            // \
    MOVE,               // +

    SCROLL_VERTICAL,
    SCROLL_HORIZONTAL,
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