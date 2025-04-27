CREATE TABLE places (
    place_id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    row INT NOT NULL,
    place INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (event_id)
);