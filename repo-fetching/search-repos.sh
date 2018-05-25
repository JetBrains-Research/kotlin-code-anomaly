#!/bin/bash
MAX_PAGE=10
for i in `seq 1 $MAX_PAGE`; do
	curl -G https://api.github.com/search/repositories \
		--data-urlencode "q=language:kotlin" \
		--data-urlencode "page=$i" \
		--data-urlencode "per_page=100" \
		--data-urlencode "archived=true" \
		--data-urlencode "sort=updated" \
		--data-urlencode "order=asc" \
		-H "Accept: application/vnd.github.preview" \
		| jq '[.items[:100][] | {full_name, stargazers_count, html_url, clone_url}]' \
		> code_$i.json &
done
