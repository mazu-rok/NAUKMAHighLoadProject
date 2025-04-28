package ua.edu.ukma.orders.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ua.edu.ukma.orders.dto.queue.QueuePlaceStatusMessage;
import ua.edu.ukma.orders.dto.queue.QueuePlaceStatusMessage.PlaceStatus;

@Service
@Slf4j
public class BookingService {

    private final RabbitTemplate rabbitTemplate;

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

    public BookingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Sends messages about booking to rabbitmq exchange
    public void sendBookingMessage(QueuePlaceStatusMessage placeStatusMessage) {
        rabbitTemplate.convertAndSend(bookingExchange, bookingRoutingKey, placeStatusMessage);
        log.warn("Sent booking message: " + placeStatusMessage.toString());
    }

    // Receive messages about booking, from server specific queue
    @RabbitListener(queues = "#{deferredBookingQueue.name}")
    public void readDeferredBooking(QueuePlaceStatusMessage placeStatusMessage) {
        log.warn("Received booking: " + placeStatusMessage.toString());
    }

    // Receive messages when the booking is expired to update in db
    // It should do only one server
    @RabbitListener(queues = "${booking.dead.queue}")
    public void handleExpiredBooking(QueuePlaceStatusMessage placeStatusMessage) {
        log.warn("Updating expired booking in db: " + placeStatusMessage.toString());

        placeStatusMessage.setPlaceStatus(PlaceStatus.AVAILABLE);

        // Sending message that booking status was updated
        rabbitTemplate.convertAndSend(bookingExchange, expiredBookingRoutingKey, placeStatusMessage);
    }

    // Receive messages when the booking is expired and its status was already updated
    // To read its status from db
    // It will do all servers
    @RabbitListener(queues = "#{expiredBookingQueue.name}")
    public void readExpiredBooking(QueuePlaceStatusMessage placeStatusMessage) {
        log.warn("Reading expired booking status from db: " + placeStatusMessage.toString());
    }

    // Test that booking queues are working
    @EventListener(ApplicationReadyEvent.class)
    public void sendTestBookings() {
        for (int i = 0; i < 10; i++) {
            QueuePlaceStatusMessage placeStatusMessage = new QueuePlaceStatusMessage(UUID.randomUUID(), UUID.randomUUID(), PlaceStatus.BOOKED);
            sendBookingMessage(placeStatusMessage);
        }
    }
}
