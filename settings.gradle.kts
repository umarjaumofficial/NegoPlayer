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
        // REQUIRED for yt-dlp and many advanced media libraries
        maven { url = uri("https://jitpack.io") } 
    }
dependencies {
		implementation 'com.github.User:Repo:Tag'
	}
}

rootProject.name = "NegoPlayer"
include(":app")
