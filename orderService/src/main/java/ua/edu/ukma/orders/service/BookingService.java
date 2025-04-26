package ua.edu.ukma.orders.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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
    public void sendBookingMessage(String bookingId) {
        rabbitTemplate.convertAndSend(bookingExchange, bookingRoutingKey, bookingId);
        log.warn("Sent booking message for ID: " + bookingId);
    }

    // Receive messages about booking, from server specific queue
    @RabbitListener(queues = "#{deferredBookingQueue.name}")
    public void readDeferredBooking(String bookingId) {
        log.warn("Received booking: " + bookingId);
    }

    // Receive messages when the booking is expired to update in db
    // It should do only one server
    @RabbitListener(queues = "${booking.dead.queue}")
    public void handleExpiredBooking(String bookingId) {
        log.warn("Updating expired booking in db: " + bookingId);

        // Sending message that booking status was updated
        rabbitTemplate.convertAndSend(bookingExchange, expiredBookingRoutingKey, bookingId);
    }

    // Receive messages when the booking is expired and its status was already updated
    // To read its status from db
    // It will do all servers
    @RabbitListener(queues = "#{expiredBookingQueue.name}")
    public void readExpiredBooking(String bookingId) {
        log.warn("Reading expired booking status from db: " + bookingId);
    }

    // Test that booking queues are working
    @EventListener(ApplicationReadyEvent.class)
    public void sendTestBookings() {
        for (int i = 0; i < 10; i++) {
            String bookingId = UUID.randomUUID().toString();
            sendBookingMessage(bookingId);
        }
    }
}
