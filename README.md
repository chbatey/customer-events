Todo:

Write side:
* Write a custom receiver to take customer events over http
* Save to Cassandra
* Write a counter based aggregation
* Write w window based aggregation

Read side:
* Direct cassandra access: last N events for customer T


General:
* Add a gatling test to generate load for demo

Tech todo:
* Introduce Cassandra unit so the tests don't rely on a running Cassandra
* Load test with Gatling
