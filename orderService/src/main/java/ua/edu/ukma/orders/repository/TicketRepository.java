package ua.edu.ukma.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.orders.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByUserId(UUID userId);

    int deleteByUserIdAndEventIdAndPlaceId(UUID userId, UUID eventId, UUID placeId);
}
