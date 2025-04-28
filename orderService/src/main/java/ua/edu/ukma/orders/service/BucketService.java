package ua.edu.ukma.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.orders.dto.Message;
import ua.edu.ukma.orders.dto.TicketDto;
import ua.edu.ukma.orders.dto.response.BucketResponse;
import ua.edu.ukma.orders.entity.Ticket;
import ua.edu.ukma.orders.exception.PlaceAlreadyBookedException;
import ua.edu.ukma.orders.repository.TicketRepository;
import ua.edu.ukma.orders.service.ws.PlacesEventsWsService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketService {
    private final TicketRepository ticketRepository;
    private final PlacesEventsWsService placesEventsWsService;

    public BucketResponse getBucket(UUID userId) {
        var tickets =  ticketRepository.findByUserId(userId).stream()
                .map(t-> new TicketDto(t.getEventId(), t.getPlaceId()))
                .toList();
        return new BucketResponse(userId, tickets);
    }

    public void addTicket(UUID userId, TicketDto request) {
        var ticket = new Ticket(
                UUID.randomUUID(),
                userId,
                request.eventId(),
                request.placeId()
        );
        try {
            ticketRepository.save(ticket);
            placesEventsWsService.broadcastForEvent(request.eventId().toString(),
                    Message.builder()
                            .status(Message.MessageStatus.BOOKED)
                            .placeId(request.placeId())
                            .build());
        } catch (Exception ex) {
            log.error("Failed to book ticket: ", ex);
            if (ex.getMessage().contains("duplicate key value")) {
                throw new PlaceAlreadyBookedException(ex.getMessage());
            }
            throw new RuntimeException("Failed to book ticket");
        }
    }

    @Transactional
    public void removeTicket(UUID userId, TicketDto request) throws Exception {
        if (ticketRepository.deleteByUserIdAndEventIdAndPlaceId(userId, request.eventId(), request.placeId()) > 0) {
            placesEventsWsService.broadcastForEvent(request.eventId().toString(),
                    Message.builder()
                            .status(Message.MessageStatus.AVAILABLE)
                            .placeId(request.placeId())
                            .build());
        }

    }
}
