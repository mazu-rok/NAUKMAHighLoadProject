package ua.edu.ukma.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.orders.dto.TicketDto;
import ua.edu.ukma.orders.dto.TicketsDto;
import ua.edu.ukma.orders.dto.queue.QueuePlaceStatusMessage;
import ua.edu.ukma.orders.dto.response.BucketResponse;
import ua.edu.ukma.orders.entity.*;
import ua.edu.ukma.orders.exception.PlaceAlreadyBookedException;
import ua.edu.ukma.orders.repository.EventRepository;
import ua.edu.ukma.orders.repository.OrdersRepository;
import ua.edu.ukma.orders.repository.PlaceRepository;
import ua.edu.ukma.orders.repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketService {
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;
    private final EventRepository eventRepository;
    private final OrdersRepository ordersRepository;
    private final BookingService bookingService;

    public BucketResponse getBucket(UUID userId) {
        var tickets = ticketRepository.findByUserIdAndOrderedFalse(userId).stream()
                .map(t -> new TicketDto(t.getEventId(), t.getPlaceId()))
                .toList();
        return new BucketResponse(userId, tickets);
    }

    @Transactional(rollbackOn = Exception.class)
    public void addTickets(UUID userId, TicketsDto request) {
        log.info("Booking tickets: {} for event: {}", request.placeIds(), request.eventId());
        var tickets = request.placeIds().stream().map(placeId -> new Ticket(
                UUID.randomUUID(),
                userId,
                request.eventId(),
                placeId,
                false
        )).toList();
        try {
            ticketRepository.saveAll(tickets);
            ticketRepository.flush();
            tickets.forEach(t -> {
                changePlaceStatus(request.eventId(), t.getPlaceId(), PlaceStatus.BOOKED);
            });
        } catch (Exception ex) {
            log.error("Failed to book ticket: ", ex);
            if (ex.getMessage().contains("duplicate key value")) {
                throw new PlaceAlreadyBookedException(ex.getMessage());
            }
            throw new RuntimeException("Failed to book ticket");
        }
        log.info("Booked tickets: {} for event: {}", request.placeIds(), request.eventId());
    }

    @Transactional
    public void removeTicket(UUID userId, TicketDto request) throws Exception {
        if (ticketRepository.deleteByUserIdAndEventIdAndPlaceIdAndOrderedFalse(userId, request.eventId(), request.placeId()) > 0) {
            changePlaceStatus(request.eventId(), request.placeId(), PlaceStatus.AVAILABLE);
            log.info("Removed ticket: {} for event: {} from bucket", request.placeId(), request.eventId());
        }
    }

    public Order buyTickets(UUID userId) {
        var tickets = ticketRepository.findByUserIdAndOrderedFalse(userId);
        if (tickets.isEmpty()) {
            return null;
        }
        List<OrderedTicket> orderedTickets = new ArrayList<>();

        tickets.forEach(t -> {
            changePlaceStatus(t.getEventId(), t.getPlaceId(), PlaceStatus.ORDERED);
            Place place = placeRepository.findById(t.getPlaceId()).orElseThrow();
            orderedTickets.add(OrderedTicket.builder()
                    .placeId(t.getPlaceId())
                    .row(place.getRow())
                    .place(place.getPlace())
                    .build());
            t.setOrdered(true);
        });
        Event event = eventRepository.findById(tickets.getFirst().getEventId()).orElseThrow();
        var order = Order.builder()
                .userId(userId)
                .eventId(event.getEventId())
                .eventName(event.getTitle())
                .tickets(orderedTickets)
                .build();

        order = ordersRepository.save(order);
        ticketRepository.saveAll(tickets);
        log.info("Order completed: {}", order);
        return order;
    }

    public List<Order> getOrders(UUID userId) {
        return ordersRepository.findByUserId(userId);
    }

    private void changePlaceStatus(UUID eventId, UUID placeId, PlaceStatus status) {
        placeRepository.updateStatusByEventIdAndPlaceId(eventId, placeId, status);
        bookingService.sendBookingMessage(new QueuePlaceStatusMessage(eventId, placeId, status));
    }
}
