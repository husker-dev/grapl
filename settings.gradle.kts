rootProject.name = "grapl"

pluginManagement {
    includeBuild("plugins/utils")
    includeBuild("plugins/maven")
    includeBuild("plugins/properties")
    includeBuild("plugins/moduleInfo")
    includeBuild("plugins/native")
}

include(
    "modules:core",
    "modules:core:native",

    "modules:ext-display",

    "modules:gl",
    "modules:gl:native",
)
