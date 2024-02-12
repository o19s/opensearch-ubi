#!/bin/bash -e

HOST=${1:-"localhost"}
STORE_NAME="awesome"

curl http://${HOST}:9200/.${STORE_NAME}_events/_search
