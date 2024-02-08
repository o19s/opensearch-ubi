# search-relevance-plugin

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
