package com.albers.webrtc.service;

import com.albers.webrtc.events.RoomEventBroadcaster;
import com.albers.webrtc.model.SignalMessage;
import com.albers.webrtc.model.SignalType;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SignalingServiceImpl implements SignalingService {

    private final SessionRegistry sessionRegistry;
    private final RoomManager roomManager;
    private final ObjectMapper objectMapper;
    private final RoomEventBroadcaster roomEventBroadcaster;

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
            case JOIN   -> handleJoin(clientId, message);
            case LEAVE  -> handleLeave(clientId, message);
            case OFFER  -> forward(clientId, message, "OFFER");
            case ANSWER -> forward(clientId, message, "ANSWER");
            case ICE    -> forward(clientId, message, "ICE");
            case CHAT   -> handleChat(clientId, message);
        }
    }

    private void handleJoin(String clientId, SignalMessage message) {

        String roomId = message.getRoomId();

        // Defensive copy — getParticipants() returns the live ConcurrentHashMap
        // keyset; without copying, joinRoom() would mutate it mid-loop.
        Set<String> existingPeers = new HashSet<>(roomManager.getParticipants(roomId));

        System.out.println("[JOIN] new client=" + clientId
            + " | existing peers snapshot=" + existingPeers);

        // Step 1 — tell the NEW client who is already in the room.
        //           The new client registers these IDs but does NOT send offers —
        //           it just knows who to expect an offer from.
        for (String existing : existingPeers) {
            System.out.println("[JOIN] -> USER_JOINED(" + existing
                + ") TO new client " + clientId);
            sessionRegistry.sendMessage(clientId,
                new SignalMessage(SignalType.USER_JOINED, roomId, existing, null, null)
            );
        }

        // Step 2 — add the new client to the room.
        roomManager.joinRoom(roomId, clientId);

        // Step 3 — tell each EXISTING peer to initiate an offer to the new client.
        //           PLEASE_OFFER = "you are the impolite offerer, send an offer to senderId".
        //           The new client will receive those offers and answer as the polite peer.
        for (String existing : existingPeers) {
            System.out.println("[JOIN] -> PLEASE_OFFER(target=" + clientId
                + ") TO existing peer " + existing);
            sessionRegistry.sendMessage(existing,
                new SignalMessage(SignalType.PLEASE_OFFER, roomId, clientId, null, null)
            );
        }

        System.out.println("[JOIN] room " + roomId + " now has: "
            + roomManager.getParticipants(roomId));
    }

    private void handleChat(String clientId, SignalMessage message) {

        String roomId = message.getRoomId();
        String chatMessage = (String) message.getPayload();
        Set<String> participants = roomManager.getParticipants(roomId);

        for (String participant : participants) {
            sessionRegistry.sendMessage(participant,
                new SignalMessage(SignalType.CHAT, roomId, clientId, null, chatMessage)
            );
        }
    }

    private void forward(String fromClientId,
                         SignalMessage message,
                         String label) {

        String targetClientId = message.getTargetId();

        System.out.println("[FORWARD] " + label
            + " from=" + fromClientId
            + " to=" + targetClientId);

        if (targetClientId == null || targetClientId.equals(fromClientId)) {
            System.out.println("[FORWARD] DROPPED " + label
                + " — targetId null or equals sender");
            return;
        }

        WebSocketSession targetSession = sessionRegistry.getSession(targetClientId);

        if (targetSession == null || !targetSession.isOpen()) {
            System.out.println("[FORWARD] DROPPED " + label
                + " — target session not found or closed");
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            targetSession.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLeave(String clientId, SignalMessage message) {

        String roomId = message.getRoomId();

        roomManager.leaveRoom(roomId, clientId);
        roomEventBroadcaster.broadcastUserLeft(roomId, clientId);

        System.out.println("[LEAVE] " + clientId + " left room " + roomId);
    }
}
