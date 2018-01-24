import json
import os
import subprocess

out_dir = '../repos/'
repos = json.load(open('repos.json'))


def git(*args):
    return subprocess.check_call(['git'] + list(args))


os.chdir(out_dir)
subprocess.run('ls')

for repo in repos:
    dir_name = repo['full_name'].replace('/', '__')
    try:
        git('clone', '--depth=1', repo['html_url'], dir_name)
    except subprocess.CalledProcessError:
        print(f'Skipping {dir_name} (could not fetch).')
