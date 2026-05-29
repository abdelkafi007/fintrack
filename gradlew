#!/bin/bash

# Gradle wrapper script
# This downloads and runs the correct Gradle version

# Determine the project directory
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)

# Determine the Java command
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Read properties
GRADLE_VERSION=""
DISTRIBUTION_URL=""
while IFS='=' read -r key value; do
    case "$key" in
        distributionUrl) DISTRIBUTION_URL=$(echo "$value" | sed 's/\\:/:/g') ;;
    esac
done < "$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_DIST_DIR="$GRADLE_USER_HOME/wrapper/dists"

# Extract version from URL
GRADLE_VERSION=$(echo "$DISTRIBUTION_URL" | grep -oP 'gradle-\K[0-9.]+')
GRADLE_DIR="$GRADLE_DIST_DIR/gradle-$GRADLE_VERSION-bin"

# Download if not present
if [ ! -d "$GRADLE_DIR" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    mkdir -p "$GRADLE_DIR"
    curl -fsSL "$DISTRIBUTION_URL" -o "$GRADLE_DIR/gradle.zip"
    unzip -q "$GRADLE_DIR/gradle.zip" -d "$GRADLE_DIR"
    rm "$GRADLE_DIR/gradle.zip"
fi

# Find gradle binary
GRADLE_BIN=$(find "$GRADLE_DIR" -name "gradle" -path "*/bin/gradle" | head -1)

if [ -z "$GRADLE_BIN" ]; then
    echo "Error: Could not find Gradle binary"
    exit 1
fi

exec "$GRADLE_BIN" "$@"
