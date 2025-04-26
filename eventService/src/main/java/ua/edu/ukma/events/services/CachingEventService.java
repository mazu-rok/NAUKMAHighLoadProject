package ua.edu.ukma.events.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;
import ua.edu.ukma.events.repositories.EventRepository;
import ua.edu.ukma.events.utils.RestResponsePage;

@Service
@Slf4j
public class CachingEventService {
    private final EventRepository eventRepository;

    public CachingEventService(EventRepository eventRepository, StorageService storageService) {
        this.eventRepository = eventRepository;
    }

    /** Returns list of public events (with SCHEDULED or ENDED statuses) */
    @Cacheable(value="eventListPublic")
    public RestResponsePage<EventResponse> listAllPublic(Integer page, Integer size, String sortBy, String direction, Optional<Set<EventStatus>> statusFilter) {
        log.info("[CACHE MISS] Fetching events from db.");
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EventResponse> result;
        if (statusFilter.isPresent()) {
            Set<EventStatus> publicStatusFilter = statusFilter.get();
            publicStatusFilter.remove(Event.EventStatus.DRAFT);
            result = eventRepository.findByStatusIn(statusFilter.get(), pageable).map(e -> new EventResponse(e));
        } else {
            result = eventRepository.findByStatusIn(Set.of(Event.EventStatus.SCHEDULED, Event.EventStatus.ENDED), pageable).map(e -> new EventResponse(e));
        }

        return new RestResponsePage<>(result.getContent(), result.getPageable(), result.getTotalElements());
    }

    /** Returns list of all events if user has role Admin */
    @Cacheable(value="eventListPrivate")
    public RestResponsePage<EventResponse> listAllPrivate(Integer page, Integer size, String sortBy, String direction, Optional<Set<EventStatus>> statusFilter) {
        log.info("[CACHE MISS] Fetching events from db.");
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EventResponse> result;
        if (statusFilter.isPresent()) {
            result = eventRepository.findByStatusIn(statusFilter.get(), pageable).map(e -> new EventResponse(e));
        } else {
            result = eventRepository.findAll(pageable).map(e -> new EventResponse(e));
        }

        return new RestResponsePage<>(result.getContent(), result.getPageable(), result.getTotalElements());
    }

    @Cacheable(value="event")
    public Optional<EventResponse> getById(UUID id) {
        log.info("[CACHE MISS] Fetching event from db.");
        Optional<Event> event = eventRepository.findById(id);

        if (event.isPresent()) {
            return Optional.of(new EventResponse(event.get()));
        }

        return Optional.empty();
    }
}
