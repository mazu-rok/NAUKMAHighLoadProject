CREATE TABLE tickets
(
    id       UUID NOT NULL PRIMARY KEY,
    user_id  UUID NOT NULL,
    event_id UUID NOT NULL,
    place_id UUID NOT NULL,
    UNIQUE (event_id, place_id)
);