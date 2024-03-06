#!/bin/bash -e

CHORUS_HOME=`realpath ../chorus-opensearch-edition`
echo "Using CHORUS_HOME = ${CHORUS_HOME}"

TEMP_FILE=`mktemp`
head -n 100 ${CHORUS_HOME}/transformed_data.json > ${TEMP_FILE}

echo "Deleting index"
curl -s -X DELETE "localhost:9200/ecommerce"

echo "Creating index"
curl -s -X PUT "localhost:9200/ecommerce" -H "Content-Type: application/json" --data-binary @${CHORUS_HOME}/opensearch/schema.json
curl -s -X PUT "localhost:9200/ecommerce/_settings"  -H "Content-Type: application/json" -d '{"index.mapping.total_fields.limit": 20000}'

echo "Indexing data"
curl -s -X POST "localhost:9200/ecommerce/_bulk?pretty" -H "Content-Type: application/json" --data-binary @${TEMP_FILE}
