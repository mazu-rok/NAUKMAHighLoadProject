package ua.edu.ukma.events.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ua.edu.ukma.events.entities.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByAuthorId(UUID authorId);
}
