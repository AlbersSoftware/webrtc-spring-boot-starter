package com.albers.webrtc.service;

import com.albers.webrtc.model.SignalMessage;
import org.springframework.web.socket.WebSocketSession;

public interface SessionRegistry {

    void registerSession(String clientId, WebSocketSession session);

    void removeSessionByClientId(String clientId);

    void removeSessionBySessionId(String sessionId);

    WebSocketSession getSession(String clientId);

    String getClientId(String sessionId);

    void sendMessage(String clientId, SignalMessage message);
}
