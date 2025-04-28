package ua.edu.ukma.orders.dto.response;

import ua.edu.ukma.orders.dto.TicketDto;

import java.util.List;
import java.util.UUID;

public record BucketResponse(UUID userId, List<TicketDto> tickets) {
}
