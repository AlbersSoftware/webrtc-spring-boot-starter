package com.albers.webrtc.service;

import java.util.Set;

public interface RoomManager {

    void joinRoom(String roomId, String clientId);

    void leaveRoom(String roomId, String clientId);

    Set<String> getParticipants(String roomId);

    boolean roomExists(String roomId);
}
