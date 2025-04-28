package ua.edu.ukma.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.orders.entity.Event;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
