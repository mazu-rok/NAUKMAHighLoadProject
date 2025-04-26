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

    @Value("${booking.routing-key}")
    private String bookingRoutingKey;

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
    public void handleDeferredBooking(String bookingId) {
        log.warn("Received booking: " + bookingId);
    }

    // Receive messages when the booking is expired
    @RabbitListener(queues = "${booking.dead.queue}")
    public void handleExpiredBooking(String bookingId) {
        log.warn("Booking expired: " + bookingId);
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
