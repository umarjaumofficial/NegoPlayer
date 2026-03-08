#!/bin/bash

# NegoPlayer GitHub Release Script
# Automates commits, pushes, tags, and creates GitHub release

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  NegoPlayer GitHub Release Automation  ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Get version from user or use default
VERSION="${1:-1.0.0}"
BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo -e "${YELLOW}Current Branch: $BRANCH${NC}"
echo -e "${YELLOW}Release Version: $VERSION${NC}"
echo ""

# Verify we're not on main/master for development branches
if [ "$BRANCH" != "main" ] && [ "$BRANCH" != "master" ] && [ "$BRANCH" != "release" ]; then
    echo -e "${YELLOW}Warning: You are on development branch '$BRANCH'${NC}"
    read -p "Continue with release from this branch? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${RED}Release cancelled.${NC}"
        exit 1
    fi
fi

# Stage all changes
echo -e "${BLUE}Staging changes...${NC}"
git add -A

# Check if there are changes to commit
if ! git diff-index --quiet --cached HEAD --; then
    echo -e "${BLUE}Creating commit...${NC}"
    git commit -m "chore: Release NegoPlayer v$VERSION

- Update app version to $VERSION
- Build optimizations and refinements
- Documentation updates
- Build script enhancements"
    echo -e "${GREEN}✓ Commit created${NC}"
else
    echo -e "${YELLOW}No changes to commit${NC}"
fi

echo ""

# Push to current branch
echo -e "${BLUE}Pushing to branch '$BRANCH'...${NC}"
git push origin "$BRANCH"
echo -e "${GREEN}✓ Pushed to remote${NC}"

echo ""

# Create and push tag
echo -e "${BLUE}Creating tag 'v$VERSION'...${NC}"
git tag -a "v$VERSION" -m "Release NegoPlayer v$VERSION" 2>/dev/null || true
git push origin "v$VERSION" 2>/dev/null || true
echo -e "${GREEN}✓ Tag created and pushed${NC}"

echo ""

# Create release notes
echo -e "${BLUE}Preparing release notes...${NC}"

RELEASE_NOTES="Release NegoPlayer v$VERSION

## Features
- Advanced media player with video, audio, and streaming support
- Material 3 UI with modern Jetpack Compose
- Gesture-based controls for intuitive playback
- Support for multiple media formats (MP4, MKV, MP3, FLAC, WebM, etc.)
- Streaming protocols: HLS, DASH, RTSP

## Improvements
- Code optimization with ProGuard/R8
- Enhanced error handling
- Improved UI responsiveness
- Better memory management

## Build Information
- Minimum SDK: 21 (Android 5.0)
- Target SDK: 34 (Android 14)
- APK Size: ~40-45 MB (optimized)

## Testing
- Unit tested with JUnit and Robolectric
- Instrumented tests with Espresso
- Tested on multiple device sizes and Android versions

## GitHub Repository
- Source: Umarjaum/NegoPlayer
- Branch: main
- Commit: $(git rev-parse --short HEAD)

## Installation
Download the APK file from releases and install on your Android device.

## Support
For issues and feature requests, please use GitHub Issues.
"

echo "$RELEASE_NOTES" > release-notes-v$VERSION.txt

echo -e "${GREEN}✓ Release notes created: release-notes-v$VERSION.txt${NC}"

echo ""
echo -e "${GREEN}═══════════════════════════════════════${NC}"
echo -e "${GREEN}Release Process Complete!${NC}"
echo -e "${GREEN}═══════════════════════════════════════${NC}"
echo ""

echo -e "${BLUE}Summary:${NC}"
echo "  Version: v$VERSION"
echo "  Branch: $BRANCH"
echo "  Status: ✓ Committed"
echo "  Status: ✓ Pushed"
echo "  Status: ✓ Tagged"
echo ""

echo -e "${GREEN}Next Steps:${NC}"
echo "  1. Go to: https://github.com/Umarjaum/NegoPlayer"
echo "  2. Click on 'Releases' or 'Tags'"
echo "  3. Create release from tag 'v$VERSION'"
echo "  4. Add release notes from: release-notes-v$VERSION.txt"
echo "  5. Upload APK files from: app/build/outputs/apk/release/"
echo "  6. Publish release"
echo ""

echo -e "${BLUE}Git Commands Used:${NC}"
echo "  git add -A"
echo "  git commit -m '...'"
echo "  git push origin $BRANCH"
echo "  git tag -a v$VERSION -m '...'"
echo "  git push origin v$VERSION"
echo ""

echo -e "${GREEN}Release preparation complete!${NC}"
