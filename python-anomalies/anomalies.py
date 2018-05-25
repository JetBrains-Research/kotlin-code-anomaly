import ast
import random

import itertools

import time
from scipy._lib.six import xrange, reduce

list_of_list = [[random.random() for _ in xrange(random.randint(1, 10))] for _ in xrange(10 ** 5)]


def naive():
    flatten_list = []
    for sublist in list_of_list:
        for item in sublist:
            flatten_list.append(item)


def plus_assign():
    flatten_list = []
    for sublist in list_of_list:
        flatten_list += sublist


def extend():
    flatten_list = []
    for sublist in list_of_list:
        flatten_list.extend(sublist)


def comprehensions():
    flatten_list = [item for sublist in list_of_list for item in sublist]


def with_itertools():
    flatten_list = itertools.chain.from_iterable(list_of_list)


def with_reduce():
    flatten_list = reduce(lambda y, x: y + x, list_of_list, [])


def with_sum():
    flatten_list = sum(list_of_list, [])


def plus_assign_2():
    flatten_list = []
    for sublist in list_of_list:
        flatten_list = flatten_list + sublist


def with_reduce_2():
    flatten_list = reduce(lambda y, x: y.extend(x) or y, list_of_list, [])


def measure_all():
    funcs = [naive, plus_assign, extend, comprehensions, with_itertools, with_reduce,
             with_sum, plus_assign_2, with_reduce_2]
    for func in funcs:
        start_time = time.time()
        func()
        time_diff = (time.time() - start_time) * 1000
        print(f"{func.__name__:18s}\t{time_diff:4.4f} ms")


def dump_ast():
    filename = 'anomalies.py'
    this_source = open(filename).read()
    tree = ast.parse(this_source, filename=filename)
    ast.dump(tree, annotate_fields=True, include_attributes=True)


measure_all()
