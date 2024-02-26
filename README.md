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
curl -X PUT http://localhost:9200/_plugins/ubi/awesome
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

Get queries:

```
curl -s http://localhost:9200/.awesome_queries/_search -H "X-ubi-store: awesome" | jq
```

Delete the store:

```
curl -X DELETE http://localhost:9200/_plugins/ubi/awesome
```