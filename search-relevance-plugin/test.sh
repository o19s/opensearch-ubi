#!/bin/bash -e

curl -X POST http://localhost:9200/_plugins/search_relevance -H "Content-Type: application/json" -d @instant-search.json

curl localhost:9200/searchrelevance/_search

