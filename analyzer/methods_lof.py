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

dataset_name = "26proj"
is_drawing = True

out_dir = f"../out-data/"
csv_in_path = f"../data/{dataset_name}_methods.csv"
csv_out_path = f"{out_dir}methods_lof"
img_out_path = f"{out_dir}methods_lof"
log_path = f"{out_dir}methods_lof.log"

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

# Clear input
X = np.array(methods.values[:, 1:], dtype="float64")
ok_lines = np.array([~np.isnan(row).any() for row in X])
methods = methods[ok_lines]
X = X[ok_lines]
n_methods = methods.shape[0]

# Preprocessing
X = PCA(n_components=3).fit_transform(X)
X = scale(X)

param_grid = {
    'n_neighbors': [10, 5, 2],
    'algorithm': ['ball_tree', 'kd_tree'],
    'contamination': [0.0005, 0.001]
}
param_sets = list(ParameterGrid(param_grid))

# For calculating 'intersection', i.e. methods marked as anomalous by all classifier configurations
all_indices = np.arange(0, n_methods)
intersect_outlier_indices = all_indices

clf = LocalOutlierFactor(n_jobs=-1)

for params in param_sets:
    config_desc = str(params)
    log(config_desc)

    # Fit the model and mark data
    clf.set_params(**params)
    marks = clf.fit_predict(X)

    inlier_indices = np.asarray([mark > 0 for mark in marks])
    outlier_indices = np.asarray([mark < 0 for mark in marks])
    intersect_outlier_indices = np.intersect1d(intersect_outlier_indices, all_indices[outlier_indices])

    X_inliers = X[inlier_indices]
    X_outliers = X[outlier_indices]
    n_inliers = X_inliers.shape[0]
    n_outliers = X_outliers.shape[0]
    log(f"\tInliers:\t{n_inliers:6}/{n_methods:6}\t{(n_inliers * 100 / n_methods):7.4}%")
    log(f"\tOutliers:\t{n_outliers:6}/{n_methods:6}\t{(n_outliers * 100 / n_methods):7.4}%")

    if is_drawing:
        # Show the principal components on 3D plot
        fig = plt.figure()
        ax = fig.add_subplot(111, projection='3d')
        ax.scatter(X_inliers[:, 1], X_inliers[:, 0], X_inliers[:, 2], c='None', edgecolor='blue', marker='o')
        ax.scatter(X_outliers[:, 1], X_outliers[:, 0], X_outliers[:, 2], c='red', marker='^')
        plt.savefig(f"{img_out_path} {config_desc}.png")

    # Save output of this configuration to file
    outlier_names = methods.values[:, 0][outlier_indices]
    dataframe = pandas.DataFrame(outlier_names)
    dataframe.to_csv(f"{csv_out_path} {config_desc}.csv")

# Save the 'intersection' to file
intersect_outlier_names = methods.values[:, 0][intersect_outlier_indices]
dataframe = pandas.DataFrame(intersect_outlier_names)
dataframe.to_csv(f"{csv_out_path}.csv")

end_time = time.time()
log(f"Total elapsed time: {end_time - start_time}")
log_file.close()
