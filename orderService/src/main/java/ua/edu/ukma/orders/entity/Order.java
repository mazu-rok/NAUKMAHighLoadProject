package ua.edu.ukma.orders.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Order entity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    private UUID userId;
    private UUID eventId;
    private String eventName;
    private List<OrderedTicket> tickets;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
