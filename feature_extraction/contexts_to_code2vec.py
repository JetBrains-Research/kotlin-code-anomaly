#!/usr/bin/python3

import argparse
import itertools
from enum import Enum
from pathlib import Path
from random import random


class DataSet(Enum):
    TRAIN = 1
    EVAL = 2
    TEST = 3


BUFSIZE = 2 ** 20
MESSAGE_PERIOD = 1000


class DataSetSelector:
    def __init__(self, val_share, test_share):
        self.val_threshold = val_share
        self.test_threshold = val_share + test_share

    def select(self):
        val = random()
        if val < self.val_threshold:
            return DataSet.EVAL
        if val < self.test_threshold:
            return DataSet.TEST
        return DataSet.TRAIN


def read_contexts_dirs(context_dirs):
    for source_data_path in context_dirs:
        data_dir = source_data_path / 'kt'
        for contexts_file_path in data_dir.glob('path_contexts_*.csv'):
            contexts_file_name = contexts_file_path.with_suffix('').name
            part_number = int(contexts_file_name[contexts_file_name.rfind('_') + 1:])
            method_paths_file_path = data_dir / f'full_method_paths_{part_number}.csv'
            with contexts_file_path.open('rt', buffering=BUFSIZE, encoding='utf8') as contexts_file, \
                    method_paths_file_path.open('rt', buffering=BUFSIZE, encoding='utf8') as method_paths_file:
                yield from zip(contexts_file, method_paths_file)


class ContextsWriter:
    def __init__(self, data_path, names_path):
        self.data_path = data_path
        self.names_path = names_path
        self.data_file = None
        self.names_file = None

    def __enter__(self):
        self.data_file = self.data_path.open('wt', buffering=BUFSIZE, encoding='utf8')
        self.names_file = self.names_path.open('wt', buffering=BUFSIZE, encoding='utf8')
        return self

    def __exit__(self, exp_type, exp_value, exp_tr):
        self.data_file.close()
        self.names_file.close()

    def write(self, method_contexts, method_full_name):
        self.data_file.write(method_contexts)
        self.names_file.write(method_full_name)


def get_method_project(method_path):
    method_path_parts = method_path.split('/')
    return method_path_parts[0] + '/' + method_path_parts[1]


def tts_methods(args):
    projects = {get_method_project(method_path) for contexts, method_path in read_contexts_dirs(args.data_paths)}
    projects_iter = iter(projects)
    selected_projects = set(itertools.islice(projects_iter, args.projects_number))
    print(f'{len(selected_projects)} projects selected')

    out_dir = args.out_dir
    dataset_name = args.dataset_name
    with ContextsWriter(out_dir / f'{dataset_name}.train.raw.txt', out_dir / f'{dataset_name}.train.raw.names.txt') \
            as train_writer, \
            ContextsWriter(out_dir / f'{dataset_name}.val.raw.txt', out_dir / f'{dataset_name}.val.raw.names.txt') \
            as eval_writer, \
            ContextsWriter(out_dir / f'{dataset_name}.test.raw.txt', out_dir / f'{dataset_name}.test.raw.names.txt') \
            as test_writer:
        dataset_selector = DataSetSelector(args.val_share, args.test_share)
        methods_counter = 0
        for contexts, method_path in read_contexts_dirs(args.data_paths):
            if get_method_project(method_path) not in selected_projects:
                continue
            dataset = dataset_selector.select()
            if dataset == DataSet.TRAIN:
                train_writer.write(contexts, method_path)
            elif dataset == DataSet.EVAL:
                eval_writer.write(contexts, method_path)
            else:
                test_writer.write(contexts, method_path)
            methods_counter += 1
            if methods_counter % MESSAGE_PERIOD == 0:
                print(f'{methods_counter} methods processed')
        print(f'{methods_counter} methods processed')


