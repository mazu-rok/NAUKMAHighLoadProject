package ua.edu.ukma.events.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {
    @Id
    @Column(name = "place_id", nullable = false, updatable = false)
    private UUID placeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false, updatable = false)
    private Event event;

    @Column(name = "row", nullable = false, updatable = false)
    private Integer row;

    @Column(name = "place", nullable = false, updatable = false)
    private Integer place;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Place.PlaceStatus status;

    public enum PlaceStatus {
        BOOKED,
        ORDERED,
        AVAILABLE
    }
}
