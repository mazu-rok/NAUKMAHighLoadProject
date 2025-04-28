package ua.edu.ukma.orders.configs;

import java.util.UUID;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingQueueConfig {
    @Value("${booking.exchange}")
    private String bookingExchangeName;

    @Value("${booking.routing-key}")
    private String bookingRoutingKey;

    @Value("${booking.expired.routing-key}")
    private String expiredBookingRoutingKey;

    @Value("${booking.ttl}")
    private int bookingTTL;

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(bookingExchangeName);
    }

    @Bean
    public Queue deferredBookingQueue() {
        // Declare temporary queue just for this server
        // To not remove booking messages from the main queue, so they would expire
        String queueName = "booking.defer." + UUID.randomUUID();
        return QueueBuilder.nonDurable(queueName).exclusive().autoDelete().withArgument("x-message-ttl", bookingTTL).build();
    }

    @Bean
    public Queue expiredBookingQueue() {
        // Declare temporary queue just for this server
        // To read statuses of expired bookings from db
        String queueName = "booking.expired." + UUID.randomUUID();
        return QueueBuilder.nonDurable(queueName).exclusive().autoDelete().build();
    }

    @Bean
    public Binding bindDeferred(Queue deferredBookingQueue, DirectExchange bookingExchange) {
        return BindingBuilder
            .bind(deferredBookingQueue)
            .to(bookingExchange)
            .with(bookingRoutingKey);
    }

    @Bean
    public Binding bindExpired(Queue expiredBookingQueue, DirectExchange bookingExchange) {
        return BindingBuilder
            .bind(expiredBookingQueue)
            .to(bookingExchange)
            .with(expiredBookingRoutingKey);
    }
}
