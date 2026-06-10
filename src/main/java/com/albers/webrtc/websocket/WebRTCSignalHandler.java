package com.albers.webrtc.websocket;

import com.albers.webrtc.model.SignalMessage;
import com.albers.webrtc.model.SignalType;
import com.albers.webrtc.service.SessionRegistry;
import com.albers.webrtc.service.SignalingService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.UUID;

/**
 * WEBSOCKET TRANSPORT LAYER (SPRING MANAGED BEAN)
 *
 * This class:
 * - Handles WebSocket lifecycle events
 * - Assigns clientId on connection
 * - Delegates all logic to SignalingService
 *
 * IMPORTANT:
 * This is now a Spring @Component so dependencies are injected properly.
 */
@Component
public class WebRTCSignalHandler extends TextWebSocketHandler {

    private final SessionRegistry sessionRegistry;
    private final SignalingService signalingService;
    private final ObjectMapper objectMapper;

    public WebRTCSignalHandler(SessionRegistry sessionRegistry,
                               SignalingService signalingService,
                               ObjectMapper objectMapper) {

        this.sessionRegistry = sessionRegistry;
        this.signalingService = signalingService;
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // CONNECTION OPEN
    // =========================================================
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String clientId = UUID.randomUUID().toString();

        System.out.println("\n[CONNECT] New connection");
        System.out.println("[CONNECT] clientId: " + clientId);
        System.out.println("[CONNECT] sessionId: " + session.getId());

        sessionRegistry.registerSession(clientId, session);
        session.getAttributes().put("clientId", clientId);

        SignalMessage message = new SignalMessage();
        message.setType(SignalType.ASSIGN_ID);
        message.setSenderId(clientId);

        send(session, message);

        System.out.println("[CONNECT] ASSIGN_ID sent\n");
    }

    // =========================================================
    // INCOMING MESSAGES
    // =========================================================
    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage textMessage) throws Exception {

        String raw = textMessage.getPayload();

        System.out.println("\n[WS RECEIVED] " + raw);

        try {
            SignalMessage message =
                    objectMapper.readValue(raw, SignalMessage.class);

            String clientId =
                    (String) session.getAttributes().get("clientId");

            System.out.println("[WS] Parsed type: " + message.getType());
            System.out.println("[WS] From clientId: " + clientId);

            signalingService.handleMessage(clientId, message);

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to handle WebSocket message");
            e.printStackTrace();
        }
    }

    // =========================================================
    // CONNECTION CLOSE
    // =========================================================
    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) throws Exception {

        String clientId =
                (String) session.getAttributes().get("clientId");

        System.out.println("\n[DISCONNECT] clientId: " + clientId);

        if (clientId != null) {
            sessionRegistry.removeSessionByClientId(clientId);
        }

        System.out.println("[DISCONNECT] cleanup complete\n");
    }

    // =========================================================
    // SEND HELPER
    // =========================================================
    private void send(WebSocketSession session,
                      SignalMessage message) throws Exception {

        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }
}
