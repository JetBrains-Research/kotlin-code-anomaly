import numpy as np
import pandas as pd
from sklearn.decomposition import PCA
from sklearn.svm import OneClassSVM

X_scaled_path = "X_51_scaled.csv"
out_marks_path = "marks.csv"

nu = 0.001
max_iter = 1000000

df_X = pd.read_csv(X_scaled_path, header=None, index_col=None)
X = np.array(df_X, dtype="float32")
del df_X

X = PCA(n_components=20).fit_transform(X)

clf = OneClassSVM(shrinking=True, kernel='linear', nu=nu, verbose=1)

clf.fit(X)
marks = clf.predict(X)

marks = pd.DataFrame(marks)
marks.to_csv(out_marks_path, header=False, index=False)
