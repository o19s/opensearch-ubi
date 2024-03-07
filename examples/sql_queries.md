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
(In this generated data, the `query` field is plain text; however in the real implementation the query will be in the internal DSL of the query and parameters.)
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
	user_id, session_id, query_id, action_name, message_type, message, event_attributes.data.data_type, timestamp 
from .ubi_log_events
where user_id ='17_a09194d2-6f28-4844-b38e-263b8435784a'
order by timestamp

```

* Note that each new session starts with a login.

user_id|session_id|query_id|action_name|message_type|message|event_attributes.data.data_type|timestamp
---|---|---|---|---|---|---|---
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||new_user_entry|WARN|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||login|WARN|estructura de precios global balanceado||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||product_sort|REJECT||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||product_purchase|PURCHASE|instalaciÃ³n neutral operativo|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||product_purchase|PURCHASE||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||brand_filter|INFO|Persistent zero-defect standardization||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||product_sort|PURCHASE|Cloned systemic groupware|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||product_hover|PURCHASE|Vision-oriented dedicated ability|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160||product_sort|INQUERY|data-warehouse dedicada enfocado a la calidad|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160|1393718a-2b20-4187-b9cd-08bea8d98ab9|on_search|INFO|Optio id quis alias at.|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160|1393718a-2b20-4187-b9cd-08bea8d98ab9|product_purchase|INQUERY|superestructura optimizada progresivo|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160|1393718a-2b20-4187-b9cd-08bea8d98ab9|button_click|INFO|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160|1393718a-2b20-4187-b9cd-08bea8d98ab9|product_purchase|PURCHASE||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|728cf0da-6dc3-43ad-81bf-73c411555bbf_160|1393718a-2b20-4187-b9cd-08bea8d98ab9|logout|INFO|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161||login|INFO|Secured tertiary access||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161||product_click|INQUERY|moderador innovadora preventivo|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161||brand_filter|ERROR|utilizaciÃ³n basado en contenido preventivo||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161||brand_filter|INFO|Devolved impactful hardware||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|99336ca5-538d-4823-9c7a-3c438b0cfab6|on_search|WARN|w00t|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|99336ca5-538d-4823-9c7a-3c438b0cfab6|brand_filter|WARN|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|99336ca5-538d-4823-9c7a-3c438b0cfab6|product_purchase|INQUERY|Centralized uniform alliance|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|99336ca5-538d-4823-9c7a-3c438b0cfab6|button_click|INFO|Fully-configurable zero administration instruction set||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|99336ca5-538d-4823-9c7a-3c438b0cfab6|button_click|INFO|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|99336ca5-538d-4823-9c7a-3c438b0cfab6|product_hover|PURCHASE||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|8ec9c322-bf44-4f72-954b-cc23ec7e0133|on_search|INFO|Consectetur enim sunt laborum adipisci occaecati reiciendis.|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|7bff4fe6-3b27-470e-b664-da62e6a5c0dc|on_search|INFO|reinvent cutting-edge e-commerce|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|7bff4fe6-3b27-470e-b664-da62e6a5c0dc|product_sort|INQUERY||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|7bff4fe6-3b27-470e-b664-da62e6a5c0dc|product_hover|INQUERY|Fundamental encompassing policy|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|a049e72c-cf15-4abf-bd7b-4a9a3da5c045_161|7bff4fe6-3b27-470e-b664-da62e6a5c0dc|type_filter|WARN|Proactive scalable customer loyalty||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||login|WARN|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||product_sort|PURCHASE|infraestructura ejecutiva integrado|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||product_hover|PURCHASE|conjunto regional de primera lÃ­nea|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||brand_filter|WARN|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||product_sort|PURCHASE|funcionalidad innovadora enfocado a ganancias|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||product_hover|REJECT|enfoque metÃ³dica balanceado|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||product_hover|INQUERY||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162||type_filter|INFO|||2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|aa14a24d-0b17-4019-9804-05cf3f993bd1|on_search|INFO|utiliza portales de clase mundial|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|5150a3b6-d13f-475f-9e52-5a2e5b95b5c2|on_search|WARN|agrega usuarios dinÃ¡micas|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|5150a3b6-d13f-475f-9e52-5a2e5b95b5c2|product_hover|INQUERY||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|5150a3b6-d13f-475f-9e52-5a2e5b95b5c2|product_hover|REJECT||product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|5150a3b6-d13f-475f-9e52-5a2e5b95b5c2|product_hover|INQUERY|actitud didÃ¡ctica proactivo|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|5150a3b6-d13f-475f-9e52-5a2e5b95b5c2|product_sort|INQUERY|concepto 24/7 versÃ¡til|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|5150a3b6-d13f-475f-9e52-5a2e5b95b5c2|product_click|INQUERY|Optional value-added methodology|product|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|f2440592-bc96-4902-9b45-ce940044944f|on_search|INFO|Consectetur enim sunt laborum adipisci occaecati reiciendis.|query|2027-04-17 10:16:45
17_a09194d2-6f28-4844-b38e-263b8435784a|58fc5768-2a8a-4a0a-b1b4-7d658b0a66a4_162|f2440592-bc96-4902-9b45-ce940044944f|logout|INFO|Optional reciprocal hub||2027-04-17 10:16:45

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
156_c4130080-84ac-4b72-b333-9ed0d8715fc1|c016f7b7-41d4-40a1-bfd3-1d3c1ddf36e9_1926|1
170_08147749-8ef3-4b68-ac92-32f870e060af|565eb562-e32f-4262-a55a-7ccab1d251ac_2094|1
171_3254806f-7f76-4110-a002-e315c7253490|158ecffe-b354-4023-8338-9d6e5e9932dc_2113|1
18_47961ea2-f2c1-4577-912b-15f6944b0a83|fd0f37b1-ea38-4dff-ad91-9fdc311ab9ad_146|1
48_c7f7bfc5-1953-4959-b459-3956950de4ba|8e204c18-f121-486d-baf9-4c43723070cb_531|1



## Queries with zero results
```sql
select
   user_id, session_id, query_id, query
