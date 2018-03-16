#!/usr/bin/env bash

./gradlew :feature-calc:jar || exit 1
mv feature-calc/build/libs/features.jar features.jar || exit 1
echo "Created or replaced features.jar"

./gradlew :visualizer:jar || exit 1
mv visualizer/build/libs/visualizer.jar visualizer.jar || exit 1
echo "Created or replaced visualizer.jar"

echo "Done."
echo "To launch metric extraction, run: ./calc-features"
echo "To launch interactive anomaly visualizer, run: java -jar visualizer.jar"
