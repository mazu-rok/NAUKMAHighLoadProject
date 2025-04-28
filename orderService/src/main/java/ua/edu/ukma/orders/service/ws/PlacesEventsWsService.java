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
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        String eventId = session.getUri().getQuery().split("=")[1];
        log.info("New connection established, event id: " + eventId);

        session.getAttributes().put("eventId", eventId);
        roomSessions.computeIfAbsent(eventId, _ -> new HashSet<>()).add(session);
        log.info("Added session: " + session.getId() + ", to event: " + eventId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String eventId = (String) session.getAttributes().get("eventId");

        log.info("Connection closed for session: " + session.getId() + ", Status: " + status);

        if (eventId != null) {
            Set<WebSocketSession> sessionsInRoom = roomSessions.get(eventId);
            if (sessionsInRoom != null) {
                sessionsInRoom.remove(session);
                log.info("Removed session: " + session.getId() + " from room: " + eventId);
            }

            if (sessionsInRoom == null || sessionsInRoom.isEmpty()) {
                roomSessions.remove(eventId);
                log.info("Room " + eventId + " is empty. Room closed.");
            }
        }
    }

    private void broadcastForEvent(String eventId, Message message, String sessionId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonMessage = mapper.writeValueAsString(message);;

        Set<WebSocketSession> sessions = roomSessions.get(eventId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (sessionId != session.getId()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }

            }
        }
    }
}
