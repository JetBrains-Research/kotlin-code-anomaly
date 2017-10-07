import numpy as np
import matplotlib.pyplot as plt
from sklearn.neighbors import LocalOutlierFactor

is_analyzing_methods = False

if is_analyzing_methods:
    csv_path = '../data/6proj_methodMetrics.csv'
    out_path = '../data/6proj_methods_lof.csv'
    img_out_path = '../data/6proj_methods_lof.png'
else:
    csv_path = '../data/6proj_fileMetrics.csv'
    out_path = '../data/6proj_files_lof.csv'
    img_out_path = '../data/6proj_files_lof.png'

n_neighbors = 5

# Process input

labels = np.genfromtxt(csv_path, delimiter=',', usecols=[0], dtype=None)
raw_data = np.genfromtxt(csv_path, delimiter=',', comments=None)[:, 1:]

# Fit the model and mark data

lof = LocalOutlierFactor(n_neighbors=n_neighbors)
marks = lof.fit_predict(raw_data)
marked = np.column_stack((labels, raw_data, marks))

inlier_indices = np.asarray([int(row[-1]) > 0 for row in marked])
outlier_indices = np.asarray([int(row[-1]) < 0 for row in marked])
inliers = marked[inlier_indices]
outliers = marked[outlier_indices]
outlier_names, _ = np.hsplit(outliers, [1])

# Save results

print(f"Outliers: {len(outliers)}/{len(marked)}, {len(outliers) / len(marked) * 100}%")
np.savetxt(out_path, outlier_names.astype('U'), fmt='%s')


def draw_subplot(x_label, y_label):
    plt.subplot(2, 2, draw_subplot.counter)
    x_index = columns[x_label]
    y_index = columns[y_label]
    plt.scatter(inliers[:, x_index], inliers[:, y_index], c='blue', edgecolor='k')
    plt.scatter(outliers[:, x_index], outliers[:, y_index], c='red', edgecolor='k')
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    draw_subplot.counter += 1


draw_subplot.counter = 1
if is_analyzing_methods:
    columns = {'SLoC': 1, 'AST nodes': 2, 'AST height': 3, 'Loop nesting depth': 4, 'Cyclomatic complexity': 5}
    plt.figure(num='Methods', figsize=(12, 8))
    draw_subplot('SLoC', 'AST nodes')
    draw_subplot('AST nodes', 'AST height')
    draw_subplot('AST height', 'Cyclomatic complexity')
    draw_subplot('SLoC', 'Loop nesting depth')
else:
    columns = {'LoC': 1, 'SLoC': 2, 'AST nodes': 3, 'AST height': 4}
    plt.figure(num='Files', figsize=(12, 8))
    draw_subplot('LoC', 'SLoC')
    draw_subplot('SLoC', 'AST nodes')
    draw_subplot('SLoC', 'AST height')

plt.tight_layout()
plt.savefig(img_out_path)
