package ua.edu.ukma.orders.service;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ua.edu.ukma.orders.dto.Message;
import ua.edu.ukma.orders.dto.queue.QueuePlaceStatusMessage;
import ua.edu.ukma.orders.entity.PlaceStatus;
import ua.edu.ukma.orders.repository.PlaceRepository;
import ua.edu.ukma.orders.repository.TicketRepository;
import ua.edu.ukma.orders.service.ws.PlacesEventsWsService;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final RabbitTemplate rabbitTemplate;
    private final PlacesEventsWsService placesEventsWsService;
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;

    @Value("${booking.exchange}")
    private String bookingExchange;

    @Value("#{deferredBookingQueue.name}")
    private String deferredBookingQueueName;

    @Value("#{expiredBookingQueue.name}")
    private String expiredBookingQueueName;

    @Value("${booking.routing-key}")
    private String bookingRoutingKey;

    @Value("${booking.expired.routing-key}")
    private String expiredBookingRoutingKey;


    // Sends messages about booking to rabbitmq exchange
    public void sendBookingMessage(QueuePlaceStatusMessage placeStatusMessage) {
        rabbitTemplate.convertAndSend(bookingExchange, bookingRoutingKey, placeStatusMessage);
        log.info("Sent booking message: {}", placeStatusMessage);
    }

    // Receive messages about booking, from server specific queue
    @RabbitListener(queues = "#{deferredBookingQueue.name}")
    public void readDeferredBooking(QueuePlaceStatusMessage placeStatusMessage) {
        log.info("Received booking: {}", placeStatusMessage.toString());
        try {
            placesEventsWsService.broadcastForEvent(placeStatusMessage.getEventId().toString(),
                    Message.builder()
                            .placeId(placeStatusMessage.getPlaceId())
                            .status(placeStatusMessage.getPlaceStatus())
                            .build());
        } catch (IOException e) {
            log.error("Failed to broadcast message: ", e);
        }
    }

    // Receive messages when the booking is expired to update in db
    // It should do only one server
    @RabbitListener(queues = "${booking.dead.queue}")
    public void handleExpiredBooking(QueuePlaceStatusMessage placeStatusMessage) {
        log.info("Updating expired booking in db: {}", placeStatusMessage.toString());
        // update status in db
        if (placeRepository.findById(placeStatusMessage.getPlaceId()).orElseThrow().getStatus() != PlaceStatus.ORDERED) {
            placeRepository.updateStatusByEventIdAndPlaceId(placeStatusMessage.getEventId(), placeStatusMessage.getPlaceId(), PlaceStatus.AVAILABLE);
            ticketRepository.deleteByEventIdAndPlaceId(placeStatusMessage.getEventId(), placeStatusMessage.getPlaceId());
            placeStatusMessage.setPlaceStatus(PlaceStatus.AVAILABLE);

            // Sending message that booking status was updated
            rabbitTemplate.convertAndSend(bookingExchange, expiredBookingRoutingKey, placeStatusMessage);
        }

    }

    // Receive messages when the booking is expired and its status was already updated
    // To read its status from db
    // It will do all servers
    @RabbitListener(queues = "#{expiredBookingQueue.name}")
    public void readExpiredBooking(QueuePlaceStatusMessage placeStatusMessage) {
        log.warn("Reading expired booking status from db: " + placeStatusMessage.toString());
        try {
            placesEventsWsService.broadcastForEvent(placeStatusMessage.getEventId().toString(),
                    Message.builder()
                            .placeId(placeStatusMessage.getPlaceId())
                            .status(placeStatusMessage.getPlaceStatus())
                            .build());
        } catch (IOException e) {
            log.error("Failed to broadcast message: ", e);
        }
    }

//    // Test that booking queues are working
//    @EventListener(ApplicationReadyEvent.class)
//    public void sendTestBookings() {
//        for (int i = 0; i < 10; i++) {
//            QueuePlaceStatusMessage placeStatusMessage = new QueuePlaceStatusMessage(UUID.randomUUID(), UUID.randomUUID(), PlaceStatus.BOOKED);
//            sendBookingMessage(placeStatusMessage);
//        }
//    }
}
