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
    classpath = "com.huskerdev.grapl.gl.natives.$shortOSName"

    includeDirs = arrayListOf(project(":modules:core:native").file("shared"))
    srcDirs = arrayListOf(project(":modules:core:native").file("shared"))

    windows {
        libs = arrayListOf("user32", "gdi32")
    }
    macos {
        frameworks = arrayListOf("OpenGL", "Cocoa")
    }
    linux {
        libs = arrayListOf("GL", "X11")
    }
}

pom {
    name = "$rootName-natives-gl-$shortOSName"
    description = "$rootName gl module (natives for $shortOSName)"
}

moduleInfo {
    name = "$rootName.natives.gl.$shortOSName"
    requiresTransitive(
        "grapl.gl"
    )
    opens(
        "com.huskerdev.grapl.gl.natives.$shortOSName"
    )
}