#!/bin/bash

echo "Building LimeChat Android SDK..."

# Clean previous builds
./gradlew clean

# Build the library AAR
./gradlew :library:assembleRelease

# Copy AAR to example app's libs folder
mkdir -p example-app/libs
cp library/build/outputs/aar/*.aar example-app/libs/

echo "AAR built and copied to example-app/libs/"
echo "Now you can test the AAR locally in the example app"

# Optional: Build and run the example app
read -p "Do you want to build and run the example app? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    ./gradlew :example-app:assembleDebug
    echo "Example app built successfully!"
    echo "Install it using: ./gradlew :example-app:installDebug"
fi