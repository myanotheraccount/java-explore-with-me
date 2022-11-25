CREATE TABLE IF NOT EXISTS stats
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app       VARCHAR(255),
    ip        VARCHAR(50),
    timestamp TIMESTAMP,
    uri       VARCHAR(255)
);