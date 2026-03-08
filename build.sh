#!/bin/bash

# NegoPlayer Build Script
# Usage: ./build.sh [debug|release]

set -e

BUILD_TYPE="${1:-debug}"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "====================================="
echo "NegoPlayer APK Build Script"
echo "====================================="
echo "Build Type: $BUILD_TYPE"
echo "Project Root: $PROJECT_ROOT"
echo ""

# Change to project root
cd "$PROJECT_ROOT"

# Grant execute permission
chmod +x gradlew

echo "[1/4] Cleaning previous builds..."
./gradlew clean

echo "[2/4] Running tests..."
./gradlew testDebugUnitTest

echo "[3/4] Building $BUILD_TYPE APK..."
if [ "$BUILD_TYPE" = "release" ]; then
    echo "Building release APK..."
    echo ""
    echo "Note: For release builds, ensure keystore is configured:"
    echo "  Export these environment variables:"
    echo "    - KEYSTORE_PATH: Path to release.keystore"
    echo "    - KEYSTORE_PASSWORD: Keystore password"
    echo "    - KEY_ALIAS: Key alias in keystore"
    echo "    - KEY_PASSWORD: Key password"
    echo ""
    
    # Check if keystore is configured
    if [ -z "$KEYSTORE_PATH" ]; then
        echo "ERROR: KEYSTORE_PATH not set. Cannot build release APK."
        exit 1
    fi
    
    ./gradlew assembleRelease --no-daemon
else
    ./gradlew assembleDebug --no-daemon
fi

echo "[4/4] Build complete!"
echo ""

# Show APK info
if [ "$BUILD_TYPE" = "release" ]; then
    APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
else
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
fi

if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo "✓ APK built successfully!"
    echo "  Location: $APK_PATH"
    echo "  Size: $APK_SIZE"
else
    echo "✗ APK build failed"
    exit 1
fi
