import itertools
import os
import numpy as np
import pandas
import webbrowser

data_path = "../out-data/out_top1k_no_pca"
compared_files = [
    "methods lof {'algorithm': 'ball_tree', 'contamination': 5e-05, 'n_neighbors': 2}.csv",
    "methods lof {'algorithm': 'ball_tree', 'contamination': 5e-05, 'n_neighbors': 5}.csv",
    "methods lof {'algorithm': 'ball_tree', 'contamination': 5e-05, 'n_neighbors': 10}.csv",
    # "methods lof {'algorithm': 'kd_tree', 'contamination': 5e-05, 'n_neighbors': 2}.csv",
    # "methods lof {'algorithm': 'kd_tree', 'contamination': 5e-05, 'n_neighbors': 5}.csv",
    # "methods lof {'algorithm': 'kd_tree', 'contamination': 5e-05, 'n_neighbors': 10}.csv",

    # "methods svm {'gamma': 0.1, 'kernel': 'poly', 'nu': 5e-05}.csv",
    # "methods svm {'gamma': 0.1, 'kernel': 'rbf', 'nu': 5e-05}.csv",
]
report_path = "../out-data/seim_reports/conf_1-3_intersection.txt"


def signature_to_url_and_fname(signature):
    signature = signature[6:]  # drop 'repos/'
    split_1 = signature.split(sep='__', maxsplit=1)
    account = split_1[0]
    signature = split_1[1]
    split_2 = signature.split(sep='.kt:', maxsplit=1)
    repo_and_filename = split_2[0]
    fun_name = split_2[1]
    split_3 = repo_and_filename.split(sep='/', maxsplit=1)
    repo = split_3[0]
    filepath = split_3[1]
    return np.array([f"https://github.com/{account}/{repo}/blob/master/{filepath}.kt", fun_name])


sets = []

for filename in compared_files:
    data = pandas.read_csv(os.path.join(data_path, filename))
    data = np.array(data)
    sets.append(set(data[:, 1]))

res = set.union(*sets)
out_info = np.array([signature_to_url_and_fname(entry) for entry in res])
np.savetxt(report_path, out_info, fmt='%s', delimiter='\n', newline='\n\n')

print(f"Total {len(res)} anomalies.")

print(f"Opening code in browser...")
for entry in out_info:
    webbrowser.open(entry[0], new=2)
