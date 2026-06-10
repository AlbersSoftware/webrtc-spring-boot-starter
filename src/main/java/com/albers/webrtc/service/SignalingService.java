package com.albers.webrtc.service;

import com.albers.webrtc.model.SignalMessage;

public interface SignalingService {

    void handleMessage(String clientId, SignalMessage message);

}
