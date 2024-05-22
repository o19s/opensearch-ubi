#!/bin/bash -e

curl -s http://localhost:9200/ecommerce/_search -H "Content-Type: application/json" -d'
 {
  "ext": {
   "ubi": {
     "query_id": "12300d16cb-b6f1-4012-93ebcc49cac90426",
     "user_query": "toner",
     "query_attributes": {
       "experiment": "exp_1",
       "system": "my_system"
     }
    }
   },
   "query": {
     "match": {
       "name": "toner"
     }
   }
 }' | jq
