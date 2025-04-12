package ua.edu.ukma.events.dto.requests;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;
import ua.edu.ukma.events.entities.Event.EventStatus;

@Data
public class EventRequest {
    private UUID authorId;
    private String title;
    private String description;
    private EventStatus status;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String locationAddress;
}
