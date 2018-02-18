import json
import os
import subprocess

import pyjq
import requests

search_path_dir = 'search-results/'
out_dir = '../repos/'
repos = json.load(open('repos.json'))


def git(*args):
    return subprocess.check_call(['git'] + list(args))


def search_repos(max_page=-1):
    url = "https://api.github.com/search/repositories"
    headers = {"Accept": "application/vnd.github.preview"}
    json_file_name = 'repos.filtered.json'
    i = 5
    params = dict(
        q="language:kotlin",
        page=i,
        per_page=100,
        archived="true",
        sort="updated",
        order="asc"
    )
    resp_content = requests.get(url=url, headers=headers, params=params).json()
    with open('repos.full.json', mode="w+") as f:
        json.dump(resp_content, fp=f, ensure_ascii=False, indent='\t')

    jq_filter = "{total_count: .total_count, items: [.items[] | {full_name, clone_url}]}"
    filtered_content = pyjq.all(jq_filter, value=resp_content)
    with open(json_file_name, mode="w+") as f:
        json.dump(filtered_content, fp=f, ensure_ascii=False, indent='\t')


def clone_all():
    os.chdir(out_dir)
    subprocess.run('ls')

    for repo in repos:
        dir_name = repo['full_name'].replace('/', '__')
        try:
            git('clone', '--depth=1', repo['html_url'], dir_name)
        except subprocess.CalledProcessError:
            print(f'Skipping {dir_name} (could not fetch).')


search_repos()
