# NegoPlayer - Complete Setup & Build Guide

A feature-rich Android media player built with Jetpack Compose and Media3, supporting both local and streaming media.

## Quick Start

### 1. Initial Setup

```bash
# Clone repository
git clone https://github.com/Umarjaum/NegoPlayer.git
cd NegoPlayer

# Make build script executable
chmod +x build.sh

# Sync gradle
./gradlew sync
```

### 2. Build Debug APK

```bash
./build.sh debug
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

### 3. Install on Device

```bash
# Using adb (Android Debug Bridge)
adb install app/build/outputs/apk/debug/app-debug.apk

# Or drag APK to Android Studio device manager
```

## Features

- **Video Playback**: MP4, MKV, WebM, AVI, and more
- **Audio Playback**: MP3, FLAC, OGG, WAV, AAC
- **Streaming Support**: HTTP, HTTPS, HLS, DASH, RTSP
- **Gesture Controls**: Volume, brightness, seek
- **Playlists**: Create and manage video/audio collections
- **Folder Browser**: Organized media browsing
- **Subtitles**: Support for SRT and embedded subtitles
- **Playback Speed**: 0.5x to 2.0x
- **Settings**: Persistent preferences (dark theme, gestures, etc)
- **Modern UI**: Material 3 design with Jetpack Compose

## System Requirements

- **Android**: 5.0+ (API 21)
- **Storage**: ~50 MB
- **RAM**: 512 MB minimum
- **Processor**: ARM, ARM64, x86, x86_64

## Release Build (for Distribution)

### Step 1: Create Release Keystore

```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer_key
```

Follow the prompts to enter certificate information.

### Step 2: Build Release APK

```bash
export KEYSTORE_PATH=./release.keystore
export KEYSTORE_PASSWORD=your_password_here
export KEY_ALIAS=negoplayer_key
export KEY_PASSWORD=your_key_password

./build.sh release
```

### Step 3: Sign and Align

```bash
KEYSTORE_PATH=./release.keystore
KEY_ALIAS=negoplayer_key
APK=app/build/outputs/apk/release/app-release-unsigned.apk

# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore $KEYSTORE_PATH $APK $KEY_ALIAS

# Align APK
${ANDROID_SDK_ROOT}/build-tools/34.0.0/zipalign -v 4 $APK app-release-aligned.apk
```

**Final APK**: `app-release-aligned.apk`

## CI/CD Setup (GitHub Actions)

### 1. Create Keystore Secret

```bash
# Encode keystore
base64 release.keystore > keystore.b64

# Copy output and add to GitHub
# Repo Settings → Secrets → New repository secret
# Name: KEYSTORE_ENCODED
# Value: <paste base64 output>
```

### 2. Add Other Secrets

```
KEYSTORE_PASSWORD   → Your keystore password
KEY_ALIAS          → negoplayer_key
KEY_PASSWORD       → Your key password
```

### 3. Create Release

```bash
# Tag and push
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions automatically:
# 1. Builds release APK
# 2. Signs APK
# 3. Creates GitHub release
# 4. Uploads APK as release asset
```

## Project Structure

```
NegoPlayer/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/negoplayer/
│   │       │   ├── data/
│   │       │   │   ├── model/           # Data models
│   │       │   │   ├── repository/      # Data repositories
│   │       │   │   └── source/          # Data sources
│   │       │   ├── player/              # Media3 player engine
│   │       │   ├── ui/                  # Compose UI screens
│   │       │   ├── viewmodel/           # State management
│   │       │   └── utils/               # Utilities
│   │       ├── AndroidManifest.xml
│   │       └── res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── .github/workflows/                   # CI/CD pipelines
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── build.sh                             # Build script
├── DEVELOPMENT.md                       # Dev guide
└── README.md                            # Main readme
```

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Media**: Media3 (ExoPlayer)
- **Networking**: OkHttp
- **Image Loading**: Coil
- **Preferences**: DataStore
- **Async**: Coroutines
- **Build**: Gradle with Kotlin DSL

## Permissions

The app requests:
- `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` (Android 13+)
- `READ_EXTERNAL_STORAGE` (Android 12 and below)
- `INTERNET` (for streaming)
- `WAKE_LOCK` (playback optimization)
- `VIBRATE` (haptic feedback)

## Common Issues

### APK Won't Install
```bash
# Ensure correct target Android version
adb list targets
adb install -r app-debug.apk  # Reinstall
```

### Build Fails
```bash
./gradlew clean
./gradlew sync
./gradlew assembleDebug --info  # Verbose output
```

### Permission Errors
- Grant app permissions in Android settings
- Ensure Android 13+ has granular media permissions

### Playback Issues
- Check file format compatibility
- Verify media permissions granted
- Check media file integrity

## Development

See `DEVELOPMENT.md` for:
- Detailed architecture
- Testing procedures
- Contributing guidelines
- Performance optimization

## Troubleshooting Build Issues

### Keystore Not Found
```bash
# Verify keystore path
ls -la release.keystore

# Set full path if needed
export KEYSTORE_PATH=$(pwd)/release.keystore
```

### Gradle Sync Issues
```bash
# Update gradle
./gradlew wrapper --gradle-version latest

# Or rebuild gradle
./gradlew clean
./gradlew sync
```

### Version Conflicts
```bash
# Check dependency tree
./gradlew dependencies --configuration releaseRuntimeClasspath
```

## Performance Optimization

The project includes:
- Code minification via ProGuard/R8
- Resource shrinking for release builds
- Efficient layouts with Compose
- Lazy loading for media lists
- Coroutine-based async operations

**Optimized APK Size**: ~45 MB

## Security

- **Keystore Protection**: Store release.keystore securely
- **ProGuard**: Obfuscates code in release builds
- **HTTPS Only**: Network requests use HTTPS
- **Permissions**: Requested at runtime only when needed
- **Secrets**: Stored in GitHub Secrets, never in code

## Versioning

Use semantic versioning:
- `vMAJOR.MINOR.PATCH` (e.g., v1.2.3)
- MAJOR: Breaking changes
- MINOR: New features
- PATCH: Bug fixes

## License

See LICENSE file for details.

## Support

- **Issues**: GitHub Issues
- **Documentation**: See DEVELOPMENT.md
- **Developer**: Muhammad Umar

---

**Last Updated**: March 2024
**Current Version**: 1.0.0
**Status**: Production Ready ✓
