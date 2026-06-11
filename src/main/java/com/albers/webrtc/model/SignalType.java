package com.albers.webrtc.model;

public enum SignalType {
    JOIN,
    OFFER,
    ANSWER,
    ICE,
    LEAVE,
    ASSIGN_ID,
    CHAT,
    USER_JOINED,   // sent to new joiner: tells them who's already in the room
    PLEASE_OFFER,  // sent to existing peer: tells them to initiate offer to new joiner
    USER_LEFT
}
