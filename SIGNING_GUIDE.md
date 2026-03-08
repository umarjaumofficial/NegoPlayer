# NegoPlayer APK Signing Guide

Complete guide for creating and managing release keystores for NegoPlayer APK signing.

## Overview

Signing an APK is required to publish it on Google Play Store or distribute it directly. This guide covers:
1. Creating a release keystore
2. Building signed APK locally
3. Setting up CI/CD signing
4. Best practices for key management

## Prerequisites

- JDK 17 or higher
- Keystore generation tool (keytool - bundled with JDK)
- Android SDK build-tools (for zipalign)

## Step 1: Create Release Keystore

### Generate Keystore

```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer_key
```

### Prompts

You'll be asked for:
- **Keystore password**: Create a strong password (20+ chars recommended)
- **Key password**: Can be same as keystore password
- **Owner name**: Your name or organization
- **Organization**: Company/Organization name
- **Location**: City
- **State/Province**: State/Province
- **Country Code**: 2-letter country code (e.g., US)

### Example

```
Enter keystore password: MyStrongPassword123!
Re-enter new password: MyStrongPassword123!
What is your first and last name? [Unknown]: John Doe
What is the name of your organizational unit? [Unknown]: Development
What is the name of your organization? [Unknown]: My Company
What is the name of your City or Locality? [Unknown]: San Francisco
What is the name of your State or Province? [Unknown]: California
What is the two-letter country code for this unit? [Unknown]: US
Is CN=John Doe, OU=Development, O=My Company, L=San Francisco, ST=California, C=US correct?
[no]: yes
```

## Step 2: Verify Keystore

```bash
# List keystore contents
keytool -list -v -keystore release.keystore -alias negoplayer_key

# Output shows:
# - Alias
# - Entry type
# - Certificate fingerprint (SHA-256)
# - Creation date
# - Validity period
```

## Step 3: Build Release APK Locally

### Option A: Using Build Script

```bash
export KEYSTORE_PATH=$(pwd)/release.keystore
export KEYSTORE_PASSWORD=MyStrongPassword123
export KEY_ALIAS=negoplayer_key
export KEY_PASSWORD=MyStrongPassword123

./build.sh release
```

### Option B: Using Gradle Directly

```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=./release.keystore \
  -Pandroid.injected.signing.store.password=MyStrongPassword123 \
  -Pandroid.injected.signing.key.alias=negoplayer_key \
  -Pandroid.injected.signing.key.password=MyStrongPassword123
```

### Option C: Using Gradle Properties

Create or edit `gradle.properties`:

```properties
# Signing configuration
RELEASE_STORE_FILE=./release.keystore
RELEASE_STORE_PASSWORD=MyStrongPassword123
RELEASE_KEY_ALIAS=negoplayer_key
RELEASE_KEY_PASSWORD=MyStrongPassword123
```

Then build:

```bash
./gradlew assembleRelease
```

## Step 4: Sign and Align APK

```bash
# Variables
APK_UNSIGNED="app/build/outputs/apk/release/app-release-unsigned.apk"
APK_SIGNED="app/build/outputs/apk/release/app-release.apk"
APK_FINAL="app/build/outputs/apk/release/app-release-aligned.apk"
KEYSTORE_PATH="./release.keystore"
KEYSTORE_PASSWORD="MyStrongPassword123"
KEY_ALIAS="negoplayer_key"
KEY_PASSWORD="MyStrongPassword123"

# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore $KEYSTORE_PATH \
  -storepass $KEYSTORE_PASSWORD \
  -keypass $KEY_PASSWORD \
  $APK_UNSIGNED $KEY_ALIAS

# Align APK (for optimization)
${ANDROID_SDK_ROOT}/build-tools/34.0.0/zipalign -v 4 \
  $APK_SIGNED $APK_FINAL
```

### Verify Signed APK

```bash
# Check signature
jarsigner -verify -verbose -certs \
  app/build/outputs/apk/release/app-release-aligned.apk

# Output should show:
# - Entry count
# - All entries signed
# - Certificate details
```

## Step 5: Setup GitHub Actions Signing

