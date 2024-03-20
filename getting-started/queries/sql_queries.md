# Sample UBI SQL queries
These can be performed on the OpenSearch Dashboards/Query Workbench: 
http(s):\//`{server}`:5601/app/opensearch-query-workbench

## Queries with zero results
Although it's trivial on the server side to find queries with no results, we can also get the same answer by querying the event side.
### Server-side
```sql
select
   count(0)
from .ubi_log_queries
where query_response_hit_ids is null
order by user_id
```

### Client event-side
```sql
select 
	count(0)
from .ubi_log_events
where action_name='on_search' and  event_attributes.data.data_detail.query_data.query_response_hit_ids is null
order by timestamp
```

Both client and server-side queries should return the same number.



## Trending queries
```sql
select 
	message, count(0) Total  
from .ubi_log_events 
where 
	action_name='on_search' 
group by message 
order by Total desc
```
message|Total
|---|---|
Virtual flexibility systematic|143
Virtual systematic flexibility|89
discrete desk service Cross group|75
Blanditiis quo sint repudiandae a sit.|75
Optio id quis alias at.|75
Consectetur enim sunt laborum adipisci occaecati reiciendis.|70
Like prepare trouble consider.|68
User Behavior Insights|65
cheapest laptop with i9|64
Cross group discrete service desk|63
Laptop|61
Amet maxime numquam libero ipsam amet.|59
fastest laptop|54
Voluptas iusto illum eum autem cum illum.|51
fortalece relaciones e-business|2
evoluciona comercio electrónico en tiempo real|2
incentiva canales escalables|1
incentiva ancho de banda inalámbrica|1
implementa sistemas de siguiente generación|1
implementa marcados eficientes|1


## Click position distribution counts
```sql
select event_attributes.position.ordinal as position, count(*) Total
FROM .ubi_log_events where event_attributes is not null
group by position
order by position
```
position|Total
|---|---|
1|769.0
2|815.0
3|838.0
4|739.0
5|710.0
6|171.0
7|177.0
8|169.0
9|175.0
10|160.0
11|160.0
12|179.0
13|171.0
14|157.0
15|161.0
16|45.0
17|34.0
18|37.0
19|36.0
20|40.0

## Event type distribution counts
To make a pie chart like widget on all the most common events:
```sql
select 
	action_name, count(0) Total  
from .ubi_log_events 
group by action_name
order by Total desc
```
action_name|Total
|---|---|
login|2542
button_click|2415
product_sort|2400
product_click|2378
product_hover|2343
type_filter|2340
on_search|2335
product_purchase|2331
brand_filter|2294
logout|1507
new_user_entry|207

### Events associated with queries
Since the `query_id` is set during client-side search, all events that are associated with a query will have that same `query_id`.
To make a pie chart like widget on the most common events preceded by a query:
```sql
select 
	action_name, count(0) Total  
from .ubi_log_events
where query_id is not null
group by action_name
order by Total desc
```
action_name|Total
|---|---|
on_search|2335
button_click|964
product_click|960
product_sort|958
product_purchase|956
type_filter|943
brand_filter|939
product_hover|919
logout|844


## Sample Search Odyssy
Find a search in the query log:
```sql
select *
from .ubi_log_queries
where query_id ='04f26fb9-2ef8-4a9c-a119-d9242a9c69f0'
order by timestamp
```
(In this generated data, the `query` field is plain text; however in the real implementation the query will be in the internal DSL of the query and parameters.)
query_response_id|query_id|user_id|query|query_response_hit_ids|session_id|timestamp
---|---|---|---|---|---|---
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|153_4e76d834-9501-4827-aa59-73e18454fff6|what is ubi?|4862510|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|1970-01-21 22:13:02.205

