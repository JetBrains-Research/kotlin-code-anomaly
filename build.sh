#!/usr/bin/env bash

./gradlew :metrics-calc:jar || exit 1
mv metrics-calc/build/libs/metrics-calc.jar metrics-calc.jar || exit 1

echo "Done."
echo "Created or replaced metrics-calc.jar"
echo "To launch metric extraction run: ./calc-metrics"
