package com.huskerdev.grapl.core.exceptions

import com.huskerdev.grapl.core.display.DisplayMode

class DisplayModeChangingException(
    displayMode: DisplayMode?,
    errorText: String
): UnsupportedOperationException(
    "Unable to set display mode " +
            "${displayMode?.size?.width?.toInt()}x${displayMode?.size?.height?.toInt()}x${displayMode?.bits} " +
            "@${displayMode?.frequency} - " +
            errorText
)