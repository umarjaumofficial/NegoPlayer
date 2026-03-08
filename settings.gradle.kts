/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 */

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NegoPlayer"

include(":app")
include(":core")
include(":data")
include(":player")
include(":network")
include(":ui")
include(":analytics")
include(":media-library")
include(":subtitle-engine")
include(":streaming")
