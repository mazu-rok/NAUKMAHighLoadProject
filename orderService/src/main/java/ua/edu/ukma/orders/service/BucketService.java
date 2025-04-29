package ua.edu.ukma.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.orders.dto.TicketDto;
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
        var tickets = ticketRepository.findByUserId(userId).stream()
                .map(t -> new TicketDto(t.getEventId(), t.getPlaceId()))
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
            changePlaceStatus(request.eventId(), request.placeId(), PlaceStatus.BOOKED);
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
            changePlaceStatus(request.eventId(), request.placeId(), PlaceStatus.AVAILABLE);
        }

    }

    @Transactional
    public Order buyTickets(UUID userId) {
        var tickets = ticketRepository.findByUserId(userId);
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
        });
        Event event = eventRepository.findById(tickets.getFirst().getEventId()).orElseThrow();
        var order = Order.builder()
                .userId(userId)
                .eventId(event.getEventId())
                .eventName(event.getTitle())
                .tickets(orderedTickets)
                .build();

        order = ordersRepository.save(order);
        ticketRepository.deleteByUserId(userId);
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
