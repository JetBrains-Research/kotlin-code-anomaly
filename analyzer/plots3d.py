import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

csv_path = '../data/6proj_methodMetrics.csv'
csv_marks_path = '../data/6proj_methods_marked_svm.csv'

raw_data = np.genfromtxt(csv_path, delimiter=',', comments=None)[:, 1:]
marks = np.genfromtxt(csv_marks_path)
marked = np.column_stack((raw_data, marks))

inlier_indices = np.asarray([int(row[-1]) > 0 for row in marked])
outlier_indices = np.asarray([int(row[-1]) < 0 for row in marked])
inliers = raw_data[inlier_indices]
outliers = raw_data[outlier_indices]

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

ax.scatter(outliers[:, 0], outliers[:, 1], outliers[:, 4], c='red', marker='^')
# ax.scatter(inliers[:, 0], inliers[:, 1], inliers[:, 4], c='blue', marker='o')

ax.set_xlabel('SLoC')
ax.set_ylabel('AST nodes')
ax.set_zlabel('Cyclomatic complexity')

plt.show()
