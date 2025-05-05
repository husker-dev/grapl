import com.huskerdev.openglfx.plugins.utils.*


plugins {
    alias(libs.plugins.kotlin.jvm)
    id("utils")
    id("maven")
    id("module-info")
}

configureKotlinProject()

dependencies {
    api(project(":modules:core"))
}

pom {
    name = "$rootName-ext-display"
    description = "$rootName display extension module"
}

moduleInfo {
    name = "$rootName.ext.display"
    requiresTransitive(
        "grapl"
    )
    exports(
        "com.huskerdev.grapl.ext.display",
        "com.huskerdev.grapl.ext.display-utils"
    )
}