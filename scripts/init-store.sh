#!/bin/bash -e

HOST=${1:-"localhost"}
STORE_NAME="awesome"

curl -X PUT http://${HOST}:9200/_plugins/search_relevance/${STORE_NAME}
