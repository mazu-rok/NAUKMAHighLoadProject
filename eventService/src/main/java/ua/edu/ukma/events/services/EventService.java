package ua.edu.ukma.events.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ua.edu.ukma.events.dto.requests.EventRequest;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;
import ua.edu.ukma.events.repositories.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    private Event convertDtoToEvent(EventRequest eventRequest) {
        return new Event(
            eventRequest.getAuthorId(),
            eventRequest.getTitle(),
            eventRequest.getDescription(),
            eventRequest.getStatus(),
            eventRequest.getStartTime(),
            eventRequest.getEndTime(),
            eventRequest.getLocationAddress()
            );
    }

    private Event convertDtoToEvent(UUID eventId, EventRequest eventRequest) {
        return new Event(
            eventId,
            eventRequest.getAuthorId(),
            eventRequest.getTitle(),
            eventRequest.getDescription(),
            eventRequest.getStatus(),
            eventRequest.getStartTime(),
            eventRequest.getEndTime(),
            eventRequest.getLocationAddress()
            );
    }

    public EventResponse create(EventRequest eventRequest) {
        return new EventResponse(eventRepository.save(convertDtoToEvent(eventRequest)));
    }

    public Page<EventResponse> listAll(Integer page, Integer size, String sortBy, String direction, Optional<EventStatus> statusFilter) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (statusFilter.isPresent()) {
            return eventRepository.findByStatus(statusFilter.get(), pageable).map(e -> new EventResponse(e));
        }
        return eventRepository.findAll(pageable).map(e -> new EventResponse(e));
    }

    public Optional<EventResponse> getById(UUID id) {
        Optional<Event> event = eventRepository.findById(id);

        if (event.isPresent()) {
            return Optional.of(new EventResponse(event.get()));
        }

        return Optional.empty();
    }

    public Optional<EventResponse> update(UUID id, EventRequest eventRequest) {
        if (eventRepository.existsById(id)) {
            return Optional.of(new EventResponse(eventRepository.save(convertDtoToEvent(id, eventRequest))));
        }

        return Optional.empty();
    }

    public void delete(UUID id) {
        eventRepository.deleteById(id);
    }

    public Optional<EventResponse> addImage(UUID eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();

        UUID imageId = UUID.randomUUID();
        String key = "events/" + eventId + "/" + imageId.toString();

        String url = String.format("https://test.com/%s", key);

        List<Map<String, String>> imagesMetadata = event.getImagesMetadata();
        imagesMetadata.add(Map.ofEntries(Map.entry("image_id", imageId.toString()), Map.entry("url", url)));
        event.setImagesMetadata(imagesMetadata);

        return Optional.of(new EventResponse(eventRepository.save(event)));
    }

    public Optional<EventResponse> removeImage(UUID eventId, UUID image_id) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();

        List<Map<String, String>> imagesMetadata = event.getImagesMetadata();
        imagesMetadata.removeIf(map -> image_id.equals(UUID.fromString(map.get("image_id"))));

        return Optional.of(new EventResponse(eventRepository.save(event)));
    }

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Transactional
    public void updateEventsStatuses() {
        eventRepository.updateStatusForEndedEvents();
    }
}
