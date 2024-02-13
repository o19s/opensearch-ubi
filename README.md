# OpenSearch User Behavior Logging

This project is getting a new name! See the voting - https://forum.opensearch.org/t/vote-user-behavior-logging-and-insights/17838

OpenSearch RFC - https://github.com/opensearch-project/OpenSearch/issues/12084

This project is the OpenSearch plugin for storing and managing search requests and responses along with events produced client-side.

## Building and Usage

Build the plugin.  Note that you will have to match up the JDK 11 on your system to java home in the `gradle.properties` file:

`./gradlew build`

Build the OpenSearch docker image and add the plugin:

`docker compose build`

Start the containers:

`docker compose up`

Initialize the `awesome` search relevance index:

```
curl -X PUT http://localhost:9200/_plugins/search_relevance/awesome
```

Send an event to the `awesome` store:

```
curl -X POST http://localhost:9200/_plugins/search_relevance/awesome -H "Content-Type: application/json" -d @instant-search.json
```

Get events:

```
curl http://localhost:9200/.awesome_events/_search
```
