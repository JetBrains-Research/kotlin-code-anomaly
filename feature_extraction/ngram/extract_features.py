import sys
import pathlib
import json
import collections
import shutil
import os

from code2tree.code_to_psi import folder_to_psi
from ast_set2matrix.trees2vectors import trees2vectors
from ast_set2matrix.sparse_transform import sparse_transform
from ast_set2matrix.vectors2matrix import vectors2matrix
from ast_set2matrix.matrix2csv import matrix2csv, matrix2csv_streaming


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
BC_DIM = 1000


def select_bc_features():
    with PATH_BC_ALL_FEATURES.open('rt') as all_features_file:
        all_features = json.load(all_features_file)
    common_features = dict(collections.Counter(all_features).most_common()[:BC_DIM])
    common_feature_set = common_features.keys()
    for step, path in enumerate(PATH_BC_VECTORS.glob('**/*.class.json'), 1):
        print(f'Reducing dimensionality for {step}-th file: {path.relative_to(PATH_BC_VECTORS)}')
        with path.open('rt') as file:
            class_vec = json.load(file)
        class_rd_vec = {
            ngram: freq for ngram, freq in class_vec.items() if ngram in common_feature_set
        }
        with path.open('wt') as file:
            json.dump(class_rd_vec, file)
    with PATH_BC_ALL_FEATURES.open('wt') as all_features_file:
        json.dump(common_features, all_features_file)


def extract_features(input_folder):
    FEATURES_FOLDER.mkdir(exist_ok=True)

    folder_to_psi(input_folder, PATH_ASTS)
    trees2vectors(str(PATH_ASTS), str(PATH_VECTORS), str(PATH_FEATURES_CONFIG))
    shutil.rmtree(str(PATH_ASTS))
    select_bc_features()
    sparse_transform(str(PATH_VECTORS), str(PATH_SPARSE_VECTORS), str(PATH_ALL_FEATURES), 'list')
    shutil.rmtree(str(PATH_VECTORS))
    sparse_transform(str(PATH_BC_VECTORS), str(PATH_BC_SPARSE_VECTORS), str(PATH_BC_ALL_FEATURES), 'list')
    shutil.rmtree(str(PATH_BC_VECTORS))
    vectors2matrix(str(PATH_SPARSE_VECTORS), str(PATH_DATASET_JSON))
    shutil.rmtree(str(PATH_SPARSE_VECTORS))
    vectors2matrix(str(PATH_BC_SPARSE_VECTORS), str(PATH_BC_DATASET_JSON))
    shutil.rmtree(str(PATH_BC_SPARSE_VECTORS))
    matrix2csv(str(PATH_DATASET_JSON), str(PATH_DATASET_CSV))
    os.remove(str(PATH_DATASET_JSON))
    matrix2csv_streaming(str(PATH_BC_DATASET_JSON), str(PATH_BC_DATASET_CSV))
    os.remove(str(PATH_BC_DATASET_JSON))


if __name__ == '__main__':
    extract_features(pathlib.Path(sys.argv[1]))
