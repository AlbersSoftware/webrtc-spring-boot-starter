package com.albers.webrtc.service;

import com.albers.webrtc.events.RoomEventBroadcaster;
import com.albers.webrtc.model.SignalMessage;
import com.albers.webrtc.model.SignalType;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

public class SignalingServiceImpl implements SignalingService {

    private final SessionRegistry sessionRegistry;
    private final RoomManager roomManager;
    private final ObjectMapper objectMapper;
    private final RoomEventBroadcaster roomEventBroadcaster; // ✅ FIXED

    public SignalingServiceImpl(
            SessionRegistry sessionRegistry,
            RoomManager roomManager,
            ObjectMapper objectMapper,
            RoomEventBroadcaster roomEventBroadcaster
    ) {
        this.sessionRegistry = sessionRegistry;
        this.roomManager = roomManager;
        this.objectMapper = objectMapper;
        this.roomEventBroadcaster = roomEventBroadcaster;
    }

    @Override
    public void handleMessage(String clientId, SignalMessage message) {

        switch (message.getType()) {

            case JOIN -> handleJoin(clientId, message);

            case LEAVE -> handleLeave(clientId, message);

            case OFFER -> forward(clientId, message, "OFFER");

            case ANSWER -> forward(clientId, message, "ANSWER");

            case ICE -> forward(clientId, message, "ICE");

            case CHAT -> handleChat(clientId, message);
        }
    }

    private void handleJoin(String clientId, SignalMessage message) {

        String roomId = message.getRoomId();

        roomManager.joinRoom(roomId, clientId);

        Set<String> participants = roomManager.getParticipants(roomId);

        System.out.println("[JOIN] " + participants);

        // keep your broadcaster call
        roomEventBroadcaster.broadcastUserJoined(roomId, clientId);
    }

    private void handleChat(String clientId, SignalMessage message) {

        String roomId = message.getRoomId();
        String chatMessage = (String) message.getPayload();

        Set<String> participants = roomManager.getParticipants(roomId);

        for (String participant : participants) {

            sessionRegistry.sendMessage(participant,
                    new SignalMessage(
                            SignalType.CHAT,
                            roomId,
                            clientId,
                            null,
                            chatMessage
                    )
            );
        }
    }

    private void forward(String fromClientId,
                         SignalMessage message,
                         String label) {

        String targetClientId = message.getTargetId();

        WebSocketSession targetSession = sessionRegistry.getSession(targetClientId);

        if (targetSession == null || !targetSession.isOpen()) return;

        try {
            String json = objectMapper.writeValueAsString(message);
            targetSession.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLeave(String clientId, SignalMessage message) {
        roomManager.leaveRoom(message.getRoomId(), clientId);
    }
}