In the event log
Search for the events that correspond to the query above, `04f26fb9-2ef8-4a9c-a119-d9242a9c69f0`.
```sql
select 
  query_id, action_name, message_type, message, event_attributes.object.object_id, event_attributes.object.description, session_id, user_id
from .ubi_log_events
where query_id = '04f26fb9-2ef8-4a9c-a119-d9242a9c69f0'
order by timestamp
```
query_id|action_name|message_type|message|event_attributes.object.object_id|event_attributes.object.description|session_id|user_id
---|---|---|---|---|---|---|---
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|on_search|QUERY|what is ubi?|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|brand_filter|INFO||||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_hover|REJECT||2123502|C2G 15ft DB25 M/M Cable DB25M Gray|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|WARN|Horizontal multi-state customer loyalty|||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|INFO|Digitized bi-directional benchmark|||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_click|INQUERY|Synergistic 6thgeneration throughput|774894|Equip Cat.6A Pro S/FTP Patch Cable, 2m|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|type_filter|WARN|inteligencia artificial bifurcada de arquitectura abierta|||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_sort|PURCHASE||3794551|Philips PET741N/37 DVD/Blu-Ray player|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|INFO||||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_hover|INQUERY|marco de tiempo recÃ­proca enfocado al cliente|2130966|C2G Cat6 Snagless Patch Cable White 7m networking cable|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_hover|REJECT|funciÃ³n 6ta generaciÃ³n sincronizado|102436|APC Symmetra LX 16KVA on-line uninterruptible power supply (UPS) 16000 VA 11200 W|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|ERROR||||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|INFO||||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|logout|INFO|codificar radical totalmente configurable|||9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|153_4e76d834-9501-4827-aa59-73e18454fff6
## User sessions
To look at more sessions from the same user above, `153_4e76d834-9501-4827-aa59-73e18454fff6`. 
```sql
select 
	user_id, session_id, query_id, action_name, message_type, message, event_attributes.object.object_type, timestamp 
from .ubi_log_events
where user_id ='153_4e76d834-9501-4827-aa59-73e18454fff6'
order by timestamp
```
Results are truncated to a few sessions:

user_id|session_id|query_id|action_name|message_type|message|event_attributes.object.object_type|timestamp
---|---|---|---|---|---|---|---
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870||new_user_entry|ERROR|Synergistic object-oriented open architecture||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870||login|INFO|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870||product_hover|PURCHASE||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|on_search|QUERY|what is ubi?|query|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|product_sort|PURCHASE||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|type_filter|WARN|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|brand_filter|WARN|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|product_sort|PURCHASE|arquitectura abierta estable de arquitectura abierta|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|product_sort|PURCHASE||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|3d5cbaea-d1a3-4956-bfd0-0c61ee88272a|product_hover|INQUERY|Persevering solution-oriented solution|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|49218a79-8d69-460e-ab10-8805bf7620d0|on_search|QUERY|Virtual systematic flexibility|query|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|49218a79-8d69-460e-ab10-8805bf7620d0|product_click|INQUERY|infraestructura secundaria en red|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|49218a79-8d69-460e-ab10-8805bf7620d0|product_hover|REJECT||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|a9809114-1327-4751-a147-d42cb7c5c828|on_search|QUERY|benchmark cross-media methodologies|query|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|a9809114-1327-4751-a147-d42cb7c5c828|brand_filter|INFO|Assimilated well-modulated Internet solution||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|f8dfd2f1-1b50-498b-833a-22d0d8c0300c_1870|a9809114-1327-4751-a147-d42cb7c5c828|logout|ERROR|User-centric modular encryption||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871||login|INFO|interfaz grÃ¡fico de usuario multiestado de arquitectura abierta||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|03b1d43f-6352-462f-83f9-0fd8a1948653|on_search|QUERY|User Behavior Insights|query|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|on_search|QUERY|what is ubi?|query|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|product_hover|INQUERY||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|product_purchase|PURCHASE|middleware logÃ­stica reducido|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|type_filter|INFO|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|product_click|INQUERY||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|product_hover|INQUERY|marco de tiempo coherente realineado|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|product_click|INQUERY|Implemented uniform capacity|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|button_click|WARN|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|product_sort|PURCHASE|Re-engineered zero tolerance capacity|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|32ae16b8-c2a5-4edc-81a5-8198d301bd4c_1871|2fea6d7b-3bd6-401b-b6a2-8f930c2939e5|logout|INFO|Synergized value-added productivity||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872||login|ERROR|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|on_search|QUERY|what is ubi?|query|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|brand_filter|INFO|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_hover|REJECT||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|WARN|Horizontal multi-state customer loyalty||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|INFO|Digitized bi-directional benchmark||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_click|INQUERY|Synergistic 6thgeneration throughput|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|type_filter|WARN|inteligencia artificial bifurcada de arquitectura abierta||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_sort|PURCHASE||product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|INFO|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_hover|INQUERY|marco de tiempo recÃ­proca enfocado al cliente|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|product_hover|REJECT|funciÃ³n 6ta generaciÃ³n sincronizado|product|1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|ERROR|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|button_click|INFO|||1970-01-21 22:13:02.205
153_4e76d834-9501-4827-aa59-73e18454fff6|9c68eaa6-dd49-4349-8949-dc7e25ded12f_1872|04f26fb9-2ef8-4a9c-a119-d9242a9c69f0|logout|INFO|codificar radical totalmente configurable||1970-01-21 22:13:02.205
10:16:45


