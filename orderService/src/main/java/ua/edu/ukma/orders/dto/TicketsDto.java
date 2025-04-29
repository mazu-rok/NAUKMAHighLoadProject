package ua.edu.ukma.orders.dto;

import java.util.List;
import java.util.UUID;

public record TicketsDto(UUID eventId, List<UUID> placeIds) {
}
