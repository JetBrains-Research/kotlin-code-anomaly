import sys
import csv
import pathlib
import re


DATA_DIR_PATH = pathlib.Path('data')


def concat_csvs(dataset_name):
    out_csv_path = DATA_DIR_PATH / f'{dataset_name}.csv'
    line_counter = 0
    with out_csv_path.open('wt') as out_file:
        writer = csv.writer(out_file, delimiter='\t')
        for file_path in DATA_DIR_PATH.iterdir():
            if re.match(f'{dataset_name}_part\d+\.csv', file_path.name):
                with file_path.open('r') as in_file:
                    reader = csv.reader(in_file, delimiter='\t')
                    for row in reader:
                        line_counter += 1
                        writer.writerow([line_counter] + row[1:])


if __name__ == '__main__':
    dataset_name = sys.argv[1]
    concat_csvs(dataset_name)
