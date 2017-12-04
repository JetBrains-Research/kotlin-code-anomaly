#!/bin/bash

# Loads info about top 100 Kotlin repositories
curl -G https://api.github.com/search/repositories \
	--data-urlencode "q=language:kotlin" \
	--data-urlencode "per_page=100" \
	--data-urlencode "sort=stars" \
	--data-urlencode "order=desc" \
	-H "Accept: application/vnd.github.preview" \
	| jq '[.items[:100][] | {full_name, stargazers_count, html_url}]' \
	> repos.json
