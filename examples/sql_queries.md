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
where query_id ='14039d3e-b91d-4cf8-a11b-55d31d14d7ae'
order by timestamp
```
hits|query_response_id|query_id|user_id|query|session_id|timestamp
---|---|---|---|---|---|---
{id=1377142}|14039d3e-b91d-4cf8-a11b-55d31d14d7ae|14039d3e-b91d-4cf8-a11b-55d31d14d7ae|172_073f163d-b2d0-401b-b7fa-b060834350ee|Virtual flexibility systematic|6044bc19-78da-40d5-8e7b-725d00dd2a78_1916|1.8079822E9

In the event log
```sql
select 
	user_id, action_name, message_type, message
from .ubi_log_events
where query_id ='14039d3e-b91d-4cf8-a11b-55d31d14d7ae'
order by timestamp
```
user_id|action_name|message_type|message
---|---|---|---
172_073f163d-b2d0-401b-b7fa-b060834350ee|on_search|WARN|Virtual flexibility systematic
172_073f163d-b2d0-401b-b7fa-b060834350ee|brand_filter|WARN|
172_073f163d-b2d0-401b-b7fa-b060834350ee|product_sort|INFO|
172_073f163d-b2d0-401b-b7fa-b060834350ee|product_sort|INFO|Mandatory background adapter
172_073f163d-b2d0-401b-b7fa-b060834350ee|product_hover|INFO|concepto bifurcada multiplataforma
172_073f163d-b2d0-401b-b7fa-b060834350ee|button_click|INFO|Right-sized dynamic methodology
172_073f163d-b2d0-401b-b7fa-b060834350ee|product_sort|INFO|
172_073f163d-b2d0-401b-b7fa-b060834350ee|button_click|INFO|paradigma radical preventivo
172_073f163d-b2d0-401b-b7fa-b060834350ee|product_sort|REJECT|middleware vÃ­a web personalizable
172_073f163d-b2d0-401b-b7fa-b060834350ee|type_filter|INFO|Down-sized actuating paradigm
172_073f163d-b2d0-401b-b7fa-b060834350ee|brand_filter|INFO|
172_073f163d-b2d0-401b-b7fa-b060834350ee|button_click|INFO|
172_073f163d-b2d0-401b-b7fa-b060834350ee|brand_filter|REJECT|algoritmo intermedia orientado a equipos
172_073f163d-b2d0-401b-b7fa-b060834350ee|logout|PURCHASE|Automated uniform toolset

## User sessions
```sql
select 
	user_id, action_name, message_type, message
from .ubi_log_events
where query_id ='14039d3e-b91d-4cf8-a11b-55d31d14d7ae'
order by timestamp
```


Note that each new session starts with a login:

action_name|message_type|message|session_id
---|---|---|---
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

