import com.huskerdev.openglfx.plugins.utils.*
import com.huskerdev.plugins.compilation.*


plugins {
    id("utils")
    id("native-compilation")
    id("maven")
    id("module-info")
}

compilation {
    baseName = "grapl"
    classpath = "com.huskerdev.grapl"

    windows {
        src = arrayListOf("src/gl/windows.cpp")
        libs = arrayListOf("opengl32")
    }
}

pom {
    name = "$rootName-natives-d3d9-$shortOSName"
    description = "$rootName d3d9 module (natives for $shortOSName)"
}