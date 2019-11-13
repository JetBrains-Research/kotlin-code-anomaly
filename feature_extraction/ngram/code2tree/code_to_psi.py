import sys
import pathlib
import shutil
import subprocess
import json
import re

from . import parse_psi


PATH_COMPILER = pathlib.Path('feature_extraction/ngram/code2tree/kotlin')
PATH_TEST_DIR = PATH_COMPILER / 'compiler' / 'testData' / 'psi' / 'anom'
COMMAND_GRADLE = str((PATH_COMPILER / 'gradlew').resolve())
ARGUMENT_GRADLE_ASSEMBLE = 'assemble'
ARGUMENT_GRADLE_GENERATE = ':compiler:generateTests'
ARGUMENT_GRADLE_RUN_TESTS = ':compiler:test'
ARGUMENT_GRADLE_STOP = '--stop'
COMMAND_GIT = 'git'
ARGUMENTS_GIT_CLONE_KOTLIN = [COMMAND_GIT, 'clone', 'https://github.com/PetukhovVictor/kotlin']
ARGUMENTS_GIT_CHECKOUT_COMMIT = [COMMAND_GIT, 'checkout', '45c8f0c24b6655580acbdc4e967bbe0cd5763ac0']
PACKAGE_PATTERN = re.compile('^\\s*package\\s+\\w+(\\.\\w+)*\\s*$')
BATCH_SIZE = 100000


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


def folder_to_psi(input_dir, output_dir):
    if not PATH_COMPILER.is_dir():
        subprocess.run(ARGUMENTS_GIT_CLONE_KOTLIN, cwd=PATH_COMPILER.parent)
        subprocess.run(ARGUMENTS_GIT_CHECKOUT_COMMIT, cwd=PATH_COMPILER)

    subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_ASSEMBLE], cwd=PATH_COMPILER)
    processed_files = 0
    for step in group_iter(input_dir.glob('**/*.kt'), BATCH_SIZE):
        for kt_file in step:
            rel_path = kt_file.relative_to(input_dir)
            masked_steps = mask_words(rel_path.parts)
            test_path = PATH_TEST_DIR.joinpath(*masked_steps)
            test_path.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy(str(kt_file), str(test_path))
            with kt_file.open('rt') as in_file, test_path.open('wt') as out_file:
                out_file.writelines(mask_package(in_file))

        subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_GENERATE], cwd=PATH_COMPILER)
        subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_RUN_TESTS], cwd=PATH_COMPILER)

        for psi_file in PATH_TEST_DIR.glob('**/*.txt'):
            rel_path = psi_file.relative_to(PATH_TEST_DIR)
            unmasked_steps = unmask_words(rel_path.with_suffix('.kt.json').parts)
            json_path = output_dir.joinpath(*unmasked_steps)
            json_path.parent.mkdir(parents=True, exist_ok=True)
            with json_path.open(mode='wt') as out_file:
                json.dump(parse_psi.psi_to_json(psi_file), out_file)
        shutil.rmtree(PATH_TEST_DIR)
        processed_files += len(step)
        print(f'Processed {processed_files} files')
    subprocess.run([COMMAND_GRADLE, ARGUMENT_GRADLE_STOP], cwd=PATH_COMPILER)


if __name__ == '__main__':
    folder_to_psi(pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2]))
