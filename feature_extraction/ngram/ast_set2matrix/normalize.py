import os
import json

from .lib.helpers.FilesWalker import FilesWalker
from .lib.helpers.TimeLogger import TimeLogger


def normalize(input_folder, output_folder, all_features_file):
    time_logger = TimeLogger(task_name='Normalize')

    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    with open(all_features_file, 'r') as all_features_file_descriptor:
        all_features_json = all_features_file_descriptor.read()
        all_features = json.loads(all_features_json)

    def ast_file_process(filename, all_features):
        time_logger_file = TimeLogger(task_name='Normalize in %s' % filename)
        with open(filename, 'r+') as features_file_descriptor:
            features_json = features_file_descriptor.read()
            features = json.loads(features_json)

            for feature in features:
                features[feature] /= len(all_features)

            relative_filename = os.path.relpath(filename, input_folder)
            output_file = output_folder + '/' + relative_filename
            with open(output_file, 'w') as features_normalized_file_descriptor:
                features_normalized_file_descriptor.write(json.dumps(features))

        time_logger_file.finish()

    FilesWalker.walk(input_folder, lambda filename: ast_file_process(filename, all_features))

    time_logger.finish(full_finish=True)
