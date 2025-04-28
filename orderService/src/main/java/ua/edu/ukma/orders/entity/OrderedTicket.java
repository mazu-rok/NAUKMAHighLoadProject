package ua.edu.ukma.orders.entity;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderedTicket {
    private UUID placeId;
    private Integer row;
    private Integer place;
}
