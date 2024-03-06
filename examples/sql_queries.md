## Frequent queries
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



## Event type counts
```sql
select 
	action_name, count(0) Total  
from .ubi_log_events 
group by action_name
order by Total desc
```
action_name|Total
|---|---|
on_search|3199
brand_filter|3112
button_click|3150
type_filter|3149
product_hover|3132
product_sort|3115
login|2458
logout|1499
new_user_entry|208

### Events associated with queries
Since the `query_id` is set during client-side search, all events with associated with a query will have that same `query_id`
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
on_search|1329
brand_filter|669
button_click|648
product_hover|639
product_sort|625
type_filter|613
logout|408

## Sample Search Odyssy
In the query log:
```sql
select *
from .ubi_log_queries
where query_id ='0b72b056-294c-4d3b-9a4b-1468b4e9cb93'
order by timestamp
```
(In this generated data, the `query` field is plain text; however in the real implementation the query will be in internal OS formatting of the query and parameters.)
hits|query_response_id|query_id|user_id|query|session_id|timestamp
---|---|---|---|---|---|---
{id=1377142}|14039d3e-b91d-4cf8-a11b-55d31d14d7ae|14039d3e-b91d-4cf8-a11b-55d31d14d7ae|172_073f163d-b2d0-401b-b7fa-b060834350ee|Virtual flexibility systematic|6044bc19-78da-40d5-8e7b-725d00dd2a78_1916|1.8079822E9

In the event log
```sql
select 
  query_id, action_name, message_type, message, event_attributes.data.data_id, event_attributes.data.description, session_id, user_id
from .ubi_log_events 
where query_id = '0b72b056-294c-4d3b-9a4b-1468b4e9cb93'
order by timestamp
```
query_id|action_name|message_type|message|event_attributes.data.data_id| event_attributes.data.description|session_id|user_id
---|---|---|---|---|---|---|---
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|product_hover|PURCHASE||681577|StarTech.com DVI-I Coupler / Gender Changer - F/F|90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|product_hover|INQUERY|desafÃ­o explÃ­cita multicanal|2048070|StarTech.com 5ft Desktop USB Extension Cable - A Male to A Female|90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|type_filter|INFO|conjunto mÃ³vil exclusivo|||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|product_sort|PURCHASE|Optional reciprocal service-desk|467909|APC ACF400 rack accessory|90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|on_search|WARN|Laptop|9105ea04-0e1e-4636-92fd-2ab1d91c9d8f||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|on_search|WARN|Laptop|9105ea04-0e1e-4636-92fd-2ab1d91c9d8f||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|brand_filter|INFO|Inverse exuding hierarchy|||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|button_click|WARN|analista vÃ­a web realineado|||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|product_sort|PURCHASE|soporte incremental descentralizado|77192955|MGA Entertainment Poopsie Slime Smash Style 2 motor skills toy|90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|type_filter|WARN|superestructura logÃ­stica monitoreado|||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|type_filter|ERROR||||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|type_filter|INFO||||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|type_filter|INFO||||90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea
0b72b056-294c-4d3b-9a4b-1468b4e9cb93|product_sort|REJECT|Secured actuating support|694281|StarTech.com 4 ft Cat5e Blue Snagless RJ45 UTP Cat 5e Patch Cable - 4ft Patch Cord|90e2f9c5-a034-4aba-8322-4fc8720c639b_1059|88_f4028096-e7f1-42e7-87a3-29973554deea

## User sessions
```sql
select 
	user_id, action_name, message_type, message
from .ubi_log_events
where query_id ='14039d3e-b91d-4cf8-a11b-55d31d14d7ae'
order by timestamp
```

* Note that each new session starts with a login.

