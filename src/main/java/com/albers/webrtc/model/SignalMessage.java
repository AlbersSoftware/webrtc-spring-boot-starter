package com.albers.webrtc.model;

public class SignalMessage {

    private SignalType type;
    private String roomId;

    // Who sent the message
    private String senderId;

    // Optional: specific target (used for OFFER/ANSWER/ICE)
    private String targetId;

    // Flexible payload (chat text OR SDP OR ICE candidate)
    private Object payload;

    public SignalMessage() {}

    public SignalMessage(SignalType type,
                         String roomId,
                         String senderId,
                         String targetId,
                         Object payload) {
        this.type = type;
        this.roomId = roomId;
        this.senderId = senderId;
        this.targetId = targetId;
        this.payload = payload;
    }

    public SignalType getType() {
        return type;
    }

    public void setType(SignalType type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
