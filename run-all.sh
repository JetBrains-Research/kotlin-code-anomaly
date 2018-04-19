#!/usr/bin/env bash

if [ "$#" -ne 1 ]; then
    echo "Usage: ./run-all.sh dataset-name"
fi

dataset=$1
part_size=100000

echo "========== BUILDING ==========="
./build.sh || exit 1

echo "========== EXTRACTING FACTORS ==========="
for i in `seq 1 10`; do
    let skip=$(( (${i} - 1) * ${part_size} ))
    echo "Part $i"
    echo "Skipping $skip..."
    ./calc-features \
        -i repos/ \
        -m data/${dataset}_part${i}.csv \
        --file-limit ${part_size} \
        --skip-files ${skip} || exit 1 \
        --error-log data/part${i}_errors.log
    echo "Done with part ${i}."
done

echo "========== CONCATENATING CSV ==========="
cd data/
python3 concat_csv.py ${dataset} || exit 1
cd ..

echo "========== RUNNING ANALYZER ==========="
cd analyzer/
#python3 methods.py ${dataset} || exit 1
cd ..

echo "Done all!"
