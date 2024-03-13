# OpenSearch User Behavior Insights

This repository contains the OpenSearch plugin for the User Behavior Insights (UBI) capability. This plugin
facilitates persisting client-side events (e.g. item clicks, scroll depth) and OpenSearch queries for the purpose of analyzing the data
to improve search relevance and user experience.

## Backend Store

The plugin uses OpenSearch indexes to store the client-side events and details about the queries going through OpenSearch.
This `Backend` is implemented as an interface to potentially allow other backends such as a database.

### OpenSearch Backend Store

The backend has a concept of a "store", which is a logical collection of the events and queries. In OpenSearch, a store consists of two indices. One
index is used to store events, and the other index is for storing queries.

#### OpenSearch Data Mappings

* The current event mappings file can be found [here](https://github.com/o19s/opensearch-ubi/blob/main/src/main/resources/events-mapping.json).
* The current query mappings file can be found [here](https://github.com/o19s/opensearch-ubi/blob/main/src/main/resources/queries-mapping.json).

Schema for events:

**Primary fields include:**
- `action_name` - (size 100)- any name you want to call your event
- `timestamp` - should be set automatically
- `user_id`. `session_id`, `page_id` - (size 100) - are id's largely at the calling client's discretion for tracking users, sessions and pages
- `query_id` - (size 100) - ID for some query.  Note that it could be a unique search string, or it could represent a cluster of related searches (i.e.: *dress*, *red dress*, *long dress* could all have the same `query_id`).  Either the client could control these, or the `query_id` could be retrieved from the API's response headers as it keeps track of queries on the node
- `message_type` - (size 100) - originally thought of in terms of ERROR, INFO, WARN...but could be anything useful such as `QUERY` or `PURCHASE`
- `message` - (size 256) - optional text for the log entry

**Other fields & data objects**
- `event_attributes` - contains various, common attributes associated with many user events
- `event_attributes.data` - contains an associated JSONified data object (i.e. products, user info, etc) if there are any
- `event_attributes.data.data_type` - indicates the type/class of object
- `event_attributes.data.data_id` - points to a unique id representing and instance of that object
- `event_attributes.data.description` - optional description of the object
- `event_attributes.data.transaction_id` - optionally points to a unique id representing a successful transaction
- `event_attributes.data.to_user_id` - optionally points to another user, if they are the recipient of this object
- `event_attributes.data.data_detail` - optional data object/map of further data details
- 
*Other mapped fields in the schema are intended to be optional placeholders for common attributes like `city`, `state`, `price`

**the users can dynamically add any further fields to the event mapping

## Plugin API

The plugin exposes a REST API.

| Method |Endpoint|Purpose|
|--------|--------|-------|
| `PUT`    | `/_plugins/ubi/{store}` | Create a new backend store|
| `DELETE` | `/_plugins/ubi/{store}` | Delete a backend store |
| `GET` | `/_plugins/ubi` | Get a list of all stores |
| `POST` | `/_plugins/ubi/{store}` | Index events into the store |
| `TRACE` | `/_plugins/ubi` | For temporary developer debugging  |

### Creating a Store

To create a store to contain events and queries, send a `PUT` request:

```
curl -X PUT http://localhost:9200/_plugins/ubi/mystore
```

### Deleting a Store

To delete a store, send a `DELETE` request:

```
curl -X DELETE http://localhost:9200/_plugins/ubi/mystore
```

This will delete the backend stores. Please use this with caution.

### Get a List of Stores

To get a list of stores, send a `GET` request:

```
curl -X GET http://localhost:9200/_plugins/ubi
```

### Persist a Client-Side Event into a Store

To persist a client-side event into a store, send a `POST` request where the body of the request is the event:

```
curl -X POST http://localhost:9200/_plugins/ubi/mystore -H "Content-Type: application/json" -d @./event.json
```

The content of `event.json` should correspond to the events mapping described earlier in this documentation.

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
|`X-ubi-query-id`|The client can provide a query ID. If not provided, the plugin will generate a random query ID. The purpose of the query ID is to uniquely identify the query.|No|[query_id](./query_id.md)|
|`X-ubi-session-id`|A session ID to associate with the query.|No||

### Example Queries

The following query tells the plugin that the query being run should be persisted to the store `mystore` and be associated with user ID `john`:

```
curl http://localhost:9200/ecommerce/_search -H "X-ubi-store: mystore" -H "X-ubi-user-id: 12345"
```

With this query, when the plugin sees a query, the plugin will be able to associate the query with an individual user and know to persist the query in the UBI store `mystore`.

[Sample SQL queries](getting-started\queries\sql_queries.md)
