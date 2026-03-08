# NegoPlayer - Local Execution Guide

## Important Note
The build automation script requires Android SDK, Gradle, and Java Development Kit (JDK) which are not available in web-based environments. You need to execute the build on your **local machine** where Android development tools are installed.

## Prerequisites

### 1. Install Required Tools

**Windows, macOS, or Linux:**
- Download and install [Android Studio](https://developer.android.com/studio)
- This includes: Android SDK, Gradle, and Java JDK

**Verify Installation:**
```bash
java -version
gradle -v
```

### 2. Clone Repository

```bash
# Navigate to your desired location
cd ~/projects

# Clone the repository
git clone https://github.com/Umarjaum/NegoPlayer.git
cd NegoPlayer

# Switch to media-player-app branch (if not default)
git checkout media-player-app
```

### 3. Set Up Environment Variables (Optional but Recommended)

Create a `.env` file in the project root:
```bash
KEYSTORE_PASSWORD=jaumumar@8627
KEY_ALIAS=negoplayer
KEY_PASSWORD=jaumumar@8627
ANDROID_SDK_ROOT=/path/to/android/sdk
ANDROID_HOME=/path/to/android/sdk
```

## Step-by-Step Execution

### Option 1: Automated Complete Build (Recommended)

**1. Make scripts executable:**
```bash
chmod +x *.sh
chmod +x build_and_release.py
```

**2. Run the complete automation:**
```bash
# Method A: Using Python script (Recommended)
python3 build_and_release.py

# Method B: Using bash script
bash full-release-automation.sh 1.0.0

# Method C: Interactive step-by-step
bash EXECUTE_COMMANDS.sh
```

**Estimated time: 20-30 minutes**

The automation will:
- ✓ Create release.keystore with your credentials
- ✓ Run all unit tests
- ✓ Build debug APK
- ✓ Build release APK (optimized)
- ✓ Build App Bundle for Google Play
- ✓ Sign all APKs automatically
- ✓ Commit changes to Git
- ✓ Push to GitHub
- ✓ Create release tag v1.0.0
- ✓ Generate release notes

### Option 2: Manual Step-by-Step Build

**Step 1: Create Release Keystore**
```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer \
  -dname "CN=umarjaum, OU=NegoPlayer, O=NegoPlayer, L=Pakistan, ST=Punjab, C=PK" \
  -storepass jaumumar@8627 \
  -keypass jaumumar@8627
```

**Step 2: Verify Keystore**
```bash
keytool -list -v -keystore release.keystore -storepass jaumumar@8627
```

**Step 3: Build Debug APK (Testing)**
```bash
./gradlew assembleDebug -x lint
# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Step 4: Build Release APK (Production)**
```bash
export KEYSTORE_PATH=$(pwd)/release.keystore
export KEYSTORE_PASSWORD=jaumumar@8627
export KEY_ALIAS=negoplayer
export KEY_PASSWORD=jaumumar@8627

./gradlew assembleRelease -x lint
# Output: app/build/outputs/apk/release/app-release.apk
```

**Step 5: Build App Bundle (For Google Play)**
```bash
export KEYSTORE_PATH=$(pwd)/release.keystore
export KEYSTORE_PASSWORD=jaumumar@8627
export KEY_ALIAS=negoplayer
export KEY_PASSWORD=jaumumar@8627

./gradlew bundleRelease -x lint
# Output: app/build/outputs/bundle/release/app-release.aab
```

**Step 6: Run Tests**
```bash
./gradlew testDebugUnitTest -x lint
```

**Step 7: Commit to Git**
```bash
git add -A
git commit -m "Release v1.0.0: NegoPlayer production-ready build

- Added professional app icons and logos
- Configured release signing with keystore
- Generated optimized APK
- All tests passing
- Documentation complete
- GitHub workflows configured"
```

**Step 8: Push to GitHub**
```bash
git push origin media-player-app
git tag -a v1.0.0 -m "NegoPlayer Release v1.0.0"
git push origin v1.0.0
```

### Option 3: Using Android Studio GUI

1. Open Android Studio
2. Click "Open" and select the NegoPlayer project folder
3. Wait for Gradle to sync
4. Go to **Build → Generate Signed Bundle/APK**
5. Select "APK" or "Android App Bundle"
6. Click "Next"
7. Click "Create new..." to create a new keystore
   - File name: `release.keystore`
   - Password: `jaumumar@8627`
   - Alias: `negoplayer`
   - Password: `jaumumar@8627`
   - Validity: `10000`
8. Click "Next" and select "Release"
9. Wait for build to complete
10. APK will be in `app/build/outputs/apk/release/`

## Build Output Files

After successful build, you'll find:

```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   └── app-debug.apk (~50 MB)
│   └── release/
│       └── app-release.apk (~40-45 MB)
└── bundle/
    └── release/
        └── app-release.aab (~30 MB)
```

### File Sizes
- **Debug APK**: ~50-55 MB (unoptimized, with ProGuard disabled)
- **Release APK**: ~40-45 MB (optimized with ProGuard)
- **App Bundle**: ~30 MB (optimized, multiple ABIs)

## Signing Information

Your keystore details:
```
Keystore: release.keystore
Store Password: jaumumar@8627
Key Alias: negoplayer
Key Password: jaumumar@8627
Algorithm: RSA 2048-bit
Validity: 10000 days (27+ years)
```

## Testing the APK

### On Physical Device
```bash
# Using ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Or for release
adb install app/build/outputs/apk/release/app-release.apk
```

### On Android Emulator
1. Open Android Emulator
2. Drag and drop APK onto emulator
3. Or use ADB command above

### Manual Testing (Android Device)
1. Transfer APK to device
2. Enable "Unknown Sources" in Settings
3. Open file manager and tap APK
4. Tap "Install"
5. Launch NegoPlayer from app drawer

## Troubleshooting

### Issue: "gradlew not found"
**Solution:** Make scripts executable:
```bash
chmod +x gradlew
```

### Issue: "ANDROID_SDK_ROOT not found"
**Solution:** Set environment variable:
```bash
export ANDROID_SDK_ROOT=$HOME/Android/Sdk  # macOS/Linux
# or
set ANDROID_SDK_ROOT=%USERPROFILE%\AppData\Local\Android\Sdk  # Windows
```

### Issue: "Java/JDK not found"
**Solution:** Install JDK 11+:
```bash
# macOS
brew install java

# Ubuntu/Debian
sudo apt install openjdk-11-jdk

# Windows - Download from oracle.com/java/technologies/downloads/
```

### Issue: Build fails with "Out of memory"
**Solution:** Increase Gradle heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

### Issue: "Keystore was tampered with"
**Solution:** Delete and recreate keystore:
```bash
rm release.keystore
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer \
  -storepass jaumumar@8627 \
  -keypass jaumumar@8627
```

## GitHub Release Creation (Manual)

After pushing to GitHub:

1. Visit: https://github.com/Umarjaum/NegoPlayer/releases
2. Click "Draft a new release"
3. Select tag: `v1.0.0`
4. Title: `NegoPlayer v1.0.0`
5. Description: Copy from `RELEASE_NOTES.md`
6. Upload files:
   - app-release.apk
   - app-release.aab
7. Publish release

## Google Play Store Submission

1. Sign up for [Google Play Console](https://play.google.com/console)
2. Create new application
3. Fill in app details
4. Upload App Bundle (app-release.aab)
5. Add screenshots and descriptions
6. Set pricing (free)
7. Submit for review

## Next Steps After Build

1. **Test thoroughly** on multiple devices
2. **Create GitHub Release** with APK/AAB files
3. **Submit to Google Play** (optional)
4. **Share download link** with users
5. **Monitor for issues** and create patches as needed

## Support

- Documentation: See INDEX.md for all guides
- Issues: Report at https://github.com/Umarjaum/NegoPlayer/issues
- Questions: Check QUICK_BUILD_GUIDE.md

---

**Ready to build? Start with:**
```bash
python3 build_and_release.py
```

or

```bash
bash full-release-automation.sh 1.0.0
```
