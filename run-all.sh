#!/usr/bin/env bash

# dataset="testSrcMethods"
# part_size=1
dataset="feb18_20metrics"
part_size=100000

echo "========== BUILDING ==========="
./build.sh || exit 1

echo "========== EXTRACTING FACTORS ==========="
for i in `seq 1 10`; do
	let skip=$(( (${i} - 1) * ${part_size} ))
	echo "Part $i"
	echo "Skipping $skip..."
	calc-features \
	    -i repos/ \
	    -m data/${dataset}_part${i}.csv \
	    --file-limit ${part_size} \
	    --skip-files ${skip} || exit 1
	echo "Done with part ${i}."
	# features-calc/src/main/kotlin/testSrc/ \
done

echo "========== CONCATENATING CSV ==========="
cd data/
python3 concat.py ${dataset} || exit 1
cd ..

echo "========== RUNNING ANALYZER ==========="
cd analyzer/
python3 methods.py ${dataset}

echo "Done all!"
