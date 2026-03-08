# NegoPlayer - Quick Build Guide

Fast reference for building, testing, and releasing NegoPlayer APK.

## One-Command Builds

### Debug APK (Development)
```bash
./build.sh debug
```
**Location**: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Production)
```bash
export KEYSTORE_PATH=./release.keystore
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=negoplayer_key
export KEY_PASSWORD=your_key_password

./build.sh release
```
**Location**: `app/build/outputs/apk/release/app-release-unsigned.apk`

## First-Time Setup

```bash
# 1. Clone repo
git clone https://github.com/Umarjaum/NegoPlayer.git
cd NegoPlayer

# 2. Make script executable
chmod +x build.sh

# 3. Sync gradle
./gradlew sync

# 4. Build & test
./build.sh debug
```

## Testing Checklist

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lint

# Build verification
./gradlew assembleDebug
```

## Creating Release Keystore (One-Time)

```bash
# Generate keystore
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer_key

# Verify keystore
keytool -list -v -keystore release.keystore \
  -alias negoplayer_key
```

## CI/CD Release Process

```bash
# 1. Update version in app/build.gradle.kts
versionCode = 2
versionName = "1.0.1"

# 2. Commit changes
git add .
git commit -m "Bump version to 1.0.1"
git push

# 3. Tag release
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1

# GitHub Actions automatically:
# - Builds release APK
# - Signs with keystore
# - Creates release
# - Uploads APK
```

## GitHub Secrets Setup (One-Time)

```bash
# 1. Encode keystore
base64 release.keystore > keystore.b64

# 2. Add to GitHub (Repo Settings → Secrets):
KEYSTORE_ENCODED      # Paste keystore.b64 content
KEYSTORE_PASSWORD     # Your keystore password
KEY_ALIAS             # negoplayer_key
KEY_PASSWORD          # Your key password
```

## File Locations After Build

### Debug Build
```
app/build/outputs/
├── apk/debug/
│   └── app-debug.apk              ← Main APK
├── androidsymbols/                ← Debug symbols
└── mapping/debug/                 ← Build artifacts
```

### Release Build
```
app/build/outputs/
├── apk/release/
│   ├── app-release-unsigned.apk   ← Before signing
│   └── app-release-aligned.apk    ← Final APK
└── bundle/                        ← App Bundle
```

## Size Information

| Build Type | Size | Optimizations |
|-----------|------|---|
| Debug | ~50 MB | None |
| Release | ~40-45 MB | ProGuard + Resources |
| Aligned | ~40-45 MB | Zipaligned |

## Installation on Device

```bash
# Via adb
adb install app/build/outputs/apk/debug/app-debug.apk

# Reinstall (if already installed)
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uninstall
adb uninstall com.negoplayer
```

## Common Issues & Fixes

### Build fails
```bash
./gradlew clean
./gradlew sync
./gradlew assembleDebug
```

### Tests fail
```bash
./gradlew testDebugUnitTest -info
```

### Keystore issues
```bash
# Forgot password?
# Unfortunately, must create new keystore and release as update

# Verify keystore
keytool -list -keystore release.keystore
```

### APK won't install
```bash
# Reinstall with force
adb install -r app-debug.apk

# Check if app is already installed
adb shell pm list packages | grep negoplayer
```

## Development Workflow

```bash
# 1. Create feature branch
git checkout -b feature/new-feature

# 2. Make changes
# ... edit code ...

# 3. Build and test
./build.sh debug
./gradlew testDebugUnitTest

# 4. Commit
git add .
git commit -m "feat: add new feature"
git push origin feature/new-feature

# 5. Create Pull Request on GitHub
# Wait for GitHub Actions checks to pass

# 6. Merge when approved
git checkout main
git pull
git merge feature/new-feature
```

## Version Management

### Update Version

Edit `app/build.gradle.kts`:
```kotlin
versionCode = 2              // Always increment
versionName = "1.0.1"        // Semantic versioning
```

### Tagging for Release
```bash
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1
```

### Version Numbering
- **1.0.0**: MAJOR.MINOR.PATCH
- MAJOR: Breaking changes
- MINOR: New features
- PATCH: Bug fixes

## Signing APK (Manual)

For local release builds:

```bash
# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore release.keystore \
  -storepass $KEYSTORE_PASSWORD \
  -keypass $KEY_PASSWORD \
  app-release-unsigned.apk negoplayer_key

