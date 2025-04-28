package ua.edu.ukma.orders.dto;

import lombok.Data;
import java.util.Map;

@Data
public class Message {
  private MessageType type;
  private Map<String, Object> message;

  public Message() {
  }

  public Message(MessageType type, Map<String, Object> message) {
    this.type = type;
    this.message = message;
  }

  public enum MessageType {
    CHANGE_PLACE_STATUS
  }
}