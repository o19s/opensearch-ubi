# OpenSearch UBI Plugin Load Test

The `load-test` directory contains a basic load testing example. The purpose of the files under `load-test` are to provide a means of testing the plugin's ability to receive and store a large number of events over time. To use the load test, first start OpenSearch on `localhost:9200`, and then:

```
cd load-test
source ./venv/bin/activate
./run.sh
```

The test will run for 10 seconds. The number of events sent will be shown along with the `_count` of events in the store:

```
Type     Name                                                                          # reqs      # fails |    Avg     Min     Max    Med |   req/s  failures/s
--------|----------------------------------------------------------------------------|-------|-------------|-------|-------|-------|-------|--------|-----------
POST     /_plugins/ubi/mystore                                                              8     0(0.00%) |      8       6       9      8 |    0.81        0.00
--------|----------------------------------------------------------------------------|-------|-------------|-------|-------|-------|-------|--------|-----------
         Aggregated                                                                         8     0(0.00%) |      8       6       9      8 |    0.81        0.00

Found 8 indexed
```

This shows 8 total requests made by locust, and 8 events are in the index. The idea being we can assert that the number of events sent matches the events stored in the index.
