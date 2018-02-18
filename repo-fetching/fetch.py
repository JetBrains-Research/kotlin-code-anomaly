import datetime as dt
import json
import os
import subprocess
import time

import pyjq
import requests

# region Configuration
search_path_dir = 'search-results/'
clone_dir = '../repos/'
repos = json.load(open('repos.json'))

repo_language = "kotlin"
global_start_date = "2009-01-01"
global_end_date = "2018-02-19"
# endregion

# region Utils
fmt = "%Y-%m-%d"

if not os.path.exists(search_path_dir):
    os.makedirs(search_path_dir)
if not os.path.exists(clone_dir):
    os.makedirs(clone_dir)


def git(*args):
    return subprocess.check_call(['git'] + list(args))


# endregion

# region Searching & fetching repo data
def search(date_from, date_until, page_number, per_page,
           filtered_file_name=None, should_save=True, sleep=True, auth=None):
    date_to = (dt.datetime.strptime(date_until, fmt) - dt.timedelta(1)).__format__(fmt)
    print(f"Query: {date_from}..{date_to}, page={page_number}")

    url = "https://api.github.com/search/repositories"
    headers = {"Accept": "application/vnd.github.preview"}
    params = dict(
        q=f"language:{repo_language} created:{date_from}..{date_to}",
        page=page_number,
        per_page=per_page,
        archived="true",
        sort="updated",
        order="asc"
    )
    if auth is None:
        resp_content = requests.get(url=url, headers=headers, params=params).json()
        if sleep:
            time.sleep(6)  # Unauthenticated GitHub API requests are limited to 10 per minute
    else:
        resp_content = requests.get(url=url, headers=headers, params=params, auth=auth).json()
        if sleep:
            time.sleep(2)  # Authenticated GitHub API requests are limited to 30 per minute
    jq_filter = "{total_count: .total_count, items: [.items[] | {full_name, clone_url}]}"
    filtered_content = pyjq.first(jq_filter, value=resp_content)
    count = filtered_content['total_count']
    print(f"\t{count:5} repos")

    if should_save:
        with open(filtered_file_name, mode="w+") as f:
            json.dump(filtered_content, fp=f, ensure_ascii=False, indent='\t')
            print(f"\tSaved to {filtered_file_name}")

    return count


def search_repos(key_points):
    for point in range(len(key_points) - 1):
        _from = key_points[point]
        _until = key_points[point + 1]
        for page in range(1, 11):
            search(date_from=_from, date_until=_until,
                   page_number=page, per_page=100,
                   filtered_file_name=f"{search_path_dir}repos_{_from}_until_{_until}_{page}.json", should_save=True,
                   # auth=('your_username', 'your_password')
                   )


def seek_key_points(start, end, start_delta):
    """Seeks key points that divide the timeline between [start] and [end] into chunks at which less than 1000
    GitHub repos were created, as GitHub only returns top 1000 results for each search request.
    Note: the function is not optimized for seeking key points for the whole global timeline."""
    key_points = [start]
    delta = start_delta
    while key_points[-1] < end:
        _from = key_points[-1]
        while True:
            _until = (dt.datetime.strptime(_from, fmt) + dt.timedelta(delta)).__format__(fmt)
            try:
                count = search(date_from=_from, date_until=_until,
                               page_number=1, per_page=1, should_save=False)
            except TypeError:
                print("Error! Exiting...")
                print(f"Intermediate results: {key_points}")
                return key_points
            if count < 1000:
                break
            delta -= 1
        print(f"\t\tNew keypoint: {_until}")
        key_points.append(_until)
    print(key_points)
    return key_points


# endregion

# region Cloning repos
# TODO rewrite
def clone_all():
    os.chdir(clone_dir)
    subprocess.run('ls')

    for repo in repos:
        dir_name = repo['full_name'].replace('/', '__')
        try:
            git('clone', '--depth=1', repo['html_url'], dir_name)
        except subprocess.CalledProcessError:
            print(f'Skipping {dir_name} (could not fetch).')


# endregion


# The line below shows usage of seek_key_points() at some subrange of the timeline.
# key_points = seek_key_points(start="2017-06-10", end=global_end, start_delta=11)

# Pre-calculated key points:
key_points = ["2009-01-01", "2015-06-02", "2015-12-02", "2016-02-27", "2016-05-01", "2016-07-07", "2016-09-08",
              "2016-10-29", "2016-12-07", "2017-01-17", "2017-02-20", "2017-03-21", "2017-04-16", "2017-05-12",
              "2017-05-24", "2017-06-01", "2017-06-10", "2017-06-21", "2017-07-01", "2017-07-11", "2017-07-22",
              "2017-08-02", "2017-08-13", "2017-08-23", "2017-09-02", "2017-09-12", "2017-09-20", "2017-09-28",
              "2017-10-07", "2017-10-15", "2017-10-23", "2017-10-30", "2017-11-05", "2017-11-11", "2017-11-17",
              "2017-11-23", "2017-11-29", "2017-12-04", "2017-12-09", "2017-12-15", "2017-12-21", "2017-12-28",
              "2018-01-04", "2018-01-10", "2018-01-16", "2018-01-21", "2018-01-26", "2018-01-31", "2018-02-05",
              "2018-02-09", "2018-02-13", "2018-02-18", "2018-02-19"]

search_repos(key_points)
