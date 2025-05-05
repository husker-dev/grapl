import com.huskerdev.openglfx.plugins.utils.*
import com.huskerdev.plugins.compilation.*


plugins {
    id("utils")
    id("native-compilation")
    id("maven")
    id("module-info")
}

compilation {
    baseName = "lib"
    classpath = "com.huskerdev.grapl.core.natives.$shortOSName"

    windows {
        libs = arrayListOf("user32", "gdi32", "setupapi", "advapi32", "ole32", "dwmapi")
    }
    macos {
        frameworks = arrayListOf("Cocoa", "IOKit")
    }
    linux {
        libs = arrayListOf("wayland-client", "X11", "Xrandr", "Xcursor")
    }
}

pom {
    name = "$rootName-natives-core-$shortOSName"
    description = "$rootName core module (natives for $shortOSName)"
}

moduleInfo {
    name = "$rootName.natives.core.$shortOSName"
    requiresTransitive(
        "grapl"
    )
    opens(
        "com.huskerdev.grapl.core.natives.$shortOSName"
    )
}