from pathlib import Path
from itertools import islice
import multiprocessing
from typing import Iterable, Tuple, List

import torch


POOL_SIZE = multiprocessing.cpu_count()
BUFFER_SIZE = 4 * 2 ** 20


DataSource = Tuple[Path, Path]
RawDataPoint = Tuple[str, str]
RawBatch = Tuple[List[str], List[str]]
TorchBatch = Tuple[torch.tensor, List[str]]


def vector_str_to_float(vector: str):
    return [float(i) for i in vector.split()]


POOL = None


def get_pool():
    global POOL
    if POOL is None:
        POOL = multiprocessing.Pool()
    return POOL


DEFAULT_BATCH_SIZE = 50000


def read_raw_data(data_sources: Iterable[DataSource]) -> Iterable[RawDataPoint]:
    for data_path, names_path in data_sources:
        with data_path.open('rt', buffering=BUFFER_SIZE, encoding='utf-8') as data_file, \
                names_path.open('rt', buffering=BUFFER_SIZE, encoding='utf-8') as names_file:
            for data_point, name in zip(data_file, names_file):
                if not data_point.startswith('nan'):
                    vector = data_point
                    name = name.rstrip()
                    yield vector, name


def raw_data_to_batches(raw_iter: Iterable[RawDataPoint], max_size: int = -1, batch_size: int = DEFAULT_BATCH_SIZE) -> \
        Iterable[RawBatch]:
    if max_size > 0:
        raw_iter = islice(raw_iter, max_size)
    finished = False
    while not finished:
        vectors, names = [], []
        try:
            for _ in range(batch_size):
                vector, name = next(raw_iter)
                vectors.append(vector)
                names.append(name)
        except StopIteration:
            finished = True
        if vectors:
            yield vectors, names


def batches_to_torch(batch_iter: Iterable[RawBatch]) -> Iterable[TorchBatch]:
    def batch_to_torch(batch):
        vectors, names = batch
        torch_vectors = list(get_pool().map(vector_str_to_float, vectors, chunksize=len(vectors) // POOL_SIZE))
        return torch.tensor(torch_vectors), names
    return map(batch_to_torch, batch_iter)


def read_torch_batches(data_sources: Iterable[DataSource], batch_size: int) -> Iterable[TorchBatch]:
    raw_data = read_raw_data(data_sources)
    batches = raw_data_to_batches(raw_data, batch_size=batch_size)
    torch_batches = batches_to_torch(batches)
    return torch_batches


class TrainValDataSet:
    def __init__(self, data_sources: List[DataSource], size: int = None, batch_size: int = DEFAULT_BATCH_SIZE,
                 val_share: float = 0.2):
        self.data_sources = data_sources
        self.size = sum(1 for _ in read_raw_data(data_sources)) if size is None else size
        self.train_size = int(self.size * (1 - val_share))
        self.batch_size = batch_size
        print(f'Data set initialized with {self.size} data points')

    def get_train(self) -> Iterable[TorchBatch]:
        train_iter = islice(read_raw_data(self.data_sources), self.train_size)
        return batches_to_torch(raw_data_to_batches(train_iter, batch_size=self.batch_size))

    def get_val(self) -> Iterable[TorchBatch]:
        val_iter = islice(read_raw_data(self.data_sources), self.train_size, self.size)
        return batches_to_torch(raw_data_to_batches(val_iter, batch_size=self.batch_size))
