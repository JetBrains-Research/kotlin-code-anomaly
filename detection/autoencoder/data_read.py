from pathlib import Path
from itertools import islice
import multiprocessing
from typing import Iterable, Tuple, List

import torch


POOL_SIZE = multiprocessing.cpu_count()
BUFFER_SIZE = 4 * 2 ** 20


DataSourceNamed = Tuple[Path, Path]
RawDataPointNamed = Tuple[str, str]
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


DEFAULT_BATCH_SIZE = 100000


def read_raw_data_with_names(data_sources: Iterable[DataSourceNamed]) -> Iterable[RawDataPointNamed]:
    for data_path, names_path in data_sources:
        with data_path.open('rt', buffering=BUFFER_SIZE, encoding='utf-8') as data_file, \
                names_path.open('rt', buffering=BUFFER_SIZE, encoding='utf-8') as names_file:
            for data_point, name in zip(data_file, names_file):
                if not data_point.startswith('nan'):
                    vector = data_point
                    name = name.rstrip()
                    yield vector, name


def read_raw_data_no_names(data_sources: Iterable[Path]):
    for data_path in data_sources:
        with data_path.open('rt', buffering=BUFFER_SIZE, encoding='utf-8') as data_file:
            yield from filter(lambda data_point: not data_point.startswith('nan'), data_file)


def raw_data_with_names_to_batches(
        raw_iter: Iterable[RawDataPointNamed], max_size: int = -1, batch_size: int = DEFAULT_BATCH_SIZE
) -> Iterable[RawBatch]:
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


def raw_data_no_names_to_batches(
        raw_iter: Iterable[str], max_size: int = -1, batch_size: int = DEFAULT_BATCH_SIZE
) -> Iterable[RawBatch]:
    raw_iter = iter(raw_iter if max_size <= 0 else islice(raw_iter, max_size))
    while True:
        vectors = list(islice(raw_iter, batch_size))
        if vectors:
            yield vectors
        if len(vectors) < batch_size:
            break


def batches_to_torch(batch_iter: Iterable[RawBatch], with_names: bool = True) -> Iterable[TorchBatch]:
    if with_names:
        def batch_to_torch(batch):
            vectors, names = batch
            torch_vectors = list(get_pool().map(vector_str_to_float, vectors, chunksize=len(vectors) // POOL_SIZE))
            return torch.tensor(torch_vectors), names
    else:
        def batch_to_torch(batch):
            torch_vectors = list(get_pool().map(vector_str_to_float, batch, chunksize=len(batch) // POOL_SIZE))
            return torch.tensor(torch_vectors)
    return map(batch_to_torch, batch_iter)


def read_torch_batches(data_sources: Iterable[DataSourceNamed], batch_size: int) -> Iterable[TorchBatch]:
    raw_data = read_raw_data_with_names(data_sources)
    batches = raw_data_with_names_to_batches(raw_data, batch_size=batch_size)
    torch_batches = batches_to_torch(batches)
    return torch_batches


class TrainValDataSet:
    def __init__(self, data_sources: List[Path], size: int = None, batch_size: int = DEFAULT_BATCH_SIZE,
                 val_share: float = 0.2):
        self.data_sources = data_sources
        self.size = sum(1 for _ in read_raw_data_no_names(data_sources)) if size is None else size
        self.train_size = int(self.size * (1 - val_share))
        self.batch_size = batch_size
        print(f'Data set initialized with {self.size} data points')

    def get_train(self) -> Iterable[TorchBatch]:
        train_iter = islice(read_raw_data_no_names(self.data_sources), self.train_size)
        return batches_to_torch(raw_data_no_names_to_batches(train_iter, batch_size=self.batch_size), with_names=False)

    def get_val(self) -> Iterable[TorchBatch]:
        val_iter = islice(read_raw_data_no_names(self.data_sources), self.train_size, self.size)
        return batches_to_torch(raw_data_no_names_to_batches(val_iter, batch_size=self.batch_size), with_names=False)

    def get_train_val(self) -> Tuple[Iterable[TorchBatch], Iterable[TorchBatch]]:
        raw_iter = read_raw_data_no_names(self.data_sources)
        train_iter = islice(raw_iter, self.train_size)
        val_iter = islice(raw_iter, self.size - self.train_size)
        return batches_to_torch(raw_data_no_names_to_batches(train_iter, batch_size=self.batch_size), with_names=False), \
            batches_to_torch(raw_data_no_names_to_batches(val_iter, batch_size=self.batch_size), with_names=False)
