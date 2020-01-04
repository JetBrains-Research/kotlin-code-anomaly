from typing import List
from pathlib import Path
from typing import Iterable, Tuple
import csv

import torch
from matplotlib import pyplot as plt
import numpy as np

import data_read
from autoencoder_model import AutoencoderModel


def calculate_differences(model, x):
    data_dim = x.shape[1]
    x_predicted = model(x).detach()
    return torch.pow(x_predicted - x, 2).sum(dim=1) / data_dim


def calc_mean_std(model: AutoencoderModel, data_sources: Iterable[data_read.DataSource], batch_size: int):
    data = data_read.read_torch_batches(data_sources, batch_size)
    differences = torch.cat([model.calculate_differences(vectors) for vectors, _ in data]).numpy()
    return differences.mean(), differences.std()


def conv_name(old_name):
    file_path, method_sign = old_name.split('.txt,')
    file_path = file_path + '.kt'
    return file_path + ':' + method_sign


def search_anomalies_by_threshold(model: AutoencoderModel, data_sources, thresholds: Tuple[float, float], save_path: Path,
                                  batch_size: int):
    indices_start = 1
    for vectors, names in data_read.read_torch_batches(data_sources, batch_size):
        differences = calculate_differences(model, vectors).numpy()
        anomalies_map = np.logical_or(differences < thresholds[0], differences > thresholds[1])
        if anomalies_map.any():
            anomalies_indices = np.argwhere(anomalies_map).flatten()
            anomalies = [
                (indices_start + anomaly_index, conv_name(names[anomaly_index]), differences[anomaly_index])
                for anomaly_index in anomalies_indices
            ]
            with save_path.open('at') as anomalies_file:
                anomalies_writer = csv.writer(anomalies_file, delimiter=',')
                for anomaly in anomalies:
                    anomalies_writer.writerow(anomaly)
        indices_start += len(vectors)


def search_anomalies(model: AutoencoderModel, data_sources: List[data_read.DataSource], threshold_deviations: float,
                     save_path: Path, batch_size: int):
    mean, std = calc_mean_std(model, data_sources, batch_size)
    print(f'Found mean error {mean} and standard deviation {std}')
    thresholds = mean - std * threshold_deviations, mean + std * threshold_deviations
    print(f'The thresholds are {thresholds}')
    search_anomalies_by_threshold(model, data_sources, thresholds, save_path, batch_size)
