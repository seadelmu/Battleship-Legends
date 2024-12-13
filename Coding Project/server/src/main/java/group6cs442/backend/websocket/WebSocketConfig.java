package group6cs442.backend.websocket;

import group6cs442.backend.websocket.WebSocketHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Value("${environment}")
    private String environment;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String protocol = "ws";
        if ("production".equals(environment)) {
            protocol = "wss";
        }
        registry.addHandler(webSocketHandler, "/" + protocol + "/{lobbyCode}").setAllowedOrigins("*");
    }
}