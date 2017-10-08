from sklearn.neighbors import LocalOutlierFactor

from common_io import *

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

# Load input data
labels, raw_data = read_data(csv_path)

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

print_plots(inliers, outliers, img_out_path, is_for_methods=is_analyzing_methods)
