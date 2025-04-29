package ua.edu.ukma.orders.dto.queue;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.orders.entity.PlaceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuePlaceStatusMessage {
    private UUID eventId;
    private UUID placeId;
    private PlaceStatus placeStatus;

}