## List user sessions that logged out without any queries
- This query denotes users without a query_id.  Note that this could happen if the client side is not passing the returned query to other events.
```sql
select 
    user_id, session_id, count(0) EventTotal
from .ubi_log_events
where action_name='logout' and query_id is null
group by user_id, session_id
order by EventTotal desc
```
user_id|session_id|EventTotal
---|---|---
100_15c182f2-05db-4f4f-814f-46dc0de6b9ea|1c36712c-44b8-4fdd-8f0d-fdfeab5bd794_1290|1
175_e5f262f1-0db3-4948-b349-c5b95ff31259|816f94d6-8966-4a8b-8984-a2641d5865b2_2251|1
175_e5f262f1-0db3-4948-b349-c5b95ff31259|314dc1ff-ef38-4da4-b4b1-061f62dddcbb_2248|1
175_e5f262f1-0db3-4948-b349-c5b95ff31259|1ce5dc30-31bb-4759-9451-5a99b28ba91b_2255|1
175_e5f262f1-0db3-4948-b349-c5b95ff31259|10ac0fc0-409e-4ba0-98e9-edb323556b1a_2249|1
174_ab59e589-1ae4-40be-8b29-8efd9fc15380|dfa8b38a-c451-4190-a391-2e1ec3c8f196_2228|1
174_ab59e589-1ae4-40be-8b29-8efd9fc15380|68666e11-087a-4978-9ca7-cbac6862273e_2233|1
174_ab59e589-1ae4-40be-8b29-8efd9fc15380|5ca7a0df-f750-4656-b9a5-5eef1466ba09_2234|1
174_ab59e589-1ae4-40be-8b29-8efd9fc15380|228c1135-b921-45f4-b087-b3422e7ed437_2236|1
173_39d4cbfd-0666-4e77-84a9-965ed785db49|f9795e2e-ad92-4f15-8cdd-706aa1a3a17b_2206|1
173_39d4cbfd-0666-4e77-84a9-965ed785db49|f3c18b61-2c8a-41b3-a023-11eb2dd6c93c_2207|1
173_39d4cbfd-0666-4e77-84a9-965ed785db49|e12f700c-ffa3-4681-90d9-146022e26a18_2210|1
173_39d4cbfd-0666-4e77-84a9-965ed785db49|da1ff1f6-26f1-49d4-bd0d-d32d199e270e_2208|1
173_39d4cbfd-0666-4e77-84a9-965ed785db49|a1674e9d-d2dd-4da9-a4d1-dd12a401e8e7_2216|1
172_875f04d6-2c35-45f4-a8ac-bc5b675425f6|cc8e6174-5c1a-48c5-8ee8-1226621fe9f7_2203|1
171_7d810730-d6e9-4079-ab1c-db7f98776985|927fcfed-61d2-4334-91e9-77442b077764_2189|1
16_581fe410-338e-457b-a790-85af2a642356|83a68f57-0fbb-4414-852b-4c4601bf6cf2_156|1
16_581fe410-338e-457b-a790-85af2a642356|7881141b-511b-4df9-80e6-5450415af42c_162|1
16_581fe410-338e-457b-a790-85af2a642356|1d64478e-c3a6-4148-9a64-b6f4a73fc684_158|1

Since some of these query-less logouts repeat with some users, here is a query to see which users do this the most:
```sql
select 
    user_id, count(0) EventTotal
from .ubi_log_events
where action_name='logout' and query_id is null
group by user_id
order by EventTotal desc
```
user_id|EventTotal
---|---
87_5a6e1f8c-4936-4184-a24d-beddd05c9274|8
127_829a4246-930a-4b24-8165-caa07ee3fa47|7
49_5da537a3-8d94-48d1-a0a4-dcad21c12615|6
56_6c7c2525-9ca5-4d5d-8ac0-acb43769ac0b|6
140_61192c8e-c532-4164-ad1b-1afc58c265b7|6
149_3443895e-6f81-4706-8141-1ebb0c2470ca|6
196_4359f588-10be-4b2c-9e7f-ee846a75a3f6|6
173_39d4cbfd-0666-4e77-84a9-965ed785db49|5
52_778ac7f3-8e60-444e-ad40-d24516bf4ce2|5
51_6335e0c3-7bea-4698-9f83-25c9fb984e12|5
175_e5f262f1-0db3-4948-b349-c5b95ff31259|5
61_feb3a495-c1fb-40ea-8331-81cee53a5eb9|5
181_f227264f-cabd-4468-bfcc-4801baeebd39|5
185_435d1c63-4829-45f3-abff-352ef6458f0e|5
100_15c182f2-05db-4f4f-814f-46dc0de6b9ea|5
113_df32ed6e-d74a-4956-ac8e-6d43d8d60317|5
151_0808111d-07ce-4c84-a0fd-7125e4e33020|5
204_b75e374c-4813-49c4-b111-4bf4fdab6f26|5
29_ec2133e5-4d9b-4222-aa7c-2a9ae0880ddd|5
41_f64abc69-56ea-4dd3-a991-7d1fd292a530|5