package ua.edu.ukma.events.dto.responses;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;

@Data
public class EventResponse {
    private UUID eventId;
    private UUID authorId;
    private String title;
    private String description;
    private EventStatus status;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String locationAddress;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        this.authorId = event.getAuthorId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.status = event.getStatus();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.locationAddress = event.getLocationAddress();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
    }
}
