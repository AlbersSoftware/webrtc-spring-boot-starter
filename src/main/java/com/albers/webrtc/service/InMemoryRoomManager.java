package com.albers.webrtc.service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRoomManager implements RoomManager {

    private final ConcurrentHashMap<String, Set<String>> rooms = new ConcurrentHashMap<>();

    @Override
    public void joinRoom(String roomId, String clientId) {
        rooms.computeIfAbsent(roomId, r -> ConcurrentHashMap.newKeySet())
             .add(clientId);
    }

    @Override
    public void leaveRoom(String roomId, String clientId) {
        Set<String> participants = rooms.get(roomId);

        if (participants != null) {
            participants.remove(clientId);

            if (participants.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    @Override
    public Set<String> getParticipants(String roomId) {
        return rooms.getOrDefault(roomId, Collections.emptySet());
    }

    @Override
    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }
}
