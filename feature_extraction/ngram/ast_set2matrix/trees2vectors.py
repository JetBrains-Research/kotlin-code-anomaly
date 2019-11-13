import os
import json

from .lib.helpers.FilesWalker import FilesWalker
from .lib.helpers.TimeLogger import TimeLogger
from .lib.tree2vec.feature_extractor import feature_extractor


def collect_features_statistic(features_file, all_features_file):
    with open(features_file, 'r') as features_file_descriptor:
        features_json = features_file_descriptor.read()
        features = json.loads(features_json)

        with open(all_features_file, 'r+') as all_features_file_descriptor:
            all_features_json = all_features_file_descriptor.read()
            all_features = json.loads(all_features_json) if all_features_json else {}
            for feature in features:
                if feature in all_features:
                    all_features[feature] += features[feature]
                else:
                    all_features[feature] = features[feature]

            all_features_file_descriptor.seek(0)
            all_features_file_descriptor.write(json.dumps(all_features))
            all_features_file_descriptor.truncate()


def trees2vectors(input_folder, output_folder, features_file):
    time_logger = TimeLogger(task_name='Feature extraction')

    all_features_file = output_folder + '/all_features.json'
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    if not os.path.isfile(all_features_file):
        with open(all_features_file, 'w') as all_features_file_descriptor:
            all_features_file_descriptor.write(json.dumps({}))

    with open(features_file, 'r') as features_file_descriptor:
        features = json.loads(features_file_descriptor.read())

    def tree_file_process(filename):
        time_logger_file = TimeLogger(task_name='Feature extraction in %s' % filename)
        relative_filename = os.path.relpath(filename, input_folder)
        output_file = output_folder + '/' + relative_filename
        output_folders = os.path.dirname(output_file)
        if not os.path.exists(output_folders):
            os.makedirs(output_folders)

        feature_extractor(filename, features, output_file)

        collect_features_statistic(output_file, all_features_file)

        time_logger_file.finish()

    FilesWalker.walk(input_folder, tree_file_process)

    time_logger.finish(full_finish=True)
