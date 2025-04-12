package ua.edu.ukma.events.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Event {
    public Event(UUID eventId, UUID authorId, String title, String description, EventStatus status, OffsetDateTime startTime, OffsetDateTime endTime, String locationAddress) {
        this.eventId = eventId;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationAddress = locationAddress;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.imagesMetadata = List.of();
    }

    public Event(UUID authorId, String title, String description, EventStatus status, OffsetDateTime startTime, OffsetDateTime endTime, String locationAddress) {
        this.eventId = UUID.randomUUID();
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationAddress = locationAddress;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.imagesMetadata = List.of();
    }

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "author_id", nullable = false, updatable = false)
    private UUID authorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = true, unique = false)
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "location_address", length = 200)
    private String locationAddress;

    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, nullable = false)
    private OffsetDateTime updatedAt;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images_metadata", columnDefinition = "jsonb")
    private List<Map<String, String>> imagesMetadata;

    public enum EventStatus {
        DRAFT,
        PUBLISHED,
        CANCELLED
    }
}
