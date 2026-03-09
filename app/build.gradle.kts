plugins {
id("com.android.application")
id("org.jetbrains.kotlin.android")
}

android {
namespace = "com.negoplayer"
compileSdk = 34

defaultConfig {
    applicationId = "com.negoplayer"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    vectorDrawables {
        useSupportLibrary = true
    }
}

signingConfigs {
    create("release") {
        val keystorePath = System.getenv("KEYSTORE_PATH")

        if (keystorePath != null) {
            storeFile = file(keystorePath)
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}

buildTypes {

    release {
        isMinifyEnabled = true
        isShrinkResources = true

        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )

        if (System.getenv("KEYSTORE_PATH") != null) {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    debug {
        applicationIdSuffix = ".debug"
        isMinifyEnabled = false
    }
}

compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}

buildFeatures {
    compose = true
}

composeOptions {
    kotlinCompilerExtensionVersion = "1.5.8"
}

packaging {
    resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

}

dependencies {

// Compose BOM
val composeBom = platform("androidx.compose:compose-bom:2024.02.01")
implementation(composeBom)
androidTestImplementation(composeBom)

// Compose UI
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")

// Material 3
implementation("androidx.compose.material3:material3")

// Icons
implementation("androidx.compose.material:material-icons-extended")

debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")

// Core Android
implementation("androidx.core:core-ktx:1.12.0")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Activity
implementation("androidx.activity:activity-compose:1.8.2")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Media3 Player
val media3Version = "1.3.1"

implementation("androidx.media3:media3-exoplayer:$media3Version")
implementation("androidx.media3:media3-ui:$media3Version")
implementation("androidx.media3:media3-session:$media3Version")
implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
implementation("androidx.media3:media3-datasource-okhttp:$media3Version")

// Downloader (yt-dlp Android)  ✅ FIXED FORMAT
implementation("com.github.umarjaumofficial:youtubedl-android:0.16.0")
implementation("com.github.umarjaumofficial:youtubedl-android-ffmpeg:0.16.0")
// YouTube player bridge
implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

// Networking
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Image loading
implementation("io.coil-kt:coil-compose:2.6.0")

// Permissions helper
implementation("com.google.accompanist:accompanist-permissions:0.34.0")

// Data storage
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Testing
testImplementation("junit:junit:4.13.2")

androidTestImplementation("androidx.test.ext:junit:1.1.6")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

}