# Align APK
${ANDROID_SDK_ROOT}/build-tools/34.0.0/zipalign -v 4 \
  app-release-unsigned.apk app-release-aligned.apk

# Verify
jarsigner -verify -verbose app-release-aligned.apk
```

## Documentation Quick Links

| Document | Purpose |
|----------|---------|
| README_SETUP.md | Complete setup & features |
| DEVELOPMENT.md | Architecture & workflow |
| SIGNING_GUIDE.md | Keystore & signing details |
| HILT_MIGRATION.md | Optional DI setup |
| PROJECT_CHECKLIST.md | Release checklist |
| IMPLEMENTATION_SUMMARY.md | Complete overview |

## System Requirements

- JDK 17+
- Android SDK (API 21-34)
- Gradle 8.0+
- Git
- ~2GB disk space for build cache

## Key Directories

```
app/
├── src/main/java/com/negoplayer/
│   ├── data/               # Data models & repos
│   ├── player/             # Media3 integration
│   ├── ui/                 # Compose screens
│   ├── viewmodel/          # State management
│   └── utils/              # Helpers
├── build.gradle.kts        # Build config
└── proguard-rules.pro      # Minification rules
```

## Gradle Tasks Summary

```bash
# Build
./gradlew assembleDebug         # Debug APK
./gradlew assembleRelease       # Release APK
./gradlew build                 # Full build

# Testing
./gradlew testDebugUnitTest     # Unit tests
./gradlew lint                  # Lint checks
./gradlew check                 # All checks

# Maintenance
./gradlew clean                 # Clean cache
./gradlew sync                  # Sync gradle
./gradlew dependencies          # Show dependencies
```

## Artifact Management

### GitHub Actions Artifacts
- Automatically uploaded
- Available for 90 days
- Accessible from Actions tab
- Download from release page

### Local Artifacts
```bash
# Keep organized
mkdir releases
cp app/build/outputs/apk/*/*.apk releases/
```

## Monitoring & Support

### Crash Reports
- Check Logcat: `adb logcat | grep ERROR`
- Monitor in Google Play Console (after upload)

### Performance
- Measure startup time
- Monitor memory usage (logcat)
- Check battery impact

### User Feedback
- Read reviews on Google Play
- Monitor GitHub issues
- Respond to bug reports

## Pre-Release Checklist

```
[ ] Version number bumped
[ ] All tests passing
[ ] No lint warnings
[ ] Tested on device
[ ] Keystore ready
[ ] Release notes written
[ ] GitHub Secrets updated
[ ] Release tagged properly
```

## Post-Release Steps

```bash
# After GitHub Actions completes:

# 1. Download APK from release
# 2. Verify APK (test install on device)
# 3. Upload to Google Play (if applicable)
# 4. Announce release
# 5. Monitor crash reports
# 6. Plan next features
```

## Emergency Hotfix

```bash
# 1. Create hotfix branch
git checkout -b hotfix/critical-bug

# 2. Fix issue
# ... edit code ...

# 3. Test thoroughly
./build.sh debug
./gradlew testDebugUnitTest

# 4. Bump patch version
# versionCode + 1, versionName "1.0.1" -> "1.0.2"

# 5. Release
git commit -m "fix: critical bug [hotfix]"
git push origin hotfix/critical-bug
git tag v1.0.2
git push origin v1.0.2
```

## Time Estimates

| Task | Time |
|------|------|
| Initial setup | 5 mins |
| Debug build | 2-3 mins |
| Release build | 4-5 mins |
| Running tests | 1-2 mins |
| Full CI/CD | 10-15 mins |

## Useful Aliases

Add to `.bashrc` or `.zshrc`:

```bash
alias build-debug='./build.sh debug'
alias build-release='./build.sh release'
alias test-all='./gradlew testDebugUnitTest lint'
alias clean='./gradlew clean'
alias sync='./gradlew sync'
```

---

**Ready to Build!** 🚀

For more details, see full documentation.
