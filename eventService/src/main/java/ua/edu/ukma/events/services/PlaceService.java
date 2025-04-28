package ua.edu.ukma.events.services;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.edu.ukma.events.dto.responses.PlaceResponse;
import ua.edu.ukma.events.entities.Place;
import ua.edu.ukma.events.repositories.EventRepository;
import ua.edu.ukma.events.repositories.PlaceRepository;
import ua.edu.ukma.events.entities.Event;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final EventRepository eventRepository;

    public PlaceService(PlaceRepository placeRepository, EventRepository eventRepository) {
        this.placeRepository = placeRepository;
        this.eventRepository = eventRepository;
    }

    public List<PlaceResponse> listPlacesByEvent(UUID eventId) {
        List<Place> places = placeRepository.findByEvent_EventId(eventId);
    
        List<PlaceResponse> result = places.stream()
          .map(PlaceResponse::new)
          .toList();
        return result;
    }

    public void fillUpPlaces(int rows, int places, UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= places; j++) {
                Place place = Place.builder()
                  .placeId(UUID.randomUUID())
                  .event(event)
                  .row(i)
                  .place(j)
                  .status(Place.PlaceStatus.AVAILABLE)
                  .build();

                placeRepository.save(place);
            }
        }
    }
}
