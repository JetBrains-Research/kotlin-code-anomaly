#!/usr/bin/env bash

./gradlew :feature-calc:jar || exit 1
mv feature_extraction/metrics/feature-calc/build/libs/features.jar feature_extraction/metrics/features.jar || exit 1
echo "Created or replaced features.jar"

./gradlew :bytecode-parser:build
mv feature_extraction/ngram/bytecode-parser/build/libs/bytecode-parser-0.1.jar feature_extraction/ngram/bytecode-parser-0.1.jar
echo "Created or replaced bytecode-parser-0.1.jar"

./gradlew :ngram-generator:build
mv feature_extraction/ngram/ngram-generator/build/libs/ngram-generator-0.1.2.jar feature_extraction/ngram/ngram-generator-0.1.2.jar
echo "Created or replaced ngram-generator-0.1.2.jar"

./gradlew :ngram-selector:build
mv feature_extraction/ngram/ngram-selector/build/libs/ngram-selector-0.1.jar feature_extraction/ngram/ngram-selector-0.1.jar
echo "Created or replaced ngram-selector-0.1.jar"

./gradlew :bytecode-to-source-mapper:build
mv code_mapping/bytecode-to-source-mapper/build/libs/bytecode-to-source-mapper-0.1.jar code_mapping
echo "Created or replaced bytecode-to-source-mapper-0.1.jar"

./gradlew :bytecode-anomalies-source-finder:build
mv code_mapping/bytecode-anomalies-source-finder/build/libs/bytecode-anomalies-source-finder-0.1.jar code_mapping
echo "Created or replaced bytecode-anomalies-source-finder-0.1.jar"

if [[ $# != 1 ]] || [[ $1 != '--no-visualizer' ]] ; then
    ./gradlew :visualizer:jar || exit 1
    mv visualizer/build/libs/visualizer-cli.jar visualizer-cli.jar || exit 1
    echo "Created or replaced visualizer-cli.jar"

    ./gradlew :visualizer-gui:jar || exit 1
    mv visualizer-gui/build/libs/visualizer-gui.jar visualizer-gui.jar || exit 1
    echo "Created or replaced visualizer-gui.jar"
fi

echo "Done."
