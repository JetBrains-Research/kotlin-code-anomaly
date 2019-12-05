import pathlib
import shutil
import subprocess
import json
import re
import argparse

#from . import parse_psi
import parse_psi


PATH_COMPILER = pathlib.Path('feature_extraction/ngram/code2tree/kotlin')
PATH_TEST_DIR = PATH_COMPILER / 'compiler' / 'testData' / 'psi' / 'anom'
COMMAND_GRADLE = str((PATH_COMPILER / 'gradlew').resolve())
ARGUMENT_GRADLE_ASSEMBLE = ':compiler:assemble'
ARGUMENT_GRADLE_GENERATE = ':compiler:generateTests'
ARGUMENT_GRADLE_RUN_TESTS = ':compiler:test'
ARGUMENT_GRADLE_STOP = '--stop'
COMMAND_GIT = 'git'
COMMIT_NUMBER = 'e321c9e4e7beb71be34bcaa55bc090fd5a94f857'
GIT_CLONE_KOTLIN = [COMMAND_GIT, 'clone', 'https://github.com/JetBrains/kotlin']
GIT_CHECKOUT_COMMIT = [COMMAND_GIT, 'checkout', COMMIT_NUMBER]
GIT_RESET = [COMMAND_GIT, 'reset', '--hard', COMMIT_NUMBER]
PACKAGE_PATTERN = re.compile('^\\s*package\\s+\\w+(\\.\\w+)*\\s*$')
BATCH_SIZE = 100000
FOLDER_SIZE = 320
FOLDER_DEPTH = 2


def mask_words(words):
    return [f'p{i}_{word}' for i, word in enumerate(words)]


def unmask_words(words):
    return [word[word.find('_') + 1:] for word in words]


def mask_package(lines):
    for line in lines:
        if PACKAGE_PATTERN.fullmatch(line):
            package_keyword, package_name = line.split()
            yield f'{package_keyword} {".".join(mask_words(package_name.split(".")))}'
        else:
            yield line


def number_to_path(number):
    parts = [f'c{(number // FOLDER_SIZE ** i) % FOLDER_SIZE}' for i in range(FOLDER_DEPTH)][::-1]
    parts = mask_words(parts)
    path = pathlib.Path(*parts).with_suffix('.kt')
    return path


def path_to_number(path):
    parts = path.with_suffix('').parts
    parts = unmask_words(parts)
    num_parts = [int(i[1:]) for i in parts]
    number = sum(num_part * FOLDER_SIZE ** i for i, num_part in enumerate(num_parts[::-1]))
    return number


def group_iter(it, n):
    it = iter(it)
    items_rest = True
    while items_rest:
        next_step = []
        try:
            for _ in range(n):
                next_step.append(next(it))
        except StopIteration:
            items_rest = False
        if next_step:
            yield next_step


class SourceChecker:
    def __init__(self, source_dir, target_dir, target_suffix):
        self.source_dir = source_dir
        self.target_dir = target_dir
        self.target_suffix = target_suffix

    def __call__(self, source_path):
        if source_path.is_dir():
            print('Is a directory, skipping: ' + str(source_path))
            return False

        relative_path = source_path.relative_to(self.source_dir)
        target_path = self.target_dir / relative_path.with_suffix(self.target_suffix)
        if target_path.exists():
            return False

        try:
            source_text = source_path.read_text()
        except UnicodeDecodeError as e:
            print('Failed to read, skipping ' + str(source_path))
            print(e)
            return False

        coroutines = False
        if 'COROUTINES_PACKAGE' in source_text:
            coroutines = True
        if 'suspend' in source_text:
            if 'suspend fun' in source_text or ': suspend' in source_text:
                coroutines = True
        if coroutines:
            print('Contains coroutines, skipping: ' + str(source_path))
            return False
        return True


def folder_to_psi(input_dir, output_dir, failures_path=None, parse_trees=False):
    if not PATH_COMPILER.is_dir():
        subprocess.run(GIT_CLONE_KOTLIN, cwd=PATH_COMPILER.parent)
        subprocess.run(GIT_CHECKOUT_COMMIT, cwd=PATH_COMPILER)
        subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_ASSEMBLE], cwd=PATH_COMPILER)

    target_suffix = '.kt.json' if parse_trees else '.txt'
    source_checker = SourceChecker(input_dir, output_dir, target_suffix)
    source_files = filter(source_checker, input_dir.glob('**/*.kt'))

    processed_files = 0
    for step in group_iter(source_files, BATCH_SIZE):
        source_files_paths = []
        file_counter = 0
        for kt_file in step:
            rel_path = kt_file.relative_to(input_dir)
            source_files_paths.append(rel_path)
            test_path = PATH_TEST_DIR / number_to_path(file_counter)
            file_counter += 1
            test_path.parent.mkdir(parents=True, exist_ok=True)
            try:
                with kt_file.open('rt') as in_file, test_path.open('wt') as out_file:
                    out_file.writelines(mask_package(in_file))
            except Exception as e:
                print("Error copying " + str(kt_file))
                print(e)

        subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_GENERATE], cwd=PATH_COMPILER)
        subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_RUN_TESTS], cwd=PATH_COMPILER)

        for file_to_convert in PATH_TEST_DIR.glob('**/*.kt'):
            file_number = path_to_number(file_to_convert.relative_to(PATH_TEST_DIR))
            psi_file = file_to_convert.with_suffix('.txt')
            if psi_file.exists():
                out_path = output_dir / source_files_paths[file_number]
                out_path = out_path.with_suffix(target_suffix)
                out_path.parent.mkdir(parents=True, exist_ok=True)
                if parse_trees:
                    with out_path.open(mode='wt') as out_file:
                        json.dump(parse_psi.psi_to_json(psi_file), out_file)
                else:
                    shutil.copy(str(psi_file), str(out_path))
            else:
                if failures_path is not None:
                    out_path = failures_path / source_files_paths[file_number]
                    out_path = out_path.with_suffix('.kt')
                    out_path.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copy(str(file_to_convert), str(out_path))
        shutil.rmtree(PATH_TEST_DIR)
        processed_files += len(step)
        print(f'Processed {processed_files} files')
    subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_STOP], cwd=PATH_COMPILER)


if __name__ == '__main__':
    argument_parser = argparse.ArgumentParser()
    argument_parser.add_argument('input_dir')
    argument_parser.add_argument('output_dir')
    argument_parser.add_argument('--failures')
    args = argument_parser.parse_args()

    input_dir = pathlib.Path(args.input_dir)
    output_dir = pathlib.Path(args.output_dir)
    failures_path = pathlib.Path(args.failures) if args.failures else None
    folder_to_psi(input_dir, output_dir, failures_path)
