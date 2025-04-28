package ua.edu.ukma.orders.dto;

import lombok.Builder;
import lombok.Data;
import ua.edu.ukma.orders.entity.PlaceStatus;

import java.util.UUID;

@Data
@Builder
public class Message {
  private PlaceStatus status;
  private UUID placeId;
}