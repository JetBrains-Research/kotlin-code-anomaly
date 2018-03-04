import csv
import matplotlib.pyplot as plt
import numpy as np
import os
import pandas
import time

# noinspection PyUnresolvedReferences
from mpl_toolkits.mplot3d import Axes3D
from sklearn.decomposition import PCA
from sklearn.model_selection import ParameterGrid
from sklearn.neighbors import LocalOutlierFactor
from sklearn.preprocessing import scale
from sklearn.svm import OneClassSVM

dataset_name = "feb18-part2"
is_drawing = False

out_dir = f"../out-data/"
csv_in_path = f"../data/{dataset_name}_methods.csv"
out_path = f"{out_dir}{dataset_name}"
log_path = f"{out_dir}methods.log"

if not os.path.exists(out_dir):
    os.makedirs(out_dir)
log_file = open(log_path, mode='w+')


def log(s):
    print(s)
    log_file.write(s)
    log_file.write('\n')


start_time = time.time()

# Load input
methods = pandas.read_csv(csv_in_path, header=0, delimiter='\t', quoting=csv.QUOTE_NONE, error_bad_lines=True,
                          engine='python')

# Fix potential problems in input
X = np.array(methods.values[:, 1:], dtype="float64")
ok_lines = np.array([~np.isnan(row).any() for row in X])
methods = methods[ok_lines]
X = X[ok_lines]
n_methods = methods.shape[0]

# Preprocessing
X = scale(X)
# X = PCA(n_components=3).fit_transform(X)

# All configs
all_clf_configs = [
    {
        'clf_name': 'lof',
        'clf': LocalOutlierFactor(n_jobs=-1),
        'param_grid': {
            'n_neighbors': [10, 5, 2],
            'algorithm': ['kd_tree'],
            'contamination': [0.0001]
        }
    },
    {
        'clf_name': 'svm',
        'clf': OneClassSVM(shrinking=True),
        'param_grid': [
            {
                'kernel': ['linear'],
                'nu': [0.00005]
            },
            {
                'kernel': ['rbf', 'poly'],
                'nu': [0.00005, 0.0001],
                'gamma': [0.1]
            }
        ]
    }
]
# Configs for the current run
clf_configs = [clf_config for clf_config in all_clf_configs if clf_config['clf_name'] in ('lof', 'svm')]

for clf_config in clf_configs:
    clf_name = clf_config['clf_name']
    clf = clf_config['clf']
    param_sets = list(ParameterGrid(clf_config['param_grid']))
    log(clf_name)

    # For calculating 'intersection', i.e. methods marked as anomalous
    # by the current classifier with all param sets
    all_indices = np.arange(0, n_methods)
    intersect_outlier_indices = all_indices

    for params in param_sets:
        param_set_desc = str(params)
        log(f"\t{param_set_desc}")

        # Fit the model and mark data
        clf.set_params(**params)
        if clf_name == 'lof':
            marks = clf.fit_predict(X)
        elif clf_name == 'svm':
            clf.fit(X)
            # Suppressed warning below: clf is in dictionary
            # noinspection PyUnresolvedReferences
            marks = clf.predict(X)
        else:
            log(f"Error: unknown classifier name {clf_name}!")
            exit(1)

        # Suppressed warning below: either `marks` is assigned, or the whole program exits with an error
        # noinspection PyUnboundLocalVariable
        inlier_indices = np.asarray([mark > 0 for mark in marks])
        outlier_indices = np.asarray([mark < 0 for mark in marks])
        intersect_outlier_indices = np.intersect1d(intersect_outlier_indices, all_indices[outlier_indices])

        X_inliers = X[inlier_indices]
        X_outliers = X[outlier_indices]
        n_inliers = X_inliers.shape[0]
        n_outliers = X_outliers.shape[0]
        log(f"\t\tInliers:\t{n_inliers:6}/{n_methods:6}\t{(n_inliers * 100 / n_methods):10.7}%")
        log(f"\t\tOutliers:\t{n_outliers:6}/{n_methods:6}\t{(n_outliers * 100 / n_methods):10.7}%")

        if n_outliers > n_inliers:
            X_temp = X_inliers
            X_inliers = X_outliers
            X_outliers = X_temp
            log("\t\tSwapped 'inliers' and 'outliers', because there were more outliers than inliers!")

        if is_drawing:
            # Show the principal components on 3D plot
            fig = plt.figure()
            ax = fig.add_subplot(111, projection='3d')
            ax.scatter(X_inliers[:, 1], X_inliers[:, 0], X_inliers[:, 2], c='None', edgecolor='blue', marker='o')
            ax.scatter(X_outliers[:, 1], X_outliers[:, 0], X_outliers[:, 2], c='red', marker='^')
            plt.savefig(f"{out_path} {clf_name} {param_set_desc}.png")

        # Save output of this configuration to file
        outlier_names = methods.values[:][outlier_indices]
        dataframe = pandas.DataFrame(outlier_names)
        dataframe.to_csv(f"{out_path} {clf_name} {param_set_desc}.csv", header=False, index=False)

    # Save the 'intersection' to file
    intersect_outlier_names = methods.values[:, 0][intersect_outlier_indices]
    dataframe = pandas.DataFrame(intersect_outlier_names)
    dataframe.to_csv(f"{out_path} {clf_name} intersection.csv", header=False, index=False)

end_time = time.time()
log(f"Total elapsed time: {end_time - start_time}")
log_file.close()
