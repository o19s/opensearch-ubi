# OpenSearch User Behavior Insights

This repository contains the OpenSearch plugin for the User Behavior Insights (UBI) capability. This plugin
facilitates persisting client-side events (e.g. item clicks, scroll depth) and OpenSearch queries for the purpose of analyzing the data
to improve search relevance and user experience.

## Quick Start

Build the plugin.  Note that you will have to match up the JDK 11 on your system to java home in the `gradle.properties` file:

`./gradlew build`

Build the OpenSearch docker image and add the plugin, then start the containers:

```
docker compose build
docker compose up
```

Or to start a three-node OpenSearch cluster:

```
docker compose build
docker compose -f docker-compose-cluster.yaml up
```

Initialize the `awesome` UBI store:

```
curl -X PUT "http://localhost:9200/_plugins/ubi/awesome?index=ecommerce&id_field=id"
```

Send an event to the `awesome` store:

```
curl -X POST http://localhost:9200/_plugins/ubi/mystore -H "Content-Type: application/json" -d '
{
  "action_name": "search",
  "user_id": "98fcf189-4fa8-4322-b6f5-63fbb6b556c9",
  "timestamp": 1705596607509
}'
```

Get events:

```
curl -s http://localhost:9200/.awesome_events/_search | jq
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

## What does the OpenSearch UBI plugin do?

The OpenSearch plugin architecture was chosen for UBI due to its maturity, community support, and wide usage by other OpenSearch and community projects. The OpenSearch plugin architecture allows for creating and extensively testing custom OpenSearch API endpoints and interacting with OpenSearch (creating indexes, indexing data, etc.) using the native OpenSearch client. Other considerations for utilizing a plugin for UBI was for control over the UBI release cycle, providing simultaneous compatibility with multiple OpenSearch versions, and extensibility that does not lock UBI into any specific design choices. For instance, UBI stores client-side events and queries in OpenSearch indexes but this could be extended to use a different backend store.

With these capabilities of the plugin architecture, UBI can offer a REST interface for initializing and managing stores, persisting client-side events and queries, and coordinating the correlation between client-side events and queries.

The UBI plugin:

* Provides a REST interface for initializing, listing, and deleting UBI stores.
* Provides a REST interface for receiving and persisting client-side events in OpenSearch (via an abstraction for extensibility).
* Passively listens for OpenSearch queries and persists queries run on an OpenSearch index with a corresponding UBI store.
* Generates a `query_id` for queries when required (when not provided by the client).
* Sets timestamps for received client-side events when required.
* Validates format and syntax of received client-side events and UBI store parameters.
* Provides a standard set of schemas for indexing client-side events and OpenSearch queries.
* Allows the user to set a `key_field` to be used as a document's unique id instead of the document's `_id` field.
* Manages user-configurable settings (`index` name, `key_field`, and others) of UBI stores.

Things the plugin does not currently do but are goals of the plugin:

* Provide the ability to dump UBI data to a format common for data analysis.
* Facilitate a method of bulk importing of historical data from a non-UBI source.

## UBI Store

The plugin has a concept of a "store", which is a logical collection of the events and queries. A store consists of two indices. One
index is used to store events, and the other index is for storing queries.

### OpenSearch Data Mappings

#### Schema for events:

The current event mappings file can be found [here](https://github.com/o19s/opensearch-ubi/blob/main/src/main/resources/events-mapping.json).

**Primary fields include:**
- `action_name` - (size 100) - any name you want to call your event
- `timestamp` - unix epoch time. if not set, will be set by the plugin when the event is received
- `user_id`. `session_id`, `page_id` - (size 100) - are id's largely at the calling client's discretion for tracking users, sessions and pages
- `query_id` - (size 100) - ID for some query.  Note that it could be a unique search string, or it could represent a cluster of related searches (i.e.: *dress*, *red dress*, *long dress* could all have the same `query_id`).  Either the client could control these, or the `query_id` could be retrieved from the API's response headers as it keeps track of queries on the node
- `message_type` - (size 100) - originally thought of in terms of ERROR, INFO, WARN, but could be anything useful such as `QUERY` or `CONVERSION`.  Use to group `action_name` together.
- `message` - (size 256) - optional text for the log entry

**Other fields & data objects**
- `event_attributes.object` - contains an associated JSONified data object (i.e. books, products, user info, etc) if there are any
  - `event_attributes.object.object_id` - points to a unique, internal, id representing and instance of that object
  - `event_attributes.object.key_value` - points to a unique, external key, matching the item that the user searched for, found and acted upon (i.e. sku, isbn, ean, etc.). 
    **This field value should match the value in for the object's value in the `id_field` [below](#id_field) from the search store**
     It is possible that the `object_id` and `key_value` match if the same id is used both internally for indexing and externally for the users. 
  - `event_attributes.object.object_type` - indicates the type/class of object
  - `event_attributes.object.description` - optional description of the object
  - `event_attributes.object.transaction_id` - optionally points to a unique id representing a successful transaction
  - `event_attributes.object.to_user_id` - optionally points to another user, if they are the recipient of this object
  - `event_attributes.object.object_detail` - optional data object/map of further data details
- `event_attributes.position` - nested object to track user events to the location of the event origins
  - `event_attributes.position.ordinal` - tracks the nth item within a list that a user could select, click
  - `event_attributes.position.{x,y}` - tracks x and y values, that the client defines
  - `event_attributes.position.page_depth` - tracks page depth
  - `event_attributes.position.scroll_depth` - tracks scroll depth
  - `event_attributes.position.trail` - text field for tracking the path/trail that a user took to get to this location

* Other mapped fields in the schema are intended to be optional placeholders for common attributes like `user_name`, `email`, `price`

**the users can dynamically add any further fields to the event mapping

####  Schema for queries:

The current query mappings file can be found [here](https://github.com/o19s/opensearch-ubi/blob/main/src/main/resources/queries-mapping.json).

- `timestamp` - A unix timestamp of when the query was received
- `query_id` - A unique ID of the query provided by the client or generated automatically by the plugin
- `query_response_id` - A unique ID for the collection of results for the query
- `user_id` - A user ID provided by the client
- `session_id` - An optional session ID provided by the client

## Plugin API

The plugin exposes a REST API for managing UBI stores and persisting events.

| Method | Endpoint                                                  | Purpose                                                                                                                                                                                                                                   |
|--------|-----------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `PUT`    | `/_plugins/ubi/{store}?index={index}&id_field={id_field}` | <p id="id_field">Initialize a new UBI store for the given index. The `id_field` is optional and allows for providing the name of a field in the `index`'s schema to be used as the unique result/item ID for each search result. If not provided, the `_id` field is used. </p>|
| `DELETE` | `/_plugins/ubi/{store}`                                   | Delete a UBI store                                                                                                                                                                                                                        |
| `GET` | `/_plugins/ubi`                                           | Get a list of all UBI stores                                                                                                                                                                                                              |
| `POST` | `/_plugins/ubi/{store}`                                   | Index an event into the UBI store                                                                                                                                                                                                         |
| `TRACE` | `/_plugins/ubi`                                           | For temporary developer debugging                                                                                                                                                                                                         |

### Creating a UBI Store

To create a UBI store to contain events and queries, send a `PUT` request:

```
curl -X PUT http://localhost:9200/_plugins/ubi/mystore?index=ecommerce
```

### Deleting a UBI Store

To delete a store, send a `DELETE` request:

```
curl -X DELETE http://localhost:9200/_plugins/ubi/mystore
```

This will delete the UBI store and all contained events and queries. Please use this with caution.

### Get a List of UBI Stores

To get a list of stores, send a `GET` request:

```
curl -X GET http://localhost:9200/_plugins/ubi
```

### Persist a Client-Side Event into a UBI Store

To persist a client-side event into a store, send a `POST` request where the body of the request is the event:

```
curl -X POST http://localhost:9200/_plugins/ubi/mystore -H "Content-Type: application/json" -d '
{
  "action_name": "search",
  "user_id": "98fcf189-4fa8-4322-b6f5-63fbb6b556c9",
  "timestamp": 1705596607509
}'
```

## Capturing Queries with UBI

### Associating a Query with Client-Side Events

The plugin passively listens to query requests passing through OpenSearch. Without any extra information,
the plugin cannot associate a query with the client-side events associated with the query. (What user clicked on what to make this query?)

To make this association, queries need to have a header value that indicates the user ID.

#### Required Headers

|Header|Purpose|Required?|Detail|
|---|---|---|---|
|`X-ubi-store`|Tells the plugin which store this query should be persisted to.|Required||
|`X-ubi-user-id`|Allow for associating client-side user events with the query|Required||
|`X-ubi-query-id`|The client can provide a query ID. If not provided, the plugin will generate a random query ID. The purpose of the query ID is to uniquely identify the query.|No|[query_id](query_id.md)|
|`X-ubi-session-id`|A session ID to associate with the query.|No||

### Example Queries

The following query tells the plugin that the query being run should be persisted to the store `mystore` and be associated with user ID `john`:

```
curl http://localhost:9200/ecommerce/_search -H "X-ubi-store: mystore" -H "X-ubi-user-id: 12345"
```

With this query, when the plugin sees a query, the plugin will be able to associate the query with an individual user and know to persist the query in the UBI store `mystore`.

[Sample SQL queries](documentation\queries\sql_queries.md)
[Sample OpenSearch queries](documentation\queries\dsl_queries.md)
