import time
from sklearn.svm import OneClassSVM

from common_io import *

start_time = time.time()
is_analyzing_methods = True

if is_analyzing_methods:
    csv_path = '../data/6proj_methodMetrics.csv'
    out_path = '../data/6proj_methods_svm.csv'
    img_out_path = '../data/6proj_methods_svm.png'
else:
    csv_path = '../data/6proj_fileMetrics.csv'
    out_path = '../data/6proj_files_svm.csv'
    img_out_path = '../data/6proj_files_svm.png'

n_neighbors = 5

labels, raw_data = read_data(csv_path)

classifier = OneClassSVM(kernel='rbf', nu=0.1, gamma=0.1)
classifier.fit(raw_data)
marks = classifier.predict(raw_data)

marked = np.column_stack((labels, raw_data, marks))
inlier_indices = np.asarray([int(row[-1]) > 0 for row in marked])
outlier_indices = np.asarray([int(row[-1]) < 0 for row in marked])
inliers = marked[inlier_indices]
outliers = marked[outlier_indices]

print_plots(inliers, outliers, img_out_path, is_for_methods=is_analyzing_methods)

end_time = time.time()
print(f"Total elapsed time: {end_time - start_time}")
