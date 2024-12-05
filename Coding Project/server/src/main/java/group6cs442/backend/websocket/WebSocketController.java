package group6cs442.backend.websocket;

import group6cs442.backend.Player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class WebSocketController {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @GetMapping("/connectedClients/{lobbyCode}")
    public int getConnectedClients(@PathVariable String lobbyCode) {
        return webSocketHandler.getNumberOfConnectedClients(lobbyCode);
    }

    @PostMapping("/lobby/{lobbyCode}/addPlayer")
    public void addPlayerToLobby(@PathVariable String lobbyCode, @RequestBody Player player) throws IOException, InterruptedException {
        webSocketHandler.addPlayerToLobby(lobbyCode, player);
    }

    @PostMapping("/lobby/{lobbyCode}/populatePlayerBoard")
    public void populatePlayerBoard(@PathVariable String lobbyCode, @RequestBody Map<String, Object> requestBody) throws IOException {
        String sessionId = (String) requestBody.get("sessionId");
        List<List<String>> playerBoardList = (List<List<String>>) requestBody.get("playerBoard");
        String[][] playerBoard = playerBoardList.stream()
                .map(list -> list.toArray(new String[0]))
                .toArray(String[][]::new);
        Set<WebSocketSession> sessions = webSocketHandler.getSessions(lobbyCode);
        for (WebSocketSession session : sessions) {
            if (session.getId().equals(sessionId)) {
                webSocketHandler.populatePlayerBoard(session, lobbyCode, playerBoard);
                break;
            }
        }
    }


    @PostMapping("lobby/{lobbyCode}/updateAreShipsPlaced")
    public void updateAreShipsPlaced(@PathVariable String lobbyCode, @RequestBody Map<String, Object> requestBody) {
        String sessionId = (String) requestBody.get("sessionId");
        boolean areShipsPlaced = (boolean) requestBody.get("areShipsPlaced");
        webSocketHandler.updateAreShipsPlaced(lobbyCode, sessionId, areShipsPlaced);
    }

    @GetMapping("/lobby/{lobbyCode}/players")
    public Map<String, Player> getPlayersInLobby(@PathVariable String lobbyCode) {
        return webSocketHandler.getPlayersInLobby(lobbyCode);
    }

    @DeleteMapping("/disconnectClient/{lobbyCode}/{sessionId}")
    public void disconnectClient(@PathVariable String lobbyCode, @PathVariable String sessionId) throws Exception {
        Set<WebSocketSession> sessions = webSocketHandler.getSessions(lobbyCode);
        for (WebSocketSession session : sessions) {
            if (session.getId().equals(sessionId)) {
                webSocketHandler.closeSession(session);
                break;
            }
        }
    }

    @PostMapping("/start/{lobbyCode}")
    public void startGame(@PathVariable String lobbyCode) {
        webSocketHandler.startGame(lobbyCode);
    }


}