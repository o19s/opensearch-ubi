# search-relevance-plugin

Build the plugin:

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
