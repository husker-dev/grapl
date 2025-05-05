import com.huskerdev.openglfx.plugins.utils.*
import com.huskerdev.plugins.maven.silentApi


plugins {
    alias(libs.plugins.kotlin.jvm)
    id("utils")
    id("maven")
    id("module-info")
}

configureKotlinProject()

dependencies {
    api(project(":modules:core"))

    silentApi("$group:$rootName-natives-gl-win:$version")
    silentApi("$group:$rootName-natives-gl-linux:$version")
    silentApi("$group:$rootName-natives-gl-macos:$version")

    // Testing
    subprojects.forEach(::testImplementation)
    testImplementation(project(":modules:core:native"))
    testImplementation(project(":modules:ext-display"))

    testImplementation(platform("org.lwjgl:lwjgl-bom:3.3.3"))
    arrayOf("", "-opengl").forEach { module ->
        arrayOf("windows", "macos", "linux").forEach { os ->
            arrayOf("-arm64", "").forEach { arch ->
                testImplementation("org.lwjgl:lwjgl$module")
                testRuntimeOnly("org.lwjgl:lwjgl$module::natives-$os$arch")
            }
        }
    }
}

pom {
    name = "$rootName-gl"
    description = "$rootName gl module"
}

moduleInfo {
    name = "$rootName.gl"
    requiresTransitive(
        "grapl"
    )
    exports(
        "com.huskerdev.grapl.gl",
        "com.huskerdev.grapl.gl.platforms.linux.egl",
        "com.huskerdev.grapl.gl.platforms.linux.glx",
        "com.huskerdev.grapl.gl.platforms.win",
        "com.huskerdev.grapl.gl.platforms.macos",
    )
}