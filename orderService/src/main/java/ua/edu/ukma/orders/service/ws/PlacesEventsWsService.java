package ua.edu.ukma.orders.service.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import ua.edu.ukma.orders.dto.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PlacesEventsWsService extends BinaryWebSocketHandler {
    private static final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        String eventId = session.getUri().getQuery().split("=")[1];
        log.info("New connection established, event id: " + eventId);

        session.getAttributes().put("roomId", eventId);
        roomSessions.computeIfAbsent(eventId, _ -> new HashSet<>()).add(session);
        log.info("Added session: " + session.getId() + ", to room: " + eventId);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("text", "New session: " + session.getId() + " joined to room: " + eventId);
        Message message = new Message(Message.MessageType.JOIN, messageData);
        broadcastToRoom(eventId, message, session.getId());
    }

    @SneakyThrows
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId == null) {
            log.error("roomId is null for session: " + session.getId());
            return;
        }

        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        Message parsedMessage = mapper.readValue(payload, Message.class);

        log.info("Received message: " + parsedMessage.getMessage());
        log.info("Received session: " + session.getId());

        Message.MessageType type = parsedMessage.getType();
        if (type == Message.MessageType.BOOK_PLACE) {
            parsedMessage.setType(Message.MessageType.BOOKED_PLACE);
            broadcastToRoom(roomId, parsedMessage, session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = (String) session.getAttributes().get("roomId");

        log.info("Connection closed for session: " + session.getId() + ", Status: " + status);

        if (roomId != null) {
            Set<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
            if (sessionsInRoom != null) {
                sessionsInRoom.remove(session);
                log.info("Removed session: " + session.getId() + " from room: " + roomId);
            }

            if (sessionsInRoom == null || sessionsInRoom.isEmpty()) {
                roomSessions.remove(roomId);
                log.info("Room " + roomId + " is empty. Room closed.");
            }
        }
    }

    private void broadcastToRoom(String roomID, Message message, String sessionId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonMessage = mapper.writeValueAsString(message);;

        Set<WebSocketSession> sessions = roomSessions.get(roomID);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (sessionId != session.getId()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }

            }
        }
    }
}
