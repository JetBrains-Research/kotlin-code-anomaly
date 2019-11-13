import sys
import pathlib
import json
import collections

from code2tree.code_to_psi import folder_to_psi
from ast_set2matrix.trees2vectors import trees2vectors
from ast_set2matrix.sparse_transform import sparse_transform
from ast_set2matrix.vectors2matrix import vectors2matrix
from ast_set2matrix.matrix2csv import matrix2csv


FEATURES_FOLDER = pathlib.Path('data')
PATH_ASTS = FEATURES_FOLDER / 'ast'
PATH_VECTORS = FEATURES_FOLDER / 'ast_vectors'
PATH_BC_VECTORS = FEATURES_FOLDER / 'bytecode_vectors'
PATH_ALL_FEATURES = PATH_VECTORS / 'all_features.json'
PATH_BC_ALL_FEATURES = PATH_BC_VECTORS / 'all_features.json'
PATH_SPARSE_VECTORS = FEATURES_FOLDER / 'ast_sparsed_vectors'
PATH_BC_SPARSE_VECTORS = FEATURES_FOLDER / 'bytecode_sparsed_vectors'
PATH_DATASET_JSON = FEATURES_FOLDER / 'dataset.json'
PATH_BC_DATASET_JSON = FEATURES_FOLDER / 'dataset_bytesode.json'
PATH_DATASET_CSV = FEATURES_FOLDER / 'dataset.csv'
PATH_BC_DATASET_CSV = FEATURES_FOLDER / 'dataset_bytesode.csv'
PATH_FEATURES_CONFIG = pathlib.Path('feature_extraction/ngram/features_config.json')


def calc_ngram_occurences(dir):
    ngram_counter = collections.Counter()
    for ngrams_path in dir.glob('**/*.class.json'):
        with ngrams_path.open('rt') as ngrams_file:
            cur_ngrams = json.load(ngrams_file)
        ngram_counter.update(cur_ngrams)
    all_features_path = dir / 'all_features.json'
    with all_features_path.open('wt') as all_features_file:
        json.dump(ngram_counter, all_features_file)


def extract_features(input_folder):
    FEATURES_FOLDER.mkdir(exist_ok=True)

    folder_to_psi(input_folder, PATH_ASTS)
    trees2vectors(str(PATH_ASTS), str(PATH_VECTORS), str(PATH_FEATURES_CONFIG))
    calc_ngram_occurences(PATH_BC_VECTORS)
    sparse_transform(str(PATH_VECTORS), str(PATH_SPARSE_VECTORS), str(PATH_ALL_FEATURES), 'list')
    sparse_transform(str(PATH_BC_VECTORS), str(PATH_BC_SPARSE_VECTORS), str(PATH_BC_ALL_FEATURES), 'list')
    vectors2matrix(str(PATH_SPARSE_VECTORS), str(PATH_DATASET_JSON))
    vectors2matrix(str(PATH_BC_SPARSE_VECTORS), str(PATH_BC_DATASET_JSON))
    matrix2csv(str(PATH_DATASET_JSON), str(PATH_DATASET_CSV))
    matrix2csv_streaming(str(PATH_BC_DATASET_JSON), str(PATH_BC_DATASET_CSV))


if __name__ == '__main__':
    extract_features(pathlib.Path(sys.argv[1]))
