#!/bin/bash

# NegoPlayer Release Build Script
# Builds optimized release APK and AAB with code minification

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
KEYSTORE_FILE="release.keystore"
KEYSTORE_PASSWORD="jaumumar@8627"
KEY_ALIAS="negoplayer"
KEY_PASSWORD="jaumumar@8627"
GRADLE_TASKS=("clean" "assembleRelease" "bundleRelease")

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║    NegoPlayer Release Build Script      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Verify keystore exists
if [ ! -f "$KEYSTORE_FILE" ]; then
    echo -e "${RED}Error: Keystore file '$KEYSTORE_FILE' not found.${NC}"
    echo -e "${YELLOW}Please run './setup-keystore.sh' first.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Keystore verified${NC}"
echo -e "${GREEN}✓ Building Release APK and AAB${NC}"
echo ""

# Set environment variables for Gradle
export KEYSTORE_PATH=$(pwd)/$KEYSTORE_FILE
export KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD
export KEY_ALIAS=$KEY_ALIAS
export KEY_PASSWORD=$KEY_PASSWORD

echo "Environment Configuration:"
echo "  KEYSTORE_PATH=$KEYSTORE_PATH"
echo "  KEY_ALIAS=$KEY_ALIAS"
echo ""

# Run Gradle tasks
echo -e "${BLUE}Starting build process...${NC}"
echo ""

if command -v ./gradlew &> /dev/null; then
    GRADLE_CMD="./gradlew"
elif command -v gradlew.bat &> /dev/null; then
    GRADLE_CMD="gradlew.bat"
else
    GRADLE_CMD="gradle"
fi

echo "Using Gradle: $GRADLE_CMD"
echo ""

# Execute build
for task in "${GRADLE_TASKS[@]}"; do
    echo -e "${BLUE}Running: $GRADLE_CMD $task${NC}"
    $GRADLE_CMD $task --stacktrace
    echo ""
done

# Find and display output
APK_PATH="app/build/outputs/apk/release/"
AAB_PATH="app/build/outputs/bundle/release/"

echo -e "${GREEN}═══════════════════════════════════════${NC}"
echo -e "${GREEN}Build Complete!${NC}"
echo -e "${GREEN}═══════════════════════════════════════${NC}"
echo ""

if [ -d "$APK_PATH" ]; then
    echo -e "${GREEN}APK Files:${NC}"
    ls -lh "$APK_PATH"*.apk 2>/dev/null || echo "  No APK files found"
    echo ""
fi

if [ -d "$AAB_PATH" ]; then
    echo -e "${GREEN}App Bundle:${NC}"
    ls -lh "$AAB_PATH"*.aab 2>/dev/null || echo "  No AAB files found"
    echo ""
fi

echo -e "${GREEN}Build Artifacts:${NC}"
echo "  APK Location: $APK_PATH"
echo "  AAB Location: $AAB_PATH"
echo ""

echo -e "${GREEN}Next Steps:${NC}"
echo "  1. Test APK on devices"
echo "  2. Upload AAB to Google Play Console"
echo "  3. Create GitHub Release with APK"
echo ""

echo -e "${BLUE}Release build script completed successfully!${NC}"
