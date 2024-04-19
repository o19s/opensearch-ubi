
# Key UBI concepts

Although the named fields below follow a schema lends to easier analytics, the schema is dynamic and allows for users to add new dynamic fields where there is need.

[`user_id`](#user_id) represents a user.  When UBI is active, any query that this user does, will generate a new `query_id` for this `user_id`.

The purpose of the [`query_id`](#query_id)'s help link the user's raw query string to the results, as well as any subsequent action that the UBI client logs. 
When UBI is turned on, a *search client* will get a `query_id` back from OpenSearch, and is passed to the UBI client.  The UBI client then associates each subsequent event with this query until it receives a new query_id.

[`action_name`](#action_name) says what the name of the event is.  It can be any name, such as *login*, *logout*, *save*, *post*, *add_to_cart*...

 [`event_attributes`](#event_attributes)'s is where any relevant information about the event can be stored.  
 The two primary, predefined objects in the attributes are [`event_attributes.position`](#position), which contains 
 information on what part of the application the user is interacting with, 
 and [`event_attributes.object`](#object), which contains identifying information of the object returned from the query that the user interacts with (i.e.: a book, a product, a post, etc..).

# TODO: `key_field` rename?
The `object` structure has two ways to refer to the object:
- `event_attributes.object.object_id` is the unique id that OpenSearch can use internally to index the object.
- `event_attributes.object.catalog_id` is the id that a user could look up the object in a *catalog*

 Therefore, the `query_id` signals the beginning of a user's *Search Journey*,
`action_name` tells us how the user is interacting with the query results within the application, 
and `event_attributes.object` is referring to the precise query result that the user interacts with.

### OpenSearch Data Mappings

#### Schema for events:

The current event mappings file can be found [here](../src/main/resources/events-mapping.json).

**Primary fields include:**
- `application` <p id="application">
	&ensp; (size 100) - name of application tracking UBI events
- `action_name` <p id="action_name">
	&ensp; (size 100) - any name you want to call your event
- `timestamp`: \
   &ensp; Unix epoch time. <s>If not set , will be set by the plugin when the event is received</s> 
- `query_id`  <p id="query_id">
	&ensp;  (size 100) - ID for some query.  Note that it could be a unique search string, or it could represent a cluster of related searches (i.e.: *dress*, *red dress*, *long dress* could all have the same `query_id`).  Either the client could control these, or the `query_id` could be retrieved from the API's response headers as it keeps track of queries on the node
- `user_id`. `session_id`, `source_id`  <p id="user_id">
	&ensp; (size 100) - are id's largely at the calling client's discretion for tracking users, sessions and sources (i.e. pages) of the event.  
	The `user_id` must be consistent in both the `query` and `event` stores.
- `message_type`  \
	&ensp; (size 100) - originally thought of in terms of ERROR, INFO, WARN, but could be anything useful such as `QUERY` or `CONVERSION`.  
	Can be used to group `action_name` together in logical bins.

- `message`  \
	&ensp; (size 256) - optional text for the log entry

**Other attribute fields & data objects** <p id="event_attributes">
- `event_attributes.object`  \
	&ensp; represents the search result object (i.e. books, products, user info, etc) if there are any
  - `event_attributes.object.object_id` - points to a unique, internal, id representing and instance of that object
  - `event_attributes.object.catalog_id`  \
	&ensp; points to a unique, external key, matching the item that the user searched for, found and acted upon (i.e. sku, isbn, ean, etc.). 
    **This field value should match the value in for the object's value in the `catalog_field` [below](#catalog_field) from the search store**
     It is possible that the `object_id` and `key_value` match if the same id is used both internally for indexing and externally for the users. 
  - `event_attributes.object.object_type`  \
	&ensp; indicates the type/class of object
  - `event_attributes.object.description`  \
	&ensp; optional description of the object
  - `event_attributes.object.transaction_id`  \
	&ensp; optionally points to a unique id representing a successful transaction
  - `event_attributes.object.to_user_id`  \
	&ensp; optionally points to another user, if they are the recipient of this object, perhaps as a gift, from the user's `user_id`
  - `event_attributes.object.object_detail`  \
	&ensp; optional text for further data object details
	  - `event_attributes.object.object_detail.json`  \
	&ensp; if the user has a json object representing what was acted upon, it can be stored here; however, note that that could lead to index bloat if the json objects are large.
- `event_attributes.position`  \
	&ensp; nested object to track user events to the location of the event origins
  - `event_attributes.position.ordinal`  \
	&ensp; tracks the nth item within a list that a user could select, click
  - `event_attributes.position.{x,y}`  \
	&ensp; tracks x and y values, that the client defines
  - `event_attributes.position.page_depth`  \
	&ensp; tracks page depth
  - `event_attributes.position.scroll_depth`  \
	&ensp; tracks scroll depth
  - `event_attributes.position.trail`  \
	&ensp; text field for tracking the path/trail that a user took to get to this location

* Note the developers can add optional, dynamic fields like `user_name`, `email`, `price` per individual use-cases.

####  Schema for queries:

The current query mappings file can be found [here](../src/main/resources/queries-mapping.json).

- `timestamp`  \
	&ensp; A unix timestamp of when the query was received
- `query_id`  \
	&ensp; A unique ID of the query provided by the client or generated automatically 
- `query_response_id`  \
	&ensp; A unique ID for the collection of results for the query
- `user_id`  \
	&ensp; A user ID provided by the client
- `session_id`  \
	&ensp; An optional session ID provided by the client
