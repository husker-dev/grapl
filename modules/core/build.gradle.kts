import com.huskerdev.openglfx.plugins.utils.*
import com.huskerdev.plugins.maven.silentApi


plugins {
    alias(libs.plugins.kotlin.jvm)
    id("utils")
    id("maven")
    id("properties")
    id("module-info")
}

configureKotlinProject()

dependencies {
    subprojects.forEach(::testImplementation)

    silentApi("$group:$rootName-natives-core-win:$version")
    silentApi("$group:$rootName-natives-core-linux:$version")
    silentApi("$group:$rootName-natives-core-macos:$version")
}

pom {
    name = rootName
    description = "$rootName core module"
}

properties {
    name = "GraplInfo"
    classpath = "com.huskerdev.grapl"
    srcDir = file("kotlin")

    field("VERSION", version)
}

moduleInfo {
    name = rootName
    requiresTransitive(
        "kotlin.stdlib"
    )
    exports(
        "com.huskerdev.grapl",
        "com.huskerdev.grapl.core",
        "com.huskerdev.grapl.core.display",
        "com.huskerdev.grapl.core.display.impl",
        "com.huskerdev.grapl.core.exceptions",
        "com.huskerdev.grapl.core.input",
        "com.huskerdev.grapl.core.platform",
        "com.huskerdev.grapl.core.platform.impl",
        "com.huskerdev.grapl.core.util",
        "com.huskerdev.grapl.core.window",
        "com.huskerdev.grapl.core.window.impl"
    )
}