from .ubi_log_queries
where query_response_hit_ids is null
order by user_id
```
user_id|session_id|query_id|query
---|---|---|---
100_0c4b48eb-6efa-406e-80f5-1a5925792492|f09c6736-3551-4cc5-a101-e5162ccce2ec_1218|1175a12d-fda6-40c3-8ed7-57e80de1a8a0|Optio id quis alias at.
100_0c4b48eb-6efa-406e-80f5-1a5925792492|52f85f24-5bde-4604-8d05-d6f505192203_1226|82a0220b-87b5-43d2-adbb-9415d24f8134|re-intermediate dynamic partnerships
101_1b24de1f-1dc0-4b21-9125-60657973cea0|209f0d5f-eabe-4c25-86b2-fb7794846e57_1237|2092ced1-1f5f-45cb-a9d1-92e1c2609889|Voluptas iusto illum eum autem cum illum.
104_e8170ee9-d48b-4948-a2d5-a2f9f0fa56e5|7174b18b-6ba1-4370-a884-71c43144c1c7_1269|e1312523-b693-413e-8486-0420be1f5ea6|Optio id quis alias at.
107_ecd1b629-fb99-4489-b691-0a50b8d4fd6b|65c4d41d-c28a-426e-b676-8267ede56f84_1295|33494d8a-a149-49c3-bd66-47b55cb6c082|Blanditiis quo sint repudiandae a sit.
107_ecd1b629-fb99-4489-b691-0a50b8d4fd6b|2aebda66-7f53-4dbb-a222-b4e0c7694142_1299|d374812a-d1ec-45af-aa12-fd38ad725334|transforma plataformas lo Ãºltimo
107_ecd1b629-fb99-4489-b691-0a50b8d4fd6b|0174c53d-c769-4a63-af45-13e9b404123f_1300|f0c9522d-33a4-468f-84f4-fb0fb8240e7a|what is ubi?
108_c06807c9-7215-421d-be0a-c63b9fc649f3|af64d422-bd04-4063-881b-2aba131535a0_1313|8658cd9d-fe67-4c77-95c0-8b382447b6ea|synthesize bricks-and-clicks communities
110_e47ac7b6-7c69-4d18-9c1c-0a5e12ac5758|b281fb7d-902a-4e76-a0f9-fd52d0f5f27f_1336|55755b6e-b975-4df8-b8e1-bf7bbba9c903|Virtual flexibility systematic
113_82c27283-577b-4a19-9ab7-652ba6635bd5|378dc967-dfa1-49f2-af5e-a8594749aa38_1389|b1e42020-182a-4e86-849d-be132d2f5fae|what is ubi?