action_name|message_type|message|session_id
---|---|---|---|
new_user_entry|ERROR|Profit-focused object-oriented pricing structure|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
login|PURCHASE|Pre-emptive global initiative|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
product_sort|INFO||a66d71cf-20f0-4e14-828c-4d15499b2776_1912
type_filter|INFO||a66d71cf-20f0-4e14-828c-4d15499b2776_1912
product_hover|INFO|Fundamental fault-tolerant superstructure|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
brand_filter|INFO|Optional client-server attitude|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
on_search|INFO|optimiza interfaces intuitivas|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
product_sort|PURCHASE|polÃ­tica holÃ­stica administrado|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
brand_filter|INFO||a66d71cf-20f0-4e14-828c-4d15499b2776_1912
type_filter|REJECT||a66d71cf-20f0-4e14-828c-4d15499b2776_1912
product_hover|INFO||a66d71cf-20f0-4e14-828c-4d15499b2776_1912
product_hover|REJECT||a66d71cf-20f0-4e14-828c-4d15499b2776_1912
logout|ERROR|Operative exuding algorithm|a66d71cf-20f0-4e14-828c-4d15499b2776_1912
login|PURCHASE||9710f6d9-b0de-418c-bea0-92aec33f0143_1913
product_hover|INFO|software tolerancia cero extendido|9710f6d9-b0de-418c-bea0-92aec33f0143_1913
product_sort|PURCHASE|monitorizar 6ta generaciÃ³n de arquitectura abierta|9710f6d9-b0de-418c-bea0-92aec33f0143_1913
product_sort|INFO||9710f6d9-b0de-418c-bea0-92aec33f0143_1913
button_click|ERROR||9710f6d9-b0de-418c-bea0-92aec33f0143_1913
login|PURCHASE|Distributed 3rdgeneration groupware|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
on_search|INFO|monetiza servicios web revolucionarias|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
brand_filter|WARN|Ergonomic systemic approach|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_sort|INFO|array orientada a soluciones orgÃ¡nico|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
type_filter|INFO|instalaciÃ³n estÃ¡tica diverso|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
brand_filter|REJECT|codificar explÃ­cita universal|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_hover|INFO||02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_hover|REJECT|conglomeraciÃ³n alto nivel con ingenierÃ­a inversa|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
type_filter|INFO||02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_sort|INFO|Intuitive modular moderator|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_hover|WARN|modelo no-volÃ¡til basado en objetos|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
brand_filter|PURCHASE||02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_sort|ERROR||02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
product_sort|REJECT|Reactive methodical pricing structure|02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
type_filter|INFO||02bd54ab-a362-4ebb-a5bc-f083963dc3b5_1914
login|REJECT||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
on_search|WARN|Consectetur enim sunt laborum adipisci occaecati reiciendis.|6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
product_hover|WARN||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
brand_filter|INFO||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
brand_filter|INFO||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
type_filter|INFO||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
product_hover|INFO|migraciÃ³n tangible programable|6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
type_filter|PURCHASE|Team-oriented tangible open system|6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
product_hover|PURCHASE||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
product_hover|INFO||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
product_hover|WARN||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
on_search|PURCHASE|Virtual systematic flexibility|6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915
logout|ERROR||6e589ef6-817d-4b94-b3ba-502829b7aa8d_1915

## List user sessions that logged out without any queries
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
156_c4130080-84ac-4b72-b333-9ed0d8715fc1|c016f7b7-41d4-40a1-bfd3-1d3c1ddf36e9_1926|1
170_08147749-8ef3-4b68-ac92-32f870e060af|565eb562-e32f-4262-a55a-7ccab1d251ac_2094|1
171_3254806f-7f76-4110-a002-e315c7253490|158ecffe-b354-4023-8338-9d6e5e9932dc_2113|1
18_47961ea2-f2c1-4577-912b-15f6944b0a83|fd0f37b1-ea38-4dff-ad91-9fdc311ab9ad_146|1
48_c7f7bfc5-1953-4959-b459-3956950de4ba|8e204c18-f121-486d-baf9-4c43723070cb_531|1


## TODO: searches without results
```sql
select 
  query_id, action_name, event_attributes.data.data_id as data_id, 
  event_attributes.data.description as data_description, user_id, session_id
from .ubi_log_events
where query_id = '2328c2d4-d501-4556-b878-2c5de9a7bacb'
order by timestamp
``sql