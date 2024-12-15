package group6cs442.backend.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    // Inject a property to check the environment (default to development)
    @Value("${app.environment:development}")
    private String environment;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String protocol = isProductionEnvironment() ? "wss" : "ws";
        String endpoint = protocol + "/{lobbyCode}";
        registry.addHandler(webSocketHandler, endpoint).setAllowedOrigins("*");
    }

    private boolean isProductionEnvironment() {
        return "production".equalsIgnoreCase(environment);
    }
}
