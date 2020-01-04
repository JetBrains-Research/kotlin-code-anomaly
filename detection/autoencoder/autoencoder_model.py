import numpy as np
import torch
from torch import nn
from time import time
from pathlib import Path
import csv

import data_read


class AutoencoderModel(nn.Module):
    def __init__(self, features_number, encoded_dim):
        super(AutoencoderModel, self).__init__()
        self.features_number = features_number
        self.encoded_dim = encoded_dim
        self.encoder = nn.Sequential(
            nn.Linear(features_number, encoded_dim),
            nn.ReLU()
        )
        self.decoder = nn.Sequential(
            nn.Linear(encoded_dim, features_number),
            nn.Sigmoid()
        )

    def forward(self, x):
        encoded = self.encoder(x)
        decoded = self.decoder(encoded)
        return decoded

    def calculate_differences(self, x):
        data_dim = x.shape[1]
        x_pred = self.forward(x).detach()
        differences = torch.pow(x_pred - x, 2).sum(dim=1) / data_dim
        return differences


def train(model: AutoencoderModel, data_set: data_read.TrainValDataSet, n_epochs: int, save_path_prefix: str,
          epochs_passed: int):
    optimizer = torch.optim.Adam(model.parameters())
    for epoch in range(epochs_passed + 1, epochs_passed + n_epochs + 1):
        print(f'Training epoch {epoch}')
        start_time = time()
        train_losses = []
        for vectors, names in data_set.get_train():
            optimizer.zero_grad()
            processed_vectors = model(vectors)
            loss = nn.functional.mse_loss(processed_vectors, vectors)
            loss.backward()
            optimizer.step()
            train_losses.append(loss.detach().cpu().numpy())
        val_losses = []
        for vectors, _ in data_set.get_val():
            processed_vectors = model(vectors)
            loss = nn.functional.mse_loss(processed_vectors, vectors)
            val_losses.append(loss.detach().cpu().numpy())
        print(f'Epoch {epoch} finished')
        print(f'Train loss: {np.mean(train_losses)}, validation loss: {np.mean(val_losses)}')
        print(f'Time: {time() - start_time}')
        if save_path_prefix:
            torch.save(model, save_path_prefix + str(epoch) + '.pth')
