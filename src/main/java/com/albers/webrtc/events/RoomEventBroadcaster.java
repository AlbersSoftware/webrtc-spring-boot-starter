package com.albers.webrtc.events;

import com.albers.webrtc.model.SignalMessage;
import com.albers.webrtc.model.SignalType;
import com.albers.webrtc.service.SessionRegistry;
import com.albers.webrtc.service.RoomManager;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RoomEventBroadcaster {

    private final SessionRegistry sessionRegistry;
    private final RoomManager roomManager;

    public RoomEventBroadcaster(SessionRegistry sessionRegistry,
                                RoomManager roomManager) {
        this.sessionRegistry = sessionRegistry;
        this.roomManager = roomManager;
    }

    // =====================================================
    // CALL THIS WHEN SOMEONE JOINS A ROOM
    // =====================================================
    public void broadcastUserJoined(String roomId, String newClientId) {

        Set<String> participants = roomManager.getParticipants(roomId);

        SignalMessage message = new SignalMessage();
        message.setType(SignalType.USER_JOINED);
        message.setRoomId(roomId);
        message.setSenderId(newClientId);
        message.setPayload(null);

        for (String clientId : participants) {

            // don’t send JOIN event to the user who just joined (optional but cleaner)
            if (clientId.equals(newClientId)) continue;

            sessionRegistry.sendMessage(clientId, message);
        }
    }
}
