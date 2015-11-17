CREATE KEYSPACE events WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };
CREATE TABLE test.events (
    customerId text,
    when timeuuid,
    id text,
    eventType text,
    staffid text,
    PRIMARY KEY (customerid, when, id)
);
