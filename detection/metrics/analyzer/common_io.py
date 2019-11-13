import numpy as np
import matplotlib.pyplot as plt


def read_data(csv_path):
    labels = np.genfromtxt(csv_path, delimiter=',', usecols=[0], dtype=None)
    raw_data = np.genfromtxt(csv_path, delimiter=',', comments=None)[:, 1:]
    return labels, raw_data


def print_plots(inliers, outliers, img_out_path, is_for_methods):
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
    if is_for_methods:
        columns = {'SLoC': 1, 'AST nodes': 2, 'AST height': 3, 'Loop nesting depth': 4, 'Cyclomatic complexity': 5}
        plt.figure(num='Methods', figsize=(12, 8))
        draw_subplot('SLoC', 'AST nodes')
        draw_subplot('AST nodes', 'AST height')
        draw_subplot('SLoC', 'Cyclomatic complexity')
        draw_subplot('Loop nesting depth', 'Cyclomatic complexity')
    else:
        columns = {'LoC': 1, 'SLoC': 2, 'AST nodes': 3, 'AST height': 4}
        plt.figure(num='Files', figsize=(12, 8))
        draw_subplot('LoC', 'SLoC')
        draw_subplot('SLoC', 'AST nodes')
        draw_subplot('SLoC', 'AST height')

    plt.tight_layout()
    plt.savefig(img_out_path)
