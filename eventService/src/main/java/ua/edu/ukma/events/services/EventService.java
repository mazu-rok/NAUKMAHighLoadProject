package ua.edu.ukma.events.services;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import ua.edu.ukma.events.dto.requests.EventRequest;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;
import ua.edu.ukma.events.repositories.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final StorageService storageService;

    @Value("${images-bucket-name}")
    private String imagesBucketName;

    public EventService(EventRepository eventRepository, StorageService storageService) {
        this.eventRepository = eventRepository;
        this.storageService = storageService;
    }

    private Event convertDtoToEvent(EventRequest eventRequest) {
        return Event.builder()
        .eventId(UUID.randomUUID())
        .authorId(eventRequest.getAuthorId())
        .title(eventRequest.getTitle())
        .description(eventRequest.getDescription())
        .status(eventRequest.getStatus())
        .startTime(eventRequest.getStartTime())
        .endTime(eventRequest.getEndTime())
        .locationAddress(eventRequest.getLocationAddress())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .imagesMetadata(List.of())
        .build();
    }

    private Event convertDtoToEvent(UUID eventId, EventRequest eventRequest) {
        return Event.builder()
        .eventId(eventId)
        .authorId(eventRequest.getAuthorId())
        .title(eventRequest.getTitle())
        .description(eventRequest.getDescription())
        .status(eventRequest.getStatus())
        .startTime(eventRequest.getStartTime())
        .endTime(eventRequest.getEndTime())
        .locationAddress(eventRequest.getLocationAddress())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .imagesMetadata(List.of())
        .build();
    }

    private String getImageStoragePath(UUID eventId, UUID imageId) {
        return "events/" + eventId + "/" + imageId.toString();
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

    public Optional<InputStream> getImage(UUID eventId, UUID imageId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();

        List<Map<String, String>> imagesMetadata = event.getImagesMetadata();
        for (Map<String, String> imageMetadata : imagesMetadata) {
            if (UUID.fromString(imageMetadata.get("image_id")).equals(imageId)) {
                String key = getImageStoragePath(eventId, imageId);
                return Optional.of(storageService.downloadFile(imagesBucketName, key));
            }

        }

        return Optional.empty();
    }

    public Optional<EventResponse> addImage(UUID eventId, MultipartFile file) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();

        UUID imageId = UUID.randomUUID();
        String key = getImageStoragePath(eventId, imageId);

        String url = String.format("https://test.com/%s/%s", imagesBucketName, key);

        List<Map<String, String>> imagesMetadata = event.getImagesMetadata();
        imagesMetadata.add(Map.ofEntries(Map.entry("image_id", imageId.toString()), Map.entry("url", url)));
        event.setImagesMetadata(imagesMetadata);

        storageService.uploadFile(imagesBucketName, key, file);

        return Optional.of(new EventResponse(eventRepository.save(event)));
    }

    public Optional<EventResponse> removeImage(UUID eventId, UUID imageId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();

        List<Map<String, String>> imagesMetadata = event.getImagesMetadata();
        for (Map<String, String> imageMetadata : imagesMetadata) {
            if (UUID.fromString(imageMetadata.get("image_id")).equals(imageId)) {
                String key = getImageStoragePath(eventId, imageId);
                storageService.deleteFile(imagesBucketName, key);
            }

        }

        imagesMetadata.removeIf(map -> imageId.equals(UUID.fromString(map.get("image_id"))));

        return Optional.of(new EventResponse(eventRepository.save(event)));
    }

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Transactional
    public void updateEventsStatuses() {
        eventRepository.updateStatusForEndedEvents();
    }
}
