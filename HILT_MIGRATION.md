# NegoPlayer Hilt Dependency Injection Migration Guide

This guide covers migrating NegoPlayer from manual dependency injection to Hilt.

## Overview

Hilt simplifies dependency injection by:
- Reducing boilerplate code
- Automatic lifecycle management
- Compile-time checking
- Better integration with Android lifecycle

## Current State

The app currently uses manual DI:
- Repositories instantiated in ViewModels
- DataSource created in Repository constructors
- No shared singleton instances

## Benefits of Migration

1. **Automatic Scoping**: Singleton instances managed automatically
2. **Constructor Injection**: Less boilerplate
3. **Testing**: Easier mocking with test modules
4. **Lifecycle Awareness**: Automatic cleanup
5. **Compile-Time Safety**: Detect missing dependencies at build time

## Step-by-Step Migration

### Step 1: Add Hilt Dependencies

Add to `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.dagger.hilt.android") version "2.48"
}

dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Hilt for ViewModels
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}
```

### Step 2: Create Application class with Hilt

```kotlin
// app/src/main/java/com/negoplayer/NegoPlayerApplication.kt
package com.negoplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.negoplayer.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NegoPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val playbackChannel = NotificationChannel(
                Constants.PLAYBACK_NOTIFICATION_CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for media playback"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(playbackChannel)
        }
    }
}
```

### Step 3: Create DI Module

```kotlin
// app/src/main/java/com/negoplayer/di/RepositoryModule.kt
package com.negoplayer.di

import android.content.Context
import com.negoplayer.data.repository.FileScannerRepository
import com.negoplayer.data.repository.MediaRepository
import com.negoplayer.data.repository.NetworkRepository
import com.negoplayer.data.source.LocalDataSource
import com.negoplayer.data.source.NetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLocalDataSource(
        @ApplicationContext context: Context
    ): LocalDataSource {
        return LocalDataSource(context)
    }

    @Singleton
    @Provides
    fun provideNetworkDataSource(): NetworkDataSource {
        return NetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideMediaRepository(
        @ApplicationContext context: Context,
        localDataSource: LocalDataSource,
        networkDataSource: NetworkDataSource
    ): MediaRepository {
        return MediaRepository(context)
    }

    @Singleton
    @Provides
    fun provideFileScannerRepository(
        @ApplicationContext context: Context
    ): FileScannerRepository {
        return FileScannerRepository(context)
    }

    @Singleton
    @Provides
    fun provideNetworkRepository(): NetworkRepository {
        return NetworkRepository()
    }
}
```

### Step 4: Update ViewModels

```kotlin
// Before (Manual DI)
class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MediaRepository(application)
    // ...
}

// After (With Hilt)
@HiltViewModel
class VideoViewModel @Inject constructor(
    application: Application,
    private val repository: MediaRepository
) : AndroidViewModel(application) {
    // ...
}
```

For all ViewModels:
- Add `@HiltViewModel` annotation
- Add `@Inject` constructor annotation
- Inject dependencies as parameters

```kotlin
// app/src/main/java/com/negoplayer/viewmodel/VideoViewModel.kt
@HiltViewModel
class VideoViewModel @Inject constructor(
    application: Application,
    private val repository: MediaRepository
) : AndroidViewModel(application) {
    // Rest of implementation unchanged
}

// Similar for MusicViewModel, FoldersViewModel, SettingsViewModel, MainViewModel
```

### Step 5: Update UI Components

For Activities:
```kotlin
// app/src/main/java/com/negoplayer/ui/MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No changes needed for Compose screens
        setContent { /* ... */ }
    }
}
```

For Fragments (if used):
```kotlin
// Add @AndroidEntryPoint to fragments
@AndroidEntryPoint
class VideoFragment : Fragment() {
    private val viewModel: VideoViewModel by viewModels()
    // ...
}
```

### Step 6: Update Compose Screens

In Compose, use `hiltViewModel()` instead of `viewModel()`:

```kotlin
// Before
@Composable
fun VideosScreen(
    viewModel: VideoViewModel = viewModel()
) { /* ... */ }

// After
@Composable
fun VideosScreen(
    viewModel: VideoViewModel = hiltViewModel()
) { /* ... */ }
```

Update all Compose screens:
```kotlin
// MusicScreen
@Composable
fun MusicScreen(viewModel: MusicViewModel = hiltViewModel()) { /* ... */ }

// FoldersScreen  
@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel = hiltViewModel(),
    onPlayVideo: (uri: String, title: String, playlist: List<String>, index: Int) -> Unit
) { /* ... */ }

// SettingsScreen
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) { /* ... */ }
```

### Step 7: Testing with Hilt

For unit tests:

```kotlin
// app/src/test/java/com/negoplayer/viewmodel/VideoViewModelTest.kt
@HiltAndroidTest
@RunWith(HiltTestRunner::class)
class VideoViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mediaRepository: MediaRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testVideoViewModel() {
        // Test implementation
    }
}
```

Add test dependencies:
```kotlin
// In build.gradle.kts
testImplementation("com.google.dagger:hilt-android-testing:2.48")
kaptTest("com.google.dagger:hilt-compiler:2.48")
androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
kaptAndroidTest("com.google.dagger:hilt-compiler:2.48")
```

### Step 8: Test Module for Fakes

```kotlin
// app/src/test/java/com/negoplayer/di/TestRepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
@UninstallModules(RepositoryModule::class)
object TestRepositoryModule {

    @Singleton
    @Provides
    fun provideMediaRepository(): MediaRepository {
        return FakeMediaRepository()
    }

    @Singleton
    @Provides
    fun provideFileScannerRepository(): FileScannerRepository {
        return FakeFileScannerRepository()
    }
}
```

## Migration Checklist

- [ ] Add Hilt dependencies to build.gradle.kts
- [ ] Add `@HiltAndroidApp` to Application class
- [ ] Create RepositoryModule with @Provides methods
- [ ] Add `@HiltViewModel` to all ViewModels
- [ ] Update @Inject constructors in ViewModels
- [ ] Replace `viewModel()` with `hiltViewModel()` in Compose
- [ ] Update unit tests with `@HiltAndroidTest`
- [ ] Create test modules with fake implementations
- [ ] Run all tests to verify DI graph
- [ ] Test on physical device

## Advantages Post-Migration

1. **Less Boilerplate**: Repositories no longer created manually
2. **Singleton Management**: Android lifecycle-aware singletons
3. **Testability**: Easy to inject test doubles
4. **Compile-Time Safety**: Detects DI errors at build time
5. **Better Performance**: Generated code is optimized

## Troubleshooting

### DI Compilation Errors
```bash
# Clear build cache
./gradlew clean

# Rebuild
./gradlew build
```

### Missing Dependencies
Ensure all @Provides methods are defined for dependencies.

### Lifecycle Issues
Use `@Singleton` for app-level singletons, `@ActivityScoped` for activity-scoped.

## References

- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Dagger 2](https://dagger.dev/)
- [Hilt Testing](https://developer.android.com/training/dependency-injection/hilt-testing)

---

**Status**: Optional Enhancement
**Complexity**: Medium
**Time to Implement**: 2-3 hours
