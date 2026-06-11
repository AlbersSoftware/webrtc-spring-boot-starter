package com.albers.webrtc.autoconfigure;

import com.albers.webrtc.events.RoomEventBroadcaster;
import com.albers.webrtc.service.*;
import com.albers.webrtc.websocket.WebRTCSignalHandler;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication
public class WebRtcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RoomManager roomManager() {
        return new InMemoryRoomManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionRegistry sessionRegistry(ObjectMapper objectMapper) {
        return new InMemorySessionRegistry(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RoomEventBroadcaster roomEventBroadcaster(SessionRegistry sessionRegistry,
                                                     RoomManager roomManager) {
        return new RoomEventBroadcaster(sessionRegistry, roomManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public SignalingService signalingService(SessionRegistry sessionRegistry,
                                             RoomManager roomManager,
                                             ObjectMapper objectMapper,
                                             RoomEventBroadcaster roomEventBroadcaster) {
        return new SignalingServiceImpl(sessionRegistry, roomManager,
                objectMapper, roomEventBroadcaster);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebRTCSignalHandler webRTCSignalHandler(SessionRegistry sessionRegistry,
                                                   SignalingService signalingService,
                                                   ObjectMapper objectMapper,
                                                   RoomManager roomManager,
                                                   RoomEventBroadcaster roomEventBroadcaster) {
        return new WebRTCSignalHandler(sessionRegistry, signalingService,
                objectMapper, roomManager, roomEventBroadcaster);
    }
}
