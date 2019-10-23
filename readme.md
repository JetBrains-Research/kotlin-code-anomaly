## kotlin-code-anomaly

 This is a tool for detecting anomalies in kotlin code.

#### Requirements:

- java 8
  + java fx for visualizer.
- python 3 with packages listed in requiremets.txt

#### Usage:

- ./build.sh - to build the feture extraction and visualization kotlin modules
  + --no-visualizer to build only feature extractor
- ./run-all.sh \<path to the data set\> - run the pipline. The extracted features are placed in the data/ directory and the resulting anomalies data is put in the out-data/ directory.
- java -jar visualizer-cli.jar
  + --seek to extract code with anomalies
  + --binary-mark to manualy mark anomalies and useful or not
  + --copy to copy kotlin files
  + --categ-copy to sort anomalies by category (useful or useless)
