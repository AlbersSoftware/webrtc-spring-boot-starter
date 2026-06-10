package com.albers.webrtc.config;

import com.albers.webrtc.websocket.WebRTCSignalHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * WEBSOCKET ENDPOINT REGISTRATION
 *
 * This class ONLY registers the endpoint.
 * No logic, no services, no construction.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebRTCSignalHandler signalHandler;

    public WebSocketConfig(WebRTCSignalHandler signalHandler) {
        this.signalHandler = signalHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(signalHandler, "/signal")
                .setAllowedOrigins("*");

        System.out.println("[WebSocketConfig] /signal registered");
    }
}
