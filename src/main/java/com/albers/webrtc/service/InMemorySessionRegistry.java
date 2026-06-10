package com.albers.webrtc.service;

import com.albers.webrtc.model.SignalMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRegistry implements SessionRegistry {

    private final ConcurrentHashMap<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionToClient = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    // =========================================================
    // CONSTRUCTOR (required for Spring config wiring)
    // =========================================================
    public InMemorySessionRegistry(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // REGISTER SESSION
    // =========================================================
    @Override
    public void registerSession(String clientId, WebSocketSession session) {
        if (clientId == null || session == null) return;

        clientSessions.put(clientId, session);
        sessionToClient.put(session.getId(), clientId);
    }

    // =========================================================
    // REMOVE BY CLIENT ID
    // =========================================================
    @Override
    public void removeSessionByClientId(String clientId) {
        WebSocketSession session = clientSessions.remove(clientId);

        if (session != null) {
            sessionToClient.remove(session.getId());
        }
    }

    // =========================================================
    // REMOVE BY SESSION ID
    // =========================================================
    @Override
    public void removeSessionBySessionId(String sessionId) {
        String clientId = sessionToClient.remove(sessionId);

        if (clientId != null) {
            clientSessions.remove(clientId);
        }
    }

    // =========================================================
    // GET SESSION
    // =========================================================
    @Override
    public WebSocketSession getSession(String clientId) {
        return clientSessions.get(clientId);
    }

    // =========================================================
    // GET CLIENT ID
    // =========================================================
    @Override
    public String getClientId(String sessionId) {
        return sessionToClient.get(sessionId);
    }

    // =========================================================
    // SEND MESSAGE
    // =========================================================
    @Override
    public void sendMessage(String clientId, SignalMessage message) {

        WebSocketSession session = clientSessions.get(clientId);

        if (session == null || !session.isOpen()) {
            System.out.println("[WS] Cannot send message, session missing: " + clientId);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));

        } catch (Exception e) {
            System.out.println("[WS] Failed to send message to " + clientId);
            e.printStackTrace();
        }
    }
}
