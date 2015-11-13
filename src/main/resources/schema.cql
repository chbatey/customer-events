CREATE KEYSPACE events WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };
CREATE TABLE test.events (
    customerid text,
    when timeuuid,
    staffid text,
    type text,
    PRIMARY KEY (customerid, when)
);
