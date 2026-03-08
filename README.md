# NegoPlayer

A feature-rich Android media player built with **Jetpack Compose** and **Media3**, supporting local playback, streaming, advanced controls, and a modern Material 3 UI.

[![Build Status](https://github.com/Umarjaum/NegoPlayer/workflows/Tests/badge.svg)](https://github.com/Umarjaum/NegoPlayer/actions)
[![Release APK](https://github.com/Umarjaum/NegoPlayer/workflows/Build%20&%20Release%20APK/badge.svg)](https://github.com/Umarjaum/NegoPlayer/releases)
[![API Level](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Features

### Playback Support
- **Video Formats**: MP4, MKV, WebM, AVI, 3GP, FLV, MOV, WMV, MPEG, and more
- **Audio Formats**: MP3, FLAC, OGG, WAV, AAC, WMA, OPUS, M4A, and more
- **Streaming**: HTTP, HTTPS, HLS, DASH, RTSP, RTMP
- **Subtitles**: SRT format with styling options
- **Playback Speed**: 0.5x to 2.0x variable speed control

### Controls & Interaction
- **Gesture Controls**: Swipe to adjust volume, brightness, and seek
- **Double-Tap Seek**: ±10 seconds with double-tap on left/right
- **Full-Screen Player**: Immersive viewing experience
- **Mini Player**: Music playback widget
- **Playback Position**: Remember where you left off

### Media Management
- **Video Library**: Browse all device videos
- **Audio Library**: Browse all device audio files
- **Folder Navigation**: Organized folder-based browsing
- **Search**: Quick search for media by title
- **Sorting**: Multiple sort options (name, date, size, duration)
- **Playlists**: Create and manage media playlists

### UI/UX
- **Material 3 Design**: Modern, beautiful interface
- **Dark/Light Themes**: Switchable app themes
- **Persistent Settings**: Save preferences
- **Permission Management**: Runtime permissions with clear prompts
- **Smooth Animations**: Polished transitions and interactions

## Technology Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Media Playback** | Media3 (ExoPlayer) |
| **Async** | Kotlin Coroutines |
| **Image Loading** | Coil |
| **Networking** | OkHttp |
| **Preferences** | DataStore |
| **Build System** | Gradle (Kotlin DSL) |
| **CI/CD** | GitHub Actions |

## Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/Umarjaum/NegoPlayer.git
cd NegoPlayer
```

### 2. Build Debug APK
```bash
chmod +x build.sh
./build.sh debug
```

### 3. Install on Device
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

For detailed setup instructions, see [README_SETUP.md](README_SETUP.md).

## System Requirements

- **Minimum Android Version**: 5.0 (API 21)
- **Target Android Version**: 14 (API 34)
- **RAM**: 512 MB minimum
- **Storage**: ~50 MB
- **Supported Architectures**: arm64-v8a, armeabi-v7a, x86, x86_64

## Project Structure

```
NegoPlayer/
├── .github/
│   ├── workflows/              # CI/CD pipelines
│   └── ISSUE_TEMPLATE/         # GitHub templates
├── app/src/main/java/com/negoplayer/
│   ├── data/                   # Data layer
│   ├── player/                 # Media3 integration
│   ├── ui/                     # Jetpack Compose screens
│   ├── viewmodel/              # State management
│   └── utils/                  # Utilities
├── .github/workflows/          # GitHub Actions
├── build.sh                    # Build script
└── docs/                       # Documentation
```

## Documentation

| Document | Purpose |
|----------|---------|
| [README_SETUP.md](README_SETUP.md) | Complete setup and features guide |
| [QUICK_BUILD_GUIDE.md](QUICK_BUILD_GUIDE.md) | Fast reference for building |
| [DEVELOPMENT.md](DEVELOPMENT.md) | Development guidelines and architecture |
| [SIGNING_GUIDE.md](SIGNING_GUIDE.md) | APK signing and distribution |
| [HILT_MIGRATION.md](HILT_MIGRATION.md) | Optional DI enhancement guide |
| [PROJECT_CHECKLIST.md](PROJECT_CHECKLIST.md) | Completion and release checklist |
| [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) | Complete project overview |

## Building & Release

### Automated Complete Release
The easiest way to build, sign, and release is using the automated script:

```bash
# Make scripts executable
chmod +x *.sh

# Run complete automation (builds, signs, commits, pushes, and tags)
bash full-release-automation.sh 1.0.0
```

This will:
- ✅ Create keystore (if needed)
- ✅ Build optimized release APK and AAB
- ✅ Sign with release keystore
- ✅ Run tests
- ✅ Commit changes to Git
- ✅ Push to GitHub
- ✅ Create release tag
- ✅ Generate release notes

### Manual Development Build
```bash
./build.sh debug
# Output: app/build/outputs/apk/debug/app-debug.apk (~50 MB)
```

### Manual Release Build
```bash
# Step 1: Setup keystore (first time only)
bash setup-keystore.sh

# Step 2: Build release
bash build-release.sh
# Output: app/build/outputs/apk/release/app-release.apk (~40-45 MB)

# Step 3: Commit and push
bash git-release.sh 1.0.0

# Step 4: Create GitHub release
# Visit: https://github.com/Umarjaum/NegoPlayer/releases
# Tag: v1.0.0
```

### Build Scripts

| Script | Purpose |
|--------|---------|
| `build.sh` | Quick build for debug/release |
| `setup-keystore.sh` | Create release signing keystore |
| `build-release.sh` | Build optimized release APK |
| `git-release.sh` | Automate Git commits, push, and tagging |
| `full-release-automation.sh` | Complete workflow (all above) |
| `EXECUTE_COMMANDS.sh` | Interactive step-by-step execution |

For detailed signing instructions, see [SIGNING_GUIDE.md](SIGNING_GUIDE.md).

## Testing

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run lint checks
./gradlew lint

# Full build verification
./gradlew build
```

## CI/CD Pipeline

The project includes automated workflows:

1. **test.yml** - Runs on every push and PR
   - Unit tests
   - Lint checks
   - Build verification

2. **build-and-release.yml** - Runs on version tags
   - Release APK build
   - APK signing
   - Automatic release creation
   - GitHub release publication

## Architecture

NegoPlayer uses a clean, layered architecture:

- **UI Layer**: Jetpack Compose screens with Material 3
- **ViewModel Layer**: State management with Kotlin Flow
- **Repository Layer**: Data abstraction and business logic
- **Data Source Layer**: Local device and network media sources

This architecture ensures:
- ✅ Separation of concerns
- ✅ Easy testing
- ✅ Maintainability
- ✅ Extensibility

## Performance

- **Startup Time**: < 3 seconds
- **APK Size**: 40-50 MB (optimized with ProGuard)
- **Memory Usage**: Efficient with coroutines
- **Battery Impact**: Minimal with proper lifecycle management

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

See [DEVELOPMENT.md](DEVELOPMENT.md) for development guidelines.

## Issues & Support

- **Bug Reports**: Use [GitHub Issues](https://github.com/Umarjaum/NegoPlayer/issues)
- **Feature Requests**: Use GitHub Issues with `[FEATURE]` tag
- **Discussions**: Use [GitHub Discussions](https://github.com/Umarjaum/NegoPlayer/discussions)

## Releases

Latest releases available on [GitHub Releases](https://github.com/Umarjaum/NegoPlayer/releases).

### Release Process

1. Update version in `app/build.gradle.kts`
2. Tag release: `git tag v1.0.0`
3. Push tag: `git push origin v1.0.0`
4. GitHub Actions automatically builds and creates release

For detailed release instructions, see [SIGNING_GUIDE.md](SIGNING_GUIDE.md).

## Roadmap

### Completed
- ✅ Video playback
- ✅ Audio playback
- ✅ Streaming support
- ✅ Gesture controls
- ✅ Modern UI with Compose
- ✅ Settings persistence
- ✅ Material 3 theme

### Future Enhancements
- [ ] Advanced subtitle editor
- [ ] Hilt dependency injection
- [ ] Cloud sync
- [ ] Library backup/restore
- [ ] Custom themes
- [ ] Widget support
- [ ] Equalizer
- [ ] Visualizers

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## Author

**Muhammad Umar**
- GitHub: [@Umarjaum](https://github.com/Umarjaum)
- Email: [contact info]

## Acknowledgments

- **Media3**: For excellent playback library
- **Jetpack Compose**: For modern UI toolkit
- **Kotlin**: For excellent language features
- **Android Community**: For continuous support

## Disclaimer

This player is optimized for personal use. When distributing through Google Play or other stores, ensure compliance with:
- Privacy policies
- Copyright laws
- Platform guidelines
- Terms of service

## Status

**Project Status**: Production Ready ✅

- ✅ All features implemented
- ✅ Comprehensive testing setup
- ✅ Full documentation
- ✅ Automated CI/CD
- ✅ Release ready

---

**Version**: 1.0.0  
**Last Updated**: March 2024  
**API Level**: 21-34  
**Kotlin**: 1.9.20  

**[Quick Start Guide](QUICK_BUILD_GUIDE.md)** | **[Full Documentation](README_SETUP.md)** | **[GitHub Repo](https://github.com/Umarjaum/NegoPlayer)**
