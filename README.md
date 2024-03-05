# OpenSearch User Behavior Insights

OpenSearch RFC - https://github.com/opensearch-project/OpenSearch/issues/12084

This project is the OpenSearch plugin for storing and managing search requests and responses along with events produced client-side.

## Building and Usage

Build the plugin.  Note that you will have to match up the JDK 11 on your system to java home in the `gradle.properties` file:

`./gradlew build`

Build the OpenSearch docker image and add the plugin:

`docker compose build`

Start the containers:

`docker compose up`

Initialize the `awesome` UBL store:

```
curl -X PUT "http://localhost:9200/_plugins/ubi/awesome?index=ecommerce&id_field=name"
```

Send an event to the `awesome` store:

```
curl -X POST http://localhost:9200/_plugins/ubi/awesome -H "Content-Type: application/json" -d @./scripts/instant-search.json
```

Get events:

```
curl -s http://localhost:9200/.awesome_events/_search | jq
```

```
curl -s http://localhost:9200/.awesome_events/_search -H 'Content-Type: application/json' -d '{"query": {"term": {"type": "instant-search"}}}' | jq
```

Do a search of the `ecommerce` index:

```
curl -s http://localhost:9200/ecommerce/_search -H "X-ubi-store: awesome" | jq
```

Get queries:

```
curl -s http://localhost:9200/.awesome_queries/_search | jq
```

Delete the store:

```
curl -X DELETE http://localhost:9200/_plugins/ubi/awesome
```

Get the stores:

```
curl http://localhost:9200/_plugins/ubi
```

## Load Test

The `load-test` directory contains a basic load testing example. The purpose of the files under `load-test` are to provide a means of testing the plugin's ability to receive and store a large number of events over time. To use the load test, first start OpenSearch on `localhost:9200`, and then:

```
cd load-test
source ./venv/bin/activate
./run.sh
```

The test will run for 10 seconds. The number of events sent will be shown along with the `_count` of events in the store:

```
Type     Name                                                                          # reqs      # fails |    Avg     Min     Max    Med |   req/s  failures/s
--------|----------------------------------------------------------------------------|-------|-------------|-------|-------|-------|-------|--------|-----------
POST     /_plugins/ubi/mystore                                                              8     0(0.00%) |      8       6       9      8 |    0.81        0.00
--------|----------------------------------------------------------------------------|-------|-------------|-------|-------|-------|-------|--------|-----------
         Aggregated                                                                         8     0(0.00%) |      8       6       9      8 |    0.81        0.00

Found 8 indexed
```

This shows 8 total requests made by locust, and 8 events are in the index. The idea being we can assert that the number of events sent matches the events stored in the index.