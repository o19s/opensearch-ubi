#!/bin/bash

curl http://localhost:9200/awesome/_search \
-H 'Content-Type: application/json; charset=utf-8' \
-d \
'
{
  "query": {
    "match": {
      "type": "instant-search"
    }
  }
}'