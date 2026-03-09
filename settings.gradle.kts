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
        // REQUIRED for JitPack based libraries
        maven { url = uri("https://jitpack.io") } 
    }
}

rootProject.name = "NegoPlayer"
include(":app")
