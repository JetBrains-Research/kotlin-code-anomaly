import os
import json
import operator

from .lib.helpers.TimeLogger import TimeLogger


def collect_statistic(output_folder, all_features_file, ns):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    time_logger = TimeLogger(task_name='Statistic collection')
    f = open(all_features_file, 'r')
    features = json.loads(f.read())
    f.close()

    grams = {}
    for n in ns:
        grams[int(n)] = {}

    for feature in features:
        n = len(feature.split(':'))
        if n in grams:
            grams[n][feature] = features[feature]

    all_grams = {}
    for n in grams:
        all_grams = {**all_grams, **grams[n]}
        grams_sorted = sorted(grams[n].items(), key=operator.itemgetter(1))
        with open(output_folder + '/all_features_sorted' + str(n) + '.json', 'w') as grams_file_descriptor:
            grams_file_descriptor.write(json.dumps(grams_sorted))

    all_grams_sorted = sorted(all_grams.items(), key=operator.itemgetter(1))
    with open(output_folder + '/all_features_sorted.json', 'w') as grams_file_descriptor:
        grams_file_descriptor.write(json.dumps(all_grams_sorted))

    time_logger.finish(full_finish=True)
