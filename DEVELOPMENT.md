# NegoPlayer Development Guide

This guide covers building, testing, and releasing NegoPlayer APK.

## Prerequisites

- **Android Studio**: Arctic Fox or later
- **JDK**: 17 or higher
- **Android SDK**: 21-34
- **Gradle**: 8.0 or higher (bundled with project)

## Project Structure

```
NegoPlayer/
├── app/
│   ├── src/main/
│   │   ├── java/com/negoplayer/
│   │   │   ├── data/          # Data layer (repositories, models, sources)
│   │   │   ├── player/        # Player engine (ExoPlayer management)
│   │   │   ├── ui/            # UI layer (Compose screens, fragments)
│   │   │   └── viewmodel/     # ViewModels (state management)
│   │   ├── AndroidManifest.xml
│   │   └── res/               # Resources
│   ├── build.gradle.kts       # App build configuration
│   └── proguard-rules.pro     # ProGuard rules
├── .github/
│   └── workflows/             # CI/CD pipelines
├── build.sh                   # Local build script
└── README.md
```

## Building

### Debug APK (Development)

```bash
# Using build script
./build.sh debug

# Or using gradle directly
./gradlew assembleDebug

# APK location: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (Production)

```bash
# Export keystore credentials first
export KEYSTORE_PATH=/path/to/release.keystore
export KEYSTORE_PASSWORD=your_keystore_password
export KEY_ALIAS=your_key_alias
export KEY_PASSWORD=your_key_password

# Build using script
./build.sh release

# Or using gradle
./gradlew assembleRelease

# APK location: app/build/outputs/apk/release/app-release-unsigned.apk
```

## Creating a Release Keystore

```bash
# Generate keystore (one-time setup)
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer_key

# Store securely (e.g., GitHub Secrets for CI/CD)
```

## Running Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Lint checks
./gradlew lint

# All checks
./gradlew check
```

## Development Workflow

### 1. Feature Development

```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and test locally
./gradlew assembleDebug

# Commit and push
git add .
git commit -m "feat: add new feature"
git push origin feature/new-feature
```

### 2. Pull Request

- Push feature branch to GitHub
- Create Pull Request against `main` or `develop`
- GitHub Actions automatically runs tests
- Address any review comments
- Merge when approved

### 3. Release

```bash
# Tag release version
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1

# GitHub Actions automatically builds and creates release
```

## GitHub Actions Workflows

### `.github/workflows/test.yml`
- Runs on every push and PR
- Executes unit tests
- Runs lint checks
- Builds debug APK to verify compilation

### `.github/workflows/build-and-release.yml`
- Triggers on version tags (v*.*.*)
- Builds release APK
- Signs with keystore credentials from secrets
- Creates GitHub release with APK

## Setting Up GitHub Secrets

For CI/CD release builds, add these secrets to your GitHub repository:

1. **KEYSTORE_ENCODED**: Base64-encoded keystore file
   ```bash
   base64 release.keystore > keystore.b64
   # Copy contents to GitHub Secret
   ```

2. **KEYSTORE_PASSWORD**: Your keystore password

3. **KEY_ALIAS**: Key alias name (from keytool)

4. **KEY_PASSWORD**: Key password

## APK Optimization

The project includes:

- **ProGuard/R8**: Code obfuscation and minification in release builds
- **Resource Shrinking**: Removes unused resources
- **ShrinkResources**: Enabled for release builds
- **BuildConfig Optimization**: Removes debug code

## Troubleshooting

### Build Fails
```bash
# Clean and rebuild
./gradlew clean assembleDebug

# Sync gradle files
./gradlew sync
```

### Tests Fail
```bash
# Run tests with verbose output
./gradlew testDebugUnitTest -info
```

### Keystore Issues
```bash
# List keystore contents
keytool -list -v -keystore release.keystore

# Verify signing
jarsigner -verify -verbose app.apk
```

## Performance

- **API Level**: Targets Android 5.0+ (API 21)
- **Min SDK**: 21, Target SDK: 34
- **APK Size**: ~40-50 MB (optimized)
- **Supported ABIs**: arm64-v8a, armeabi-v7a

## Dependencies

- **Compose**: Material 3 UI toolkit
- **Media3**: ExoPlayer for playback
- **Coil**: Image loading
- **OkHttp**: HTTP client
- **DataStore**: Preferences storage

## Architecture

- **MVVM**: ViewModel + StateFlow for reactive UI
- **Repository Pattern**: Clean data layer abstraction
- **Coroutines**: Async operations
- **Compose**: Modern declarative UI

## Best Practices

1. **Code Style**: Follow Kotlin conventions
2. **Testing**: Write unit tests for viewmodels
3. **Permissions**: Request at runtime (Android 6.0+)
4. **Resources**: Use res/values for strings and dimensions
5. **Accessibility**: Include content descriptions and labels

## Support

For issues or questions:
1. Check GitHub issues
2. Review relevant documentation
3. Open new issue with detailed description

---

**Last Updated**: 2024
**Version**: 1.0.0
