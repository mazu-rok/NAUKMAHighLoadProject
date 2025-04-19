package ua.edu.ukma.events.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.entities.Event;

@Component("eventAuth")
public class EventAuthorizationService {

    private final EventService eventService;
    
    public EventAuthorizationService(EventService eventService) {
        this.eventService = eventService;
    }

    public boolean hasPublicStatus(UUID eventId) {
        Optional<EventResponse> eventResponse = eventService.getById(eventId);

        if (eventResponse.isPresent()) {
            Event.EventStatus status = eventResponse.get().getStatus();
            return status == Event.EventStatus.SCHEDULED || status == Event.EventStatus.ENDED;
        }

        return false;
    }

    public boolean hasDraftStatus(UUID eventId) {
        Optional<EventResponse> eventResponse = eventService.getById(eventId);

        if (eventResponse.isPresent()) {
            Event.EventStatus status = eventResponse.get().getStatus();
            return status == Event.EventStatus.DRAFT;
        }

        return false;
    }
    
}
