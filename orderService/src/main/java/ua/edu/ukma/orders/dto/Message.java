package ua.edu.ukma.orders.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class Message {
  private MessageStatus status;
  private UUID placeId;

  public enum MessageStatus {
    BOOKED, ORDERED, AVAILABLE
  }
}