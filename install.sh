#!/bin/bash
# OpenPizza - Build & Install script
# Usage: ./install.sh        (build & install debug)
#        ./install.sh -r     (build, uninstall, reinstall)
echo "=== OpenPizza Builder ==="
export ANDROID_HOME="$ANDROID_HOME"
cd "$(dirname "$0")"

if [ "$1" = "-r" ]; then
  echo "Uninstalling old version..."
  adb uninstall com.dominos.app 2>/dev/null
fi

echo "Building..."
./gradlew assembleDebug --no-daemon || { echo "BUILD FAILED"; exit 1; }

echo "Installing..."
adb install -r app/build/outputs/apk/debug/app-debug.apk 2>&1 || {
  echo "Install failed - trying fresh install..."
  adb uninstall com.dominos.app 2>/dev/null
  adb install app/build/outputs/apk/debug/app-debug.apk
}

echo "Launching..."
adb shell am start -n com.dominos.app/.MainActivity
echo "=== Done! ==="
