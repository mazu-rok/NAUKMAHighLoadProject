package ua.edu.ukma.events.dto.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.events.entities.Place;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    private UUID placeId;
    private UUID eventId;
    private Number row;
    private Number place;
    private Place.PlaceStatus status;

    public PlaceResponse (Place place) {
        this.placeId = place.getPlaceId();
        this.eventId = place.getEvent().getEventId();
        this.row = place.getRow();
        this.place = place.getPlace();
        this.status = place.getStatus();
    }
}
