rootProject.name = "grapl"

pluginManagement {
    includeBuild("plugins/utils")
    includeBuild("plugins/maven")
    includeBuild("plugins/properties")
    includeBuild("plugins/moduleInfo")
    includeBuild("plugins/native")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include(
    "modules:core",
    "modules:core:native",

    "modules:ext-display",

    "modules:gl",
    "modules:gl:native",
)
