#!/bin/bash

# NegoPlayer - Complete Execution Commands
# Run all necessary commands in sequence

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  NegoPlayer Complete Build Execution   ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Make scripts executable
echo -e "${YELLOW}Making scripts executable...${NC}"
chmod +x setup-keystore.sh build-release.sh git-release.sh full-release-automation.sh build.sh

echo -e "${GREEN}✓ Scripts are now executable${NC}"
echo ""

# Step 1: Setup Keystore
echo -e "${BLUE}Step 1: Setting up keystore${NC}"
echo "Run: bash setup-keystore.sh"
echo ""
read -p "Press Enter to continue with keystore setup..."

bash setup-keystore.sh || {
    echo -e "${RED}Keystore setup failed${NC}"
    exit 1
}

echo -e "${GREEN}✓ Keystore setup complete${NC}"
echo ""

# Step 2: Clean and Build
echo -e "${BLUE}Step 2: Building release APK and AAB${NC}"
echo "Run: bash build-release.sh"
echo ""
read -p "Press Enter to continue with build..."

bash build-release.sh || {
    echo -e "${RED}Build failed${NC}"
    exit 1
}

echo -e "${GREEN}✓ Build complete${NC}"
echo ""

# Step 3: Commit and Push
echo -e "${BLUE}Step 3: Committing and pushing to GitHub${NC}"
echo "Run: bash git-release.sh [VERSION]"
echo ""
read -p "Enter version number (default: 1.0.0): " VERSION
VERSION=${VERSION:-1.0.0}

bash git-release.sh "$VERSION" || {
    echo -e "${RED}Git operations failed${NC}"
    exit 1
}

echo -e "${GREEN}✓ Git operations complete${NC}"
echo ""

# Summary
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo -e "${GREEN}All tasks completed successfully!${NC}"
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo ""

echo -e "${BLUE}Summary:${NC}"
echo "  ✓ Keystore created"
echo "  ✓ APK built and signed"
echo "  ✓ Code minified and optimized"
echo "  ✓ Changes committed to Git"
echo "  ✓ Pushed to GitHub"
echo "  ✓ Release tag created"
echo ""

echo -e "${BLUE}What's Next:${NC}"
echo "  1. Open: https://github.com/Umarjaum/NegoPlayer/releases"
echo "  2. Find tag: v$VERSION"
echo "  3. Click 'Create release from tag'"
echo "  4. Add description from RELEASE_NOTES_v$VERSION.txt"
echo "  5. Upload APK: app/build/outputs/apk/release/app-release.apk"
echo "  6. Click 'Publish release'"
echo ""

echo -e "${GREEN}Your app is ready for distribution! 🚀${NC}"
