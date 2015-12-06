CREATE KEYSPACE events WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE test.events (
    customerId text,
    when timeuuid,
    id text,
    event_type text,
    staffid text,
    PRIMARY KEY (customerid, when, id)
);

CREATE TABLE test.event_counts (
    event_type text,
    count counter,
    PRIMARY KEY (event_type)
);

CREATE TABLE test.running_count (
    event_type text,
    count int,
    PRIMARY KEY (event_type)
);

CREATE TABLE test.sliding_count (
    event_type text,
    count int,
    PRIMARY KEY (event_type)
);
