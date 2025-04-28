package ua.edu.ukma.orders.repository;



import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.orders.entity.Place;
import ua.edu.ukma.orders.entity.PlaceStatus;

import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
  @Transactional
  @Modifying
  @Query("UPDATE Place p SET p.status = :newStatus WHERE p.eventId = :eventId AND p.placeId = :placeId")
  int updateStatusByEventIdAndPlaceId(UUID eventId, UUID placeId, PlaceStatus newStatus);
}
