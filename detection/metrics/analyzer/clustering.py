import csv
import os
import time

import matplotlib.pyplot as plt
import numpy as np
import pandas
# noinspection PyUnresolvedReferences
from mpl_toolkits.mplot3d import Axes3D
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
from sklearn.preprocessing import scale

is_drawing = False

out_dir = f"../post-analysis/"
csv_in_path = f"../post-analysis/feb18_true_all.csv"
# out_path = f"{out_dir}"

if not os.path.exists(out_dir):
    os.makedirs(out_dir)

start_time = time.time()

# Load input
methods = pandas.read_csv(csv_in_path, header=None, delimiter=',', quoting=csv.QUOTE_MINIMAL, error_bad_lines=True,
                          engine='python')

# Fix potential problems in input
X = np.array(methods.values[:, 1:], dtype="float64")
ok_lines = np.array([~np.isnan(row).any() for row in X])
methods = methods[ok_lines]
X = X[ok_lines]
n_methods = methods.shape[0]

# Preprocessing
X = scale(X)
X_pca = PCA(n_components=2).fit_transform(X)


def run_kmeans(methods):
    # K-Means
    n_clusters_kmeans = 4
    kmeans = KMeans(init='k-means++', n_clusters=n_clusters_kmeans, n_init=10)
    kmeans_labels = kmeans.fit_predict(X_pca)

    # Step size of the mesh. Decrease to increase the quality of the VQ.
    h = .02  # point in the mesh [x_min, x_max]x[y_min, y_max].
    # Plot the decision boundary. For that, we will assign a color to each
    x_min, x_max = X_pca[:, 0].min() - 1, X_pca[:, 0].max() + 1
    y_min, y_max = X_pca[:, 1].min() - 1, X_pca[:, 1].max() + 1
    xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
    # Obtain labels for each point in mesh. Use last trained model.
    Z = kmeans.predict(np.c_[xx.ravel(), yy.ravel()])
    # Put the result into a color plot
    Z = Z.reshape(xx.shape)
    plt.figure(1)
    plt.clf()
    plt.imshow(Z, interpolation='nearest',
               extent=(xx.min(), xx.max(), yy.min(), yy.max()),
               cmap=plt.cm.Paired,
               aspect='auto', origin='lower')
    # Plot the centroids as a white X
    centroids = kmeans.cluster_centers_
    plt.scatter(centroids[:, 0], centroids[:, 1], marker='x', s=100, linewidths=2, color='w')
    # Plot the data
    plt.plot(X_pca[:, 0], X_pca[:, 1], 'k.', markersize=2)
    plt.title('K-means (PCA-reduced data)')
    plt.xlim(x_min, x_max)
    plt.ylim(y_min, y_max)
    plt.xticks(())
    plt.yticks(())
    plt.savefig(f"{out_dir}k_means.png")

    # Save each cluster
    for i in range(n_clusters_kmeans):
        cur_indices = np.asarray([label == i for label in kmeans_labels])
        cur_methods = methods[cur_indices]
        cur_methods.to_csv(f"{out_dir}cluster_{i}.csv", header=False, index=False)


run_kmeans(methods)

# Finish
end_time = time.time()
print(f"Total elapsed time: {end_time - start_time}")
