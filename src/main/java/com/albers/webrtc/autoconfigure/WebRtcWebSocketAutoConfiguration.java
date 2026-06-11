package com.albers.webrtc.autoconfigure;

import com.albers.webrtc.websocket.WebRTCSignalHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.socket.config.annotation.*;

@AutoConfiguration(after = WebRtcAutoConfiguration.class)
@EnableWebSocket
@ConditionalOnBean(WebRTCSignalHandler.class)
public class WebRtcWebSocketAutoConfiguration implements WebSocketConfigurer {

    private final WebRTCSignalHandler signalHandler;

    public WebRtcWebSocketAutoConfiguration(WebRTCSignalHandler signalHandler) {
        this.signalHandler = signalHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal")
                .setAllowedOrigins("*");
    }
}
