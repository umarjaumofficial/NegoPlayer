# NegoPlayer - Start Here 🚀

Complete step-by-step instructions to build, sign, and release your NegoPlayer APK with all automation scripts.

## Prerequisites

Before starting, ensure you have:
- Git installed
- JDK 11+ installed
- Android SDK (API 21-34)
- ~2GB free disk space

Verify installation:
```bash
git --version
java -version
```

## Project Overview

**NegoPlayer** is a feature-rich Android media player with:
- Advanced video/audio playback (MP4, MKV, MP3, FLAC, etc.)
- HLS/DASH/RTSP streaming
- Material 3 UI with Jetpack Compose
- Gesture-based controls
- Modern architecture with MVVM + Repository pattern

## Files Included

### Automation Scripts
| File | Purpose |
|------|---------|
| `setup-keystore.sh` | Creates release signing keystore |
| `build-release.sh` | Builds optimized APK and AAB |
| `git-release.sh` | Commits, pushes, and tags release |
| `full-release-automation.sh` | Runs all steps automatically |
| `EXECUTE_COMMANDS.sh` | Interactive step-by-step guide |

### Documentation
| File | Purpose |
|------|---------|
| `README.md` | Project overview |
| `README_SETUP.md` | Complete setup guide |
| `QUICK_BUILD_GUIDE.md` | Fast reference |
| `SIGNING_GUIDE.md` | Detailed signing instructions |
| `DEVELOPMENT.md` | Development guidelines |
| `IMPLEMENTATION_SUMMARY.md` | Complete project summary |

### Logo & Branding
- App icon: `app/src/main/res/mipmap-*/ic_launcher.png`
- NegoPlayer red and black chevron logo

### Build Configuration
- Gradle signing config ready
- ProGuard minification enabled
- Multiple architecture support (arm64, armv7, x86)
- GitHub Actions CI/CD workflows

## Quick Start (5 minutes)

### Option 1: Fully Automated (Recommended)

```bash
# Make all scripts executable
chmod +x *.sh

# Run the complete automation
bash full-release-automation.sh 1.0.0
```

This automatically:
1. Creates keystore (if needed)
2. Builds release APK
3. Commits changes to Git
4. Pushes to GitHub
5. Creates release tag (v1.0.0)
6. Generates release notes

Then manually:
1. Go to: https://github.com/Umarjaum/NegoPlayer/releases
2. Click on tag v1.0.0
3. Create release from tag
4. Upload APK from `app/build/outputs/apk/release/`
5. Publish release

### Option 2: Step-by-Step with Interactive Guide

```bash
chmod +x *.sh
bash EXECUTE_COMMANDS.sh
```

Follow the interactive prompts for each step.

### Option 3: Manual Control

```bash
chmod +x *.sh

# Step 1: Setup keystore (first time only)
bash setup-keystore.sh

# Step 2: Build release APK
bash build-release.sh

# Step 3: Commit and push
bash git-release.sh 1.0.0

# Step 4: Create release manually on GitHub
# Visit: https://github.com/Umarjaum/NegoPlayer/releases
```

## Signing Credentials (Already Configured)

The keystore is configured with:
- **Keystore**: `release.keystore`
- **Alias**: `negoplayer`
- **Owner**: umarjaum
- **Validity**: 10,000 days (~27 years)

These are automatically used in the build process.

## Build Outputs

After building, find:
- **APK**: `app/build/outputs/apk/release/app-release.apk` (~40-45 MB, optimized)
- **AAB**: `app/build/outputs/bundle/release/app.aab` (for Google Play)

## GitHub Workflow

### 1. First Time Setup
```bash
# Initialize git (if not already initialized)
git init
git remote add origin https://github.com/Umarjaum/NegoPlayer.git
git branch -M main
```

### 2. Commit and Push with Automation
```bash
bash full-release-automation.sh 1.0.0
```

### 3. Create GitHub Release (Manual)
```bash
# Go to: https://github.com/Umarjaum/NegoPlayer
# Click on "Releases"
# Click "Create a new release"
# Select tag: v1.0.0
# Add title: NegoPlayer v1.0.0
# Add description from: RELEASE_NOTES_v1.0.0.md
# Upload APK file
# Publish release
```

## Commands Quick Reference

### Build Commands
```bash
# Debug build
./build.sh debug

# Release build
./build.sh release

# Clean build
./gradlew clean build
```

### Test Commands
```bash
# Unit tests
./gradlew testDebugUnitTest

# All tests
./gradlew test

# Lint
./gradlew lint
```

