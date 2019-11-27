## kotlin-code-anomaly

 This is a tool for detecting anomalies in kotlin code. Anomalies are found in two ways: by classical machine learning algorithms on code metrics and by variational autoencoder on n-grams.

#### Requirements:

- java 8
  + java fx for visualizer.
- python 3 with packages listed in requiremets.txt

#### Usage:

- ./build.sh - to build the feture extraction and visualization kotlin modules
  + --no-visualizer to build only feature extractor
- ./run-all.sh \<path to the data set\> - run the pipline. The extracted features are placed in the data/ directory and the resulting anomalies data is put in the out-data/ directory.
- java -jar visualizer-cli.jar helps analyze the anomalies found by classical algorithms with metrics
  + --seek to extract code with anomalies
  + --binary-mark to manualy mark anomalies and useful or not
  + --copy to copy kotlin files
  + --categ-copy to sort anomalies by category (useful or useless)

The following parts contain external code:
-  detection/ngram/anomaly_detection
    - License: Apache 2
    - origin: https://github.com/PetukhovVictor/code-anomaly-detection
- feature_extraction/ngram/ast_set2matrix
    - License: Apache 2
    - origin: https://github.com/PetukhovVictor/tree-set2matrix/
- feature_extraction/ngram/bytecode-parser
    - License: Apache 2
    - origin: https://github.com/PetukhovVictor/bytecode-parser/
- feature_extraction/ngram/ngram-generator
    - License: Apache 2
    - origin: https://github.com/PetukhovVictor/ngram-generator/
- code_mapping/bytecode-to-source-mapper
    - License: Apache 2
    - origin: https://github.com/PetukhovVictor/bytecode-to-source-mapper
- code_mapping/bytecode-anomalies-source-finder
    - License: Apache 2
    - origin: https://github.com/PetukhovVictor/bytecode-anomalies-source-finder
