#!/usr/bin/env bash

if [ "$#" -ne 1 ]; then
    echo "Usage: ./run-all.sh dataset-path"
fi

data_path=$1
part_size=100000

echo "========== BUILDING ==========="
./build.sh --no-visualizer || exit 1

echo "========== EXTRACTING METRIC FACTORS ==========="
for i in `seq 1 10`; do
    let skip=$(( (${i} - 1) * ${part_size} ))
    echo "Part $i"
    echo "Skipping $skip..."
    feature_extraction/metrics/calc-features \
        -i ${data_path}/ \
        -m data/metrics_part${i}.csv \
        --file-limit ${part_size} \
        --skip-files ${skip} \
        --error-log data/part${i}_errors.log || exit 1
    echo "Done with part ${i}."
done

echo "========== EXTRACTING N-GRAM FACTORS ==========="
java -jar feature_extraction/ngram/bytecode-parser-0.1.jar -i ${data_path} --parsing
java -jar feature_extraction/ngram/bytecode-parser-0.1.jar -i ${data_path} --grouping
java -jar feature_extraction/ngram/ngram-generator-0.1.2.jar -i `realpath ${data_path}/../classes_grouped/packages` -o data/bytecode_vectors --list
# rm -rf ../classes_grouped
mv all_ngrams.json data/bytecode_vectors/all_features.json
# java -jar feature_extraction/ngram/ngram-generator-0.1.2.jar -i data/ast -o data/ast_vectors --tree
# mv all_ngrams.json data/ast_vectors/all_features.json
java -jar feature_extraction/ngram/ngram-selector-0.1.jar -i data/bytecode_vectors -o data/bytecode_vectors_reduced \
    --all_ngrams_file data/bytecode_vectors/all_features.json
# java -jar feature_extraction/ngram/ngram-selector-0.1.jar -i data/ast_vectors -o data/ast_vectors_reduced \
#     --all_ngrams_file data/ast_vectors/all_features.json
python3 feature_extraction/ngram/extract_features.py $data_path || exit 1

echo "========== CONCATENATING CSV ==========="
python3 feature_extraction/metrics/concat_csv.py metrics || exit 1

echo "========== RUNNING ANALYZERS WITH METRICS ==========="
python3 detection/metrics/analyzer/methods.py metrics || exit 1

echo "========== RUNNING AUTOENCODER ANALYZER WITH N-GRAMS ==========="
python3 detection/ngram/anomaly_detection/main.py -f data/dataset.csv --split_percent 0.1 \
    --encoding_dim_percent 0.25 --files_map_file data/files_map.json \
    -o n-gram_anomalies.txt
python3 detection/ngram/anomaly_detection/main.py -f data/dataset_bytesode.csv --split_percent 0.1 \
   --encoding_dim_percent 0.25 --files_map_file data/files_map.json \
   -o n-gram_bytecode_anomalies.txt --binary

echo "Done all!"
