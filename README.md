# OpenSearch User Behavior Insights

This project is the OpenSearch plugin for storing and managing search requests and responses along with events produced client-side.

Related Links:
* OpenSearch UBI RFC - https://github.com/opensearch-project/OpenSearch/issues/12084

## Building the Plugin

Build the plugin

```
./gradlew build
```

Build the OpenSearch docker image and add the plugin, then start the containers:

```
docker compose build
docker compose up
```

See the [Quick Start](documentation/documentation.md#quick-start) section in the documentation for a complete example of how to initialize the plugin and store events and queries. 

## Releasing

Run `tag-and-release.sh` to create and push a tag to GitHub. The tag will kick off the GitHub Action to build the release. After running `tag-and-release.sh`, update the version numbers in `gradle.properties` and the `Dockerfile` and commit the changes.
