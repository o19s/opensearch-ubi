# Getting Started with Ubi!

[log_events.zip](sample-data/log_events.zip) - Contains syntetic user event data

## Initializing Store
Initializing the store `ubi_log` creates 2 hidden indices in the background: `.ubi_log_events` and `.ubi_log_queries`.  
1) Using `curl`:
```
curl -X PUT "http://localhost:9200/_plugins/ubi/ubi_log?index=ecommerce&id_field=name"
```
- For the syntheic data purposes, only, the `index` and `id_field` are irrelevant, but in the general case `index` tells the Ubi Plugin to log queries off that index and `id_field` within that index.

2) Run the python script to upload the synthetic data:
```python
python index_sample_data.py
```

The successful output should look something like the following:
```
...

Indexing rows in ./sample_data/log_events.zip/log_events.json
* Uploaded 24064 rows to .ubi_log_events
* Uploaded 2395 rows to .ubi_log_queries
Done! Indexed 26459 rows.
```

3) From there, you should be able to execute the queries in [sql_queries.md](./queries/sql_queries.md) in the Query Workbench.