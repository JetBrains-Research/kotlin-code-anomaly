import os
import json
import csv

from .lib.helpers.TimeLogger import TimeLogger


def matrix2csv(input_file, output_file):
    time_logger = TimeLogger(task_name='Matrix to CSV transformation')

    with open(input_file, 'r') as matrix_file_descriptor:
        matrix = json.load(matrix_file_descriptor)

    with open(output_file, 'w') as csv_file_descriptor:
        csv_file_writer = csv.writer(csv_file_descriptor)
        for vector in matrix:
            csv_file_writer.writerow([str(x) for x in vector])

    time_logger.finish(full_finish=True)


# convertion without loading the full json file for RAM consumption optimization
def matrix2csv_streaming(input_path, output_path):
    with open(input_path, 'rt') as input_file, open(output_path, 'wt') as output_file:
        buff = input_file.read(READ_STEP)
        buff = buff[1:]
        output_writer = csv.writer(output_file)
        done = False
        while not done:
            while buff.find(']') == -1:
                buff = buff + input_file.read(READ_STEP)
            end_pos = buff.index(']') + 1
            part = json.loads(buff[0:end_pos])
            output_writer.writerow(part)
            buff = buff[end_pos:] + input_file.read(READ_STEP)
            if buff[0] == ']':
                done = True
            else:
                buff = buff[buff.index('['):]
