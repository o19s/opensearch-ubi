#!/bin/bash -e

curl -X POST http://localhost:9200/_plugins/search_relevance -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
{
  "type": "instant-search",
  "keywords": "khgkj",
  "timestamp": 1705596607509,
  "url": "file:///C:/jason/_dev/search/OpenSearch/src/search-collector/demo/order.html",
  "ref": "",
  "lang": "en-US",
  "session": "npckcy4",
  "channel": "demo-channel",
  "query": ""
}
EOF
