package ua.edu.ukma.events.dto.responses;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;

import io.hypersistence.utils.hibernate.type.util.ObjectMapperWrapper.OffsetDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private UUID eventId;
    private UUID authorId;
    private String title;
    private String description;
    private EventStatus status;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime startTime;
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime endTime;

    private String locationAddress;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime createdAt;
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime updatedAt;

    private List<Map<String, String>> imagesMetadata;

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
        this.imagesMetadata = event.getImagesMetadata();
    }
}
