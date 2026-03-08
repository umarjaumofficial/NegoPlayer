#!/bin/bash

# NegoPlayer - RUN NOW - Complete Execution
# This is the main entry point - Run this ONE script to do everything!

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Banner
clear
echo -e "${CYAN}"
cat << "EOF"
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║    🎬 NegoPlayer - Complete Build & Release Automation 🎬     ║
║                                                                ║
║    One Script to Build Everything!                            ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
EOF
echo -e "${NC}"
echo ""

# Get version from user
echo -e "${BLUE}Enter version number (default: 1.0.0):${NC}"
read -r VERSION
VERSION=${VERSION:-1.0.0}

echo ""
echo -e "${GREEN}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}NegoPlayer v$VERSION Release${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Step 1: Make scripts executable
echo -e "${BLUE}Step 1: Making scripts executable...${NC}"
chmod +x setup-keystore.sh build-release.sh git-release.sh full-release-automation.sh EXECUTE_COMMANDS.sh build.sh
echo -e "${GREEN}✓ Scripts are executable${NC}"
echo ""

# Step 2: Run full automation
echo -e "${BLUE}Step 2: Running full automation (this may take 15-20 minutes)...${NC}"
echo -e "${YELLOW}This will:${NC}"
echo "  • Create keystore (if needed)"
echo "  • Build optimized release APK"
echo "  • Run tests"
echo "  • Commit changes to Git"
echo "  • Push to GitHub"
echo "  • Create release tag"
echo "  • Generate release notes"
echo ""
read -p "Continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Cancelled by user${NC}"
    exit 1
fi

echo ""
bash full-release-automation.sh "$VERSION"

# Final summary
echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✓ Build and Release Complete!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${BLUE}What was done:${NC}"
echo "  ✓ Keystore created (if needed)"
echo "  ✓ APK built and optimized (40-45 MB)"
echo "  ✓ APK signed with your credentials"
echo "  ✓ Tests executed"
echo "  ✓ Changes committed to Git"
echo "  ✓ Pushed to GitHub repository"
echo "  ✓ Release tag created: v$VERSION"
echo "  ✓ Release notes generated"
echo ""

echo -e "${BLUE}What's next:${NC}"
echo "  1. Go to: https://github.com/Umarjaum/NegoPlayer"
echo "  2. Click: Releases"
echo "  3. Find tag: v$VERSION"
echo "  4. Click: Create release from tag"
echo "  5. Add title: NegoPlayer v$VERSION"
echo "  6. Paste description from: RELEASE_NOTES_v${VERSION}.md"
echo "  7. Upload APK: app/build/outputs/apk/release/app-release.apk"
echo "  8. Click: Publish release"
echo ""

echo -e "${YELLOW}File Locations:${NC}"
echo "  APK:        app/build/outputs/apk/release/app-release.apk"
echo "  AAB:        app/build/outputs/bundle/release/app.aab"
echo "  Keystore:   release.keystore"
echo "  Notes:      RELEASE_NOTES_v${VERSION}.md"
echo ""

echo -e "${GREEN}Your NegoPlayer app is ready to ship! 🚀${NC}"
echo ""
echo "Questions? See: START_HERE.md or COMPLETE_SETUP_GUIDE.md"