### Git Commands (Automated by scripts)
```bash
# These are automatically run by git-release.sh
git add -A
git commit -m "chore(release): NegoPlayer v1.0.0"
git push origin main
git tag -a v1.0.0 -m "Release NegoPlayer v1.0.0"
git push origin v1.0.0
```

## Troubleshooting

### Keystore Issues
```bash
# Regenerate keystore
rm release.keystore
bash setup-keystore.sh
```

### Build Errors
```bash
# Clean and rebuild
./gradlew clean build

# View full error log
./gradlew build --stacktrace --info
```

### Git Push Failed
```bash
# Verify remote
git remote -v

# Update remote
git remote set-url origin https://github.com/Umarjaum/NegoPlayer.git

# Try push again
git push origin main
```

### APK Not Found
```bash
# Check build output
ls -lah app/build/outputs/apk/release/

# Rebuild
bash build-release.sh
```

## Configuration Files

### Gradle Settings
- **Project Root**: `build.gradle.kts`
- **App Module**: `app/build.gradle.kts` (signing config included)
- **Properties**: `gradle.properties`

### Android Manifest
- **Location**: `app/src/main/AndroidManifest.xml`
- **Permissions**: All required for media playback and file access

### ProGuard Configuration
- **Location**: `app/proguard-rules.pro`
- **Purpose**: Code obfuscation and optimization
- **Status**: Enabled in release builds

## Release Checklist

Before releasing, verify:

- [ ] App icon/logo updated (✓ Done)
- [ ] Signing configured (✓ Done)
- [ ] Version updated in gradle (check `app/build.gradle.kts`)
- [ ] Tests passing (`./gradlew test`)
- [ ] No lint errors (`./gradlew lint`)
- [ ] APK tested on device
- [ ] GitHub repository updated
- [ ] Release notes prepared
- [ ] Tag created on GitHub

## Distribution Paths

### Google Play Store
1. Upload AAB: `app/build/outputs/bundle/release/app.aab`
2. Follow Google Play guidelines
3. Requires Play Developer account

### GitHub Releases
1. Create release from tag
2. Upload APK file
3. Add release notes
4. Publish

### Direct APK Distribution
1. Host APK on server
2. Create download link
3. Share with users
4. Users enable "Unknown Sources" to install

## Key Features

✅ **Video Playback**: MP4, MKV, WebM, AVI, and more
✅ **Audio Playback**: MP3, FLAC, OGG, WAV, AAC, and more  
✅ **Streaming**: HLS, DASH, RTSP, HTTP
✅ **Gesture Controls**: Swipe for volume, brightness, seek
✅ **Modern UI**: Material 3 with Jetpack Compose
✅ **Optimized**: ProGuard minification, 40-45 MB
✅ **Well-Tested**: Unit and instrumented tests included
✅ **CI/CD Ready**: GitHub Actions workflows configured

## Next Steps

1. **Review Code**: Check `app/src/main/java/` for architecture
2. **Customize**: Modify strings, colors, and branding as needed
3. **Test**: Run tests and build locally
4. **Release**: Use automation scripts to build and push
5. **Distribute**: Publish to GitHub Releases or Google Play

## Support & Documentation

- **Setup Guide**: [README_SETUP.md](README_SETUP.md)
- **Quick Reference**: [QUICK_BUILD_GUIDE.md](QUICK_BUILD_GUIDE.md)
- **Signing Details**: [SIGNING_GUIDE.md](SIGNING_GUIDE.md)
- **Development**: [DEVELOPMENT.md](DEVELOPMENT.md)
- **Project Summary**: [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)

## Important Notes

### Keystore Security
- `release.keystore` is required for signing
- Keep it safe and backed up
- Don't commit to public repositories
- Use environment variables in CI/CD

### Version Management
- Update version in `app/build.gradle.kts` before release
- Use semantic versioning (major.minor.patch)
- Tag releases with `v` prefix

### ProGuard Minification
- Automatically enabled in release builds
- Reduces APK size by ~30-40%
- Obfuscates code for security
- Keep mapping file for crash debugging

## Final Command to Execute Everything

```bash
# One command to rule them all!
chmod +x *.sh && bash full-release-automation.sh 1.0.0
```

---

**Ready to Build & Release? Let's Go! 🚀**

For more details, see [README.md](README.md) and [QUICK_BUILD_GUIDE.md](QUICK_BUILD_GUIDE.md).
