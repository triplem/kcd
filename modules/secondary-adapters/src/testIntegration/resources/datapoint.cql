USE kcd;

CREATE TABLE observation
(
    user       text,
    id         timeuuid,
    date       timestamp,
    type       text, // an enum really?
    createdAt  timestamp,
    modifiedAt timestamp,
    data       text,
    PRIMARY KEY (user, id)
);
-- one user, measure every 5min (24*60/5) 288 per day
-- *2 (basal changes, bolus, ...)
-- 500 per day
-- 15000 per month

CREATE MATERIALIZED VIEW observation_by_type AS
SELECT user, type, id, date, createdAt, modifiedAt, data
FROM observation
WHERE user IS NOT NULL
  AND type IS NOT NULL
  AND id IS NOT NULL
PRIMARY KEY ((user, type), id);
-- select distinct type from observation_by_type where user = "" and date > '' and date < ''



