package com.albers.webrtc.config;

import com.albers.webrtc.service.*;
import com.albers.webrtc.events.RoomEventBroadcaster;
import tools.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CORE WIRING CONFIGURATION
 */
@Configuration
public class WebRTCConfiguration {

    // =========================================================
    // ROOM MANAGEMENT
    // =========================================================
    @Bean
    public RoomManager roomManager() {
        System.out.println("[WebRTCConfiguration] Creating InMemoryRoomManager");
        return new InMemoryRoomManager();
    }

    // =========================================================
    // SESSION REGISTRY
    // =========================================================
    @Bean
    public SessionRegistry sessionRegistry(ObjectMapper objectMapper) {
        System.out.println("[WebRTCConfiguration] Creating InMemorySessionRegistry");
        return new InMemorySessionRegistry(objectMapper);
    }

    // =========================================================
    // ROOM EVENT BROADCASTER
    // =========================================================
    @Bean
    public RoomEventBroadcaster roomEventBroadcaster(SessionRegistry sessionRegistry,
                                                     RoomManager roomManager) {

        System.out.println("[WebRTCConfiguration] Creating RoomEventBroadcaster");

        return new RoomEventBroadcaster(sessionRegistry, roomManager);
    }

    // =========================================================
    // SIGNALING SERVICE (CORE BRAIN)
    // =========================================================
    @Bean
    public SignalingService signalingService(SessionRegistry sessionRegistry,
                                             RoomManager roomManager,
                                             ObjectMapper objectMapper,
                                             RoomEventBroadcaster roomEventBroadcaster) {

        System.out.println("[WebRTCConfiguration] Creating SignalingServiceImpl");

        return new SignalingServiceImpl(
                sessionRegistry,
                roomManager,
                objectMapper,
                roomEventBroadcaster
        );
    }
}
