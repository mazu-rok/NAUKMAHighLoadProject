package ua.edu.ukma.events.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.edu.ukma.events.dto.responses.PlaceResponse;

import ua.edu.ukma.events.entities.Place;
import ua.edu.ukma.events.repositories.PlaceRepository;
import ua.edu.ukma.events.utils.RestResponsePage;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CachingPlaceService {
    private final PlaceRepository placeRepository;

    public CachingPlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Cacheable(value="placeListPublic")
    public List<PlaceResponse> listAllPlacesByEventId(UUID eventId) {
        log.info("[CACHE MISS] Fetching places for eventId={} from DB.", eventId);

        List<Place> places = placeRepository.findByEvent_EventId(eventId);

        List<PlaceResponse> result = places.stream()
          .map(PlaceResponse::new)
          .toList();

        return result;
    }
}