def tts_projects(args):
    projects = {get_method_project(method_path) for contexts, method_path in read_contexts_dirs(args.data_paths)}
    projects_iter = iter(projects)
    train_projects = set(itertools.islice(projects_iter, args.train_size))
    val_projects = set(itertools.islice(projects_iter, args.val_size))
    test_projects = set(itertools.islice(projects_iter, args.test_size))
    print(f'{len(train_projects)} train, {len(val_projects)} validation and {len(test_projects)} test '
          f'projects selected')
    
    out_dir = args.out_dir
    dataset_name = args.dataset_name
    with ContextsWriter(out_dir / f'{dataset_name}.train.raw.txt', out_dir / f'{dataset_name}.train.raw.names.txt') \
            as train_writer, \
            ContextsWriter(out_dir / f'{dataset_name}.val.raw.txt', out_dir / f'{dataset_name}.val.raw.names.txt') \
            as eval_writer, \
            ContextsWriter(out_dir / f'{dataset_name}.test.raw.txt', out_dir / f'{dataset_name}.test.raw.names.txt') \
            as test_writer:
        methods_counter = 0
        for contexts, method_path in read_contexts_dirs(args.data_paths):
            project = get_method_project(method_path)
            if project in train_projects:
                train_writer.write(contexts, method_path)
            elif project in val_projects:
                eval_writer.write(contexts, method_path)
            elif project in test_projects:
                test_writer.write(contexts, method_path)
            methods_counter += 1
            if methods_counter % MESSAGE_PERIOD == 0:
                print(f'{methods_counter} methods processed')
        print(f'{methods_counter} methods processed')


def merge(args):
    with ContextsWriter(args.target_path, args.target_names_path) as target_writer:
        methods_counter = 0
        for context, method_path in read_contexts_dirs(args.data_paths):
            target_writer.write(context, method_path)
            methods_counter += 1
            if methods_counter % MESSAGE_PERIOD == 0:
                print(f'{methods_counter} methods processed')
        print(f'{methods_counter} methods processed')


if __name__ == '__main__':
    argument_parser = argparse.ArgumentParser()
    subparsers = argument_parser.add_subparsers()

    tts_methods_parser = subparsers.add_parser('tts_methods')
    tts_methods_parser.add_argument('--dataset_name', help='Name of the dataset to process')
    tts_methods_parser.add_argument('--out_dir', help='Output directory', type=Path)
    tts_methods_parser.add_argument('--val_share', help='Share of the validation data', type=float)
    tts_methods_parser.add_argument('--test_share', help='Share of the test data', type=float)
    tts_methods_parser.add_argument('--data_paths', help='Paths to the source data', nargs='*', type=Path)
    tts_methods_parser.add_argument('--projects_number', help='Number', type=int)
    tts_methods_parser.set_defaults(func=tts_methods)

    tts_projects_parser = subparsers.add_parser('tts_projects')
    tts_projects_parser.add_argument('--dataset_name', help='Name of the dataset to process')
    tts_projects_parser.add_argument('--out_dir', help='Output directory', type=Path)
    tts_projects_parser.add_argument('--train_size', help='Size of the training dataset', type=int)
    tts_projects_parser.add_argument('--val_size', help='Size of the validation dataset', type=int)
    tts_projects_parser.add_argument('--test_size', help='Size of the testing dataset', type=int)
    tts_projects_parser.add_argument('--data_paths', help='Paths to the source data', nargs='*', type=Path)
    tts_projects_parser.set_defaults(func=tts_projects)

    merge_parser = subparsers.add_parser('merge')
    merge_parser.add_argument('--target_path', help='Path to the place dataset', type=Path)
    merge_parser.add_argument('--target_names_path', help='Path to place the method names file', type=Path)
    merge_parser.add_argument('--data_paths', help='Paths to the source data', nargs='*', type=Path)
    merge_parser.set_defaults(func=merge)

    arguments = argument_parser.parse_args()

    arguments.func(arguments)