### A. Prepare Keystore for Secrets

```bash
# Encode keystore to base64
base64 -i release.keystore -o keystore.b64

# Or on macOS
base64 release.keystore > keystore.b64

# Copy the base64 content
cat keystore.b64
```

### B. Add Repository Secrets

1. Go to GitHub repository
2. Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Add secrets:

| Name | Value |
|------|-------|
| KEYSTORE_ENCODED | Base64-encoded keystore |
| KEYSTORE_PASSWORD | Your keystore password |
| KEY_ALIAS | negoplayer_key |
| KEY_PASSWORD | Your key password |

### C. Workflow Decoding

In `.github/workflows/build-and-release.yml`:

```yaml
- name: Decode Keystore
  env:
    KEYSTORE_ENCODED: ${{ secrets.KEYSTORE_ENCODED }}
    KEYSTORE_PATH: ${{ runner.temp }}/release.keystore
  run: |
    echo "$KEYSTORE_ENCODED" | base64 -d > $KEYSTORE_PATH
```

## Step 6: Release on Google Play

### Prerequisites

- Google Play Developer account ($25 one-time fee)
- Signed APK

### Steps

1. Create new app in Google Play Console
2. Fill app details (name, description, screenshots, etc)
3. Set up pricing and distribution
4. Upload signed APK to "Internal testing" first
5. Test thoroughly
6. Move to "Closed testing" → "Open testing" → "Production"
7. Submit for review

### First Release

Google Play will review your first release (24-72 hours typically).

## Best Practices

### Security

1. **Keystore Storage**
   - Keep locally: Use encrypted storage, password manager
   - Use GitHub Secrets: Never commit keystore to repo
   - Backup: Store secure backup offline (encrypted USB, safe deposit box)

2. **Key Rotation**
   - Keep same key for app lifetime (Google Play requirement)
   - Never lose the key - you won't be able to update your app

3. **Password Management**
   - Use strong passwords (20+ characters)
   - Store securely (password manager like 1Password, LastPass)
   - Don't share with team members

### Version Management

```bash
# Update version in app/build.gradle.kts
versionCode = 2
versionName = "1.0.1"

# Tag release
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1
```

### APK Testing

Before publishing:

1. Install on multiple devices
2. Test all features:
   - Video playback (local & streaming)
   - Audio playback
   - Permissions
   - Navigation
   - Settings persistence
3. Test on different Android versions (API 21+)
4. Check for crashes (logcat)

### Monitoring

After release:

1. Monitor Google Play Console
2. Check crash reports
3. Read user reviews
4. Fix bugs and release updates

## Troubleshooting

### Keystore Issues

#### Forgot password
```bash
# Unfortunately, cannot recover if lost
# Must create new keystore and release as update

# Create new keystore with different alias
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias negoplayer_key_v2
```

#### Password verification failed
```bash
# Check correct password
keytool -list -v -keystore release.keystore

# Try again if you made a typo
```

#### Keystore corrupted
```bash
# Verify keystore
keytool -list -v -keystore release.keystore

# If corrupted, restore from backup
cp release.keystore.backup release.keystore
```

### Signing Issues

#### Invalid certificate
- Ensure SHA-256 digest algorithm
- Verify keystore path is correct
- Check passwords match

#### APK won't install
```bash
# Check device compatibility
adb logcat | grep "error"

# Verify APK structure
zipinfo app-release-aligned.apk | grep "META-INF"
```

## Useful Commands

```bash
# List all keys in keystore
keytool -list -keystore release.keystore

# Display specific key details
keytool -list -v -keystore release.keystore -alias negoplayer_key

# Export certificate
keytool -export -keystore release.keystore \
  -alias negoplayer_key \
  -file cert.pem

# Change password
keytool -storepasswd -keystore release.keystore

# Delete key
keytool -delete -keystore release.keystore -alias key_name
```

## Reference

- [Android App Signing Documentation](https://developer.android.com/studio/publish/app-signing)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Keytool Documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html)
- [Jarsigner Documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jarsigner.html)

---

**Last Updated**: March 2024
**Version**: 1.0
