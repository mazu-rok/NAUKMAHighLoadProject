package ua.edu.ukma.orders.service.ws;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Slf4j
@Service
public class PlacesEventsWsService extends BinaryWebSocketHandler {
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("New connection established {}", session);
    }

    @Override
    public void handleBinaryMessage(@NonNull WebSocketSession session, BinaryMessage message) {
        log.info("Received message: " + message.getPayload());
        log.info("Received session: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Connection closed. Status: " + status);
    }
}
