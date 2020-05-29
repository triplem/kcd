USE
kcd;

CREATE TABLE observation
(
    id         uuid,
    user       text,
    type       text, /
    /
    an
    enum
    really
    ?
    date
    timestamp,
    createdAt  timestamp,
    modifiedAt timestamp,
    data       text,
    PRIMARY KEY (id, user, type, date)
);

CREATE
MATERIALIZED VIEW observation_by_date AS
SELECT user, date, type, id, data, createdAt, modifiedAt
FROM observation
WHERE user IS NOT NULL
  AND date IS NOT NULL
  AND type IS NOT NULL
  AND id IS NOT NULL
    PRIMARY KEY (user
    , date
    , type
    , id);

CREATE
MATERIALIZED VIEW observation_by_type AS
SELECT user, type, date, id, data, createdAt, modifiedAt
FROM observation
WHERE user IS NOT NULL
  AND date IS NOT NULL
  AND type IS NOT NULL
  AND id IS NOT NULL
    PRIMARY KEY (user
    , type
    , date
    , id);
