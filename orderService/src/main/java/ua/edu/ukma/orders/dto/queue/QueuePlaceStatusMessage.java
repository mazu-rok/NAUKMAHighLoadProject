package ua.edu.ukma.orders.dto.queue;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuePlaceStatusMessage {
    private UUID eventId;
    private UUID placeId;
    private PlaceStatus placeStatus;

    public enum PlaceStatus {
        BOOKED,
        ORDERED,
        AVAILABLE
    }
}
