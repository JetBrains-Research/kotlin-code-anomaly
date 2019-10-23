#!/usr/bin/env bash

if [ "$#" -ne 1 ]; then
    echo "Usage: ./run-all.sh dataset-path"
fi

data_path=$1
part_size=100000

echo "========== BUILDING ==========="
./build.sh --no-visualizer || exit 1

echo "========== EXTRACTING FACTORS ==========="
for i in `seq 1 10`; do
    let skip=$(( (${i} - 1) * ${part_size} ))
    echo "Part $i"
    echo "Skipping $skip..."
    ./calc-features \
        -i ${data_path}/ \
        -m data/metrics_part${i}.csv \
        --file-limit ${part_size} \
        --skip-files ${skip} || exit 1 \
        --error-log data/part${i}_errors.log
    echo "Done with part ${i}."
done

echo "========== CONCATENATING CSV ==========="
python3 concat_csv.py metrics || exit 1

echo "========== RUNNING ANALYZER ==========="
cd analyzer/
python3 methods.py metrics || exit 1
cd ..

echo "Done all!"
