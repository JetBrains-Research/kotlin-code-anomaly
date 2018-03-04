#!/usr/bin/env bash

./gradlew :metrics-calc:jar || exit 1
mv metrics-calc/build/libs/metrics-calc.jar metrics-calc.jar || exit 1
echo "Created or replaced metrics-calc.jar"

./gradlew :visualizer:jar || exit 1
mv visualizer/build/libs/visualizer.jar visualizer.jar || exit 1
echo "Created or replaced visualizer.jar"

echo "Done."
echo "To launch metric extraction, run: ./calc-metrics"
echo "To launch interactive anomaly visualizer, run: java -jar visualizer.jar"
