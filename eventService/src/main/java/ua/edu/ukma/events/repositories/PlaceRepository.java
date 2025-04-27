package ua.edu.ukma.events.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.events.entities.Place;

import java.util.List;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
  List<Place> findByEvent_EventId(UUID eventId);
}
