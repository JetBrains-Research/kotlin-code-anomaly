import matplotlib.pyplot as plt
import numpy as np
import pandas

# noinspection PyUnresolvedReferences
from mpl_toolkits.mplot3d import Axes3D
from sklearn.decomposition import PCA
from sklearn.neighbors import LocalOutlierFactor
from sklearn.preprocessing import scale

csv_path = '../data/6proj_methodMetrics.csv'
out_path = '../out-data/6proj_methods_lof.csv'
img_out_path = '../out-data/6proj_methods_lof'

# Load input
methods = pandas.read_csv(csv_path, header=0)
n_methods = methods.shape[0]

# Preprocessing
X = np.array(methods.values[:, 1:], dtype="float64")
X = PCA(n_components=3).fit_transform(X)
X = scale(X)

configs = [
    (20, 'auto', 0.001),
    (10, 'auto', 0.001),
    (5, 'ball_tree', 0.001),
    (3, 'kd_tree', 0.001),
]

all_indices = np.arange(0, n_methods)
intersect_outlier_indices = all_indices

is_drawing = False
for n_neighbors, algorithm, contamination in configs:
    # Fit the model and mark data
    clf = LocalOutlierFactor(n_neighbors=n_neighbors, algorithm=algorithm, contamination=contamination, n_jobs=-1)
    marks = clf.fit_predict(X)

    inlier_indices = np.asarray([mark > 0 for mark in marks])
    outlier_indices = np.asarray([mark < 0 for mark in marks])
    intersect_outlier_indices = np.intersect1d(intersect_outlier_indices, all_indices[outlier_indices])

    X_inliers = X[inlier_indices]
    X_outliers = X[outlier_indices]
    n_inliers = X_inliers.shape[0]
    n_outliers = X_outliers.shape[0]

    config_desc = f"N={n_neighbors}, algo={clf.algorithm}, contam={contamination}"
    print(config_desc)
    print(f"\tInliers:\t{n_inliers}/{n_methods}\t({n_inliers * 100 / n_methods}%)")
    print(f"\tOutliers:\t{n_outliers}/{n_methods}\t({n_outliers * 100 / n_methods}%)")

    if is_drawing:
        # Show the principal components on 3D plot
        fig = plt.figure()
        ax = fig.add_subplot(111, projection='3d')
        ax.scatter(X_inliers[:, 1], X_inliers[:, 0], X_inliers[:, 2], c='None', edgecolor='blue', marker='o')
        ax.scatter(X_outliers[:, 1], X_outliers[:, 0], X_outliers[:, 2], c='red', marker='^')
        plt.savefig(f"{img_out_path} {config_desc}.png")

# Save the 'intersection' to file
intersect_outlier_names = methods.values[:, 0][intersect_outlier_indices]
np.savetxt(out_path, intersect_outlier_names.astype('U'), fmt='%s')
