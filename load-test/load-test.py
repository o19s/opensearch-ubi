import json
from locust import HttpUser, between, task

class OpenSearchUBIClient(HttpUser):
    wait_time = between(1, 2)

    @task
    def event_task(self):
        headers = {
            "Content-Type": "application/json"
        }
        data = {
            "type": "instant-search",
            "keywords": "khgkj",
            "timestamp": "1705596607509",
            "url": "file:///C:/jason/_dev/search/OpenSearch/src/search-collector/demo/order.html",
            "ref": "",
            "lang": "en-US",
            "session": "npckcy4",
            "channel": "demo-channel",
            "query": ""
        }

        self.client.post("/_plugins/ubi/mystore", headers=headers, json=data)
