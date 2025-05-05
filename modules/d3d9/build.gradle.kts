import com.huskerdev.openglfx.plugins.utils.*
import com.huskerdev.plugins.maven.silentApi


plugins {
    alias(libs.plugins.kotlin.jvm)
    id("utils")
    id("maven")
}

configureKotlinProject()