USE
kcd;

CREATE TABLE user_types
(
    user text,
    type text,
    PRIMARY KEY (user, type)
);