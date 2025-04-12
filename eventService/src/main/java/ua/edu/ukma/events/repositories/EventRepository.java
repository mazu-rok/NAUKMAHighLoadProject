package ua.edu.ukma.events.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import ua.edu.ukma.events.entities.Event;
import ua.edu.ukma.events.entities.Event.EventStatus;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findByAuthorId(UUID authorId, Pageable pageable);
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Event e SET e.status = 'ENDED' WHERE e.endTime < CURRENT_TIMESTAMP AND e.status = 'SCHEDULED'")
    int updateStatusForEndedEvents();
}
