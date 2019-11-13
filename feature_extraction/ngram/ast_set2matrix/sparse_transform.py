import os
import json

from .lib.helpers.FilesWalker import FilesWalker
from .lib.helpers.TimeLogger import TimeLogger


def sparse_transform(input_folder, output_folder, all_features_file, sparse_format):
    time_logger = TimeLogger(task_name='Sparse transformation')

    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    with open(all_features_file, 'r') as all_features_file_descriptor:
        all_features_json = all_features_file_descriptor.read()
        all_features = json.loads(all_features_json)

    def ast_file_process(filename, all_features):
        if filename == all_features_file:
            return

        time_logger_file = TimeLogger(task_name='Sparse transformation in %s' % filename)
        with open(filename, 'r+') as features_file_descriptor:
            features_json = features_file_descriptor.read()
            features = json.loads(features_json)

            features_sparsed = {}
            for feature in all_features:
                features_sparsed[feature] = features[feature] if feature in features else 0

            if sparse_format == 'list':
                feature_values = [value for (key, value) in sorted(features_sparsed.items())]
            else:
                feature_values = features_sparsed

            relative_filename = os.path.relpath(filename, input_folder)
            output_file = output_folder + '/' + relative_filename
            file_folder = os.path.dirname(output_folder + '/' + relative_filename)
            if not os.path.exists(file_folder):
                os.makedirs(file_folder)
            with open(output_file, 'w') as features_sparsed_file_descriptor:
                features_sparsed_file_descriptor.write(json.dumps(feature_values))

        time_logger_file.finish()

    FilesWalker.walk(input_folder, lambda filename: ast_file_process(filename, all_features))

    time_logger.finish(full_finish=True)
