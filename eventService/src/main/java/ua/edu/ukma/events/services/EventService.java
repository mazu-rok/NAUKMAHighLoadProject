package ua.edu.ukma.events.services;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ua.edu.ukma.events.dto.requests.EventRequest;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;
import ua.edu.ukma.events.repositories.EventRepository;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final CachingEventService cachingEventService;

    @Value("${images-bucket-name}")
    private String imagesBucketName;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    public EventService(EventRepository eventRepository, StorageService storageService, CachingEventService cachingEventService) {
        this.eventRepository = eventRepository;
        this.storageService = storageService;
        this.cachingEventService = cachingEventService;
    }

    private Event convertDtoToEvent(UUID authorId, EventRequest eventRequest) {
        return Event.builder()
        .eventId(UUID.randomUUID())
        .authorId(authorId)
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

    private Event convertDtoToEvent(UUID eventId, UUID authorId, List<Map<String, String>> imagesMetadata, EventRequest eventRequest) {
        return Event.builder()
        .eventId(eventId)
        .authorId(authorId)
        .title(eventRequest.getTitle())
        .description(eventRequest.getDescription())
        .status(eventRequest.getStatus())
        .startTime(eventRequest.getStartTime())
        .endTime(eventRequest.getEndTime())
        .locationAddress(eventRequest.getLocationAddress())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .imagesMetadata(imagesMetadata)
        .build();
    }

    private String getImageStoragePath(UUID eventId, UUID imageId) {
        return "events/" + eventId + "/" + imageId.toString();
    }

    public EventResponse create(Authentication auth, EventRequest eventRequest) {
        UUID authorId = (UUID) auth.getPrincipal();
        return new EventResponse(eventRepository.save(convertDtoToEvent(authorId, eventRequest)));
    }

    public Page<EventResponse> listAll(Optional<Authentication> auth, Integer page, Integer size, String sortBy, String direction, Optional<Set<EventStatus>> statusFilter) {
        boolean isAdmin = false;
        if (auth.isPresent()) {
            isAdmin = auth.get().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }

        if (isAdmin) {
            return cachingEventService.listAllPrivate(page, size, sortBy, direction, statusFilter);
        }

        return cachingEventService.listAllPublic(page, size, sortBy, direction, statusFilter);
    }

    public Optional<EventResponse> getById(UUID id) {
        return cachingEventService.getById(id);
    }

    public Optional<EventResponse> update(UUID id, EventRequest eventRequest) {
        Optional<Event> dbEvent = eventRepository.findById(id);

        if (dbEvent.isPresent()) {
            Event eventToSave = convertDtoToEvent(id, dbEvent.get().getAuthorId(), dbEvent.get().getImagesMetadata(), eventRequest);
            return Optional.of(new EventResponse(eventRepository.save(eventToSave)));
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

        String url = String.format("%s/%s/%s", minioPublicUrl, imagesBucketName, key);

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
