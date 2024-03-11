#!/bin/bash -e

# Delete the store if it exists.
curl -X DELETE "http://localhost:9200/_plugins/ubi/mystore"

# Create the store.
curl -X PUT "http://localhost:9200/_plugins/ubi/mystore?index=ecommerce"

# Index subset of Chorus data.
../index-chorus-data.sh `realpath ../../chorus-opensearch-edition`

# Insert events and queries.
locust -f load-test.py --headless -u 1 -r 1 --run-time 10s --host http://localhost:9200

# Let events index.
sleep 10

# Get count of indexed events.
EVENTS=`curl -s http://localhost:9200/.mystore_events/_count | jq .count`
echo "Found $EVENTS events"

# Get count of indexed queries.
QUERIES=`curl -s http://localhost:9200/.mystore_queries/_count | jq .count`
echo "Found $QUERIES queries"
