#!/bin/bash

# NegoPlayer Keystore Generation Script
# This script generates the release keystore for signing APKs

set -e

KEYSTORE_FILE="release.keystore"
KEYSTORE_ALIAS="negoplayer"
VALIDITY_DAYS=10000

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== NegoPlayer Keystore Setup ===${NC}"
echo ""

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo -e "${YELLOW}Warning: Keystore file already exists at $KEYSTORE_FILE${NC}"
    read -p "Do you want to overwrite it? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}Aborting keystore creation.${NC}"
        exit 1
    fi
    rm "$KEYSTORE_FILE"
fi

echo "Generating keystore with the following details:"
echo "  Keystore File: $KEYSTORE_FILE"
echo "  Key Alias: $KEYSTORE_ALIAS"
echo "  Validity: $VALIDITY_DAYS days"
echo "  Algorithm: RSA 2048-bit"
echo ""

# Generate keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY_DAYS" \
    -alias "$KEYSTORE_ALIAS" \
    -storepass "jaumumar@8627" \
    -keypass "jaumumar@8627" \
    -dname "CN=Muhammad Umar Jabbar, OU=Development, O=NegoPlayer, L=Islamabad, ST=Federal, C=PK"

echo ""
echo -e "${GREEN}Keystore created successfully!${NC}"
echo ""

# Display keystore information
echo "Keystore Details:"
keytool -list -v -keystore "$KEYSTORE_FILE" -storepass "jaumumar@8627"

echo ""
echo -e "${GREEN}Setup Complete!${NC}"
echo ""
echo "Environment Variables for Gradle:"
echo "  KEYSTORE_PATH=$(pwd)/$KEYSTORE_FILE"
echo "  KEYSTORE_PASSWORD=jaumumar@8627"
echo "  KEY_ALIAS=$KEYSTORE_ALIAS"
echo "  KEY_PASSWORD=jaumumar@8627"
echo ""
echo "Add these to your GitHub Secrets or set them before building release APK."
echo ""

# Create GitHub Secrets template
cat > github-secrets-template.txt << 'EOF'
KEYSTORE_PATH=./release.keystore
KEYSTORE_PASSWORD=jaumumar@8627
KEY_ALIAS=negoplayer
KEY_PASSWORD=jaumumar@8627
EOF

echo -e "${GREEN}GitHub Secrets template created: github-secrets-template.txt${NC}"
