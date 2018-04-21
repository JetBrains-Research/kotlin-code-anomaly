#!/usr/bin/env bash

./gradlew :feature-calc:jar || exit 1
mv feature-calc/build/libs/features.jar features.jar || exit 1
echo "Created or replaced features.jar"

./gradlew :visualizer:jar || exit 1
mv visualizer/build/libs/visualizer-cli.jar visualizer-cli.jar || exit 1
echo "Created or replaced visualizer-cli.jar"

./gradlew :visualizer-gui:jar || exit 1
mv visualizer-gui/build/libs/visualizer-gui.jar visualizer-gui.jar || exit 1
echo "Created or replaced visualizer-gui.jar"

echo "Done."
echo "To launch metric extraction, run: ./calc-features"
