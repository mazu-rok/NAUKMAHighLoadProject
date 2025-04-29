package ua.edu.ukma.orders.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.orders.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByUserId(UUID userId);
    @Transactional
    int deleteByUserId(UUID userId);
    @Transactional
    int deleteByEventIdAndPlaceId(UUID eventId, UUID placeId);

    int deleteByUserIdAndEventIdAndPlaceId(UUID userId, UUID eventId, UUID placeId);
}
