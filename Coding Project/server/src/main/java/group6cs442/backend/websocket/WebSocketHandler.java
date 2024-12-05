package group6cs442.backend.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group6cs442.backend.Lobby.Lobby;
import group6cs442.backend.Player.Player;
import group6cs442.backend.TurnSystem.TurnSystem;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<String, TurnSystem> turnSystems = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> lobbySessions = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Map<String, Player>> lobbyPlayers = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public void startGame(String lobbyCode) {
        if (!turnSystems.containsKey(lobbyCode)) {
            List<Player> players = new ArrayList<>(lobbyPlayers.get(lobbyCode).values());
            Set<WebSocketSession> sessions = lobbySessions.get(lobbyCode);
            TurnSystem turnSystem = new TurnSystem(players, sessions);
            turnSystems.put(lobbyCode, turnSystem);
            turnSystem.startTurns();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String lobbyCode = getLobbyCode(session);
        lobbies.computeIfAbsent(lobbyCode, k -> new Lobby(lobbyCode, "defaultPassword", 10)); // Example initialization
        lobbySessions.computeIfAbsent(lobbyCode, k -> Collections.synchronizedSet(new HashSet<>())).add(session);

        // Send the sessionId to the client
        session.sendMessage(new TextMessage("{\"sessionId\": \"" + session.getId() + "\"}"));
        System.out.println("Connection established in lobby " + lobbyCode + ". Total clients: " + lobbySessions.get(lobbyCode).size());
        // Ensure the new player gets the updated list of players
        broadcastPlayersUpdate(lobbyCode);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        String lobbyCode = getLobbyCode(session);
        Set<WebSocketSession> sessions = lobbySessions.get(lobbyCode);
        if (sessions != null) {
            sessions.remove(session);
            Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(sessionId);
            if (player != null) {
                System.out.println("PlayerID is:" + player.getId());
                removePlayerFromLobby(lobbyCode, player);
                turnSystems.get(lobbyCode).playerLeave(player);
                System.out.println("Player removed..");
            } else {
                System.out.println("Player not found for session ID: " + sessionId);
            }
            System.out.println("Connection closed in lobby " + lobbyCode + ". Total clients: " + sessions.size());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(payload);
        System.out.println("Received message: " + jsonNode);
        String type = jsonNode.get("type").asText();
        System.out.println("Type: " + type);
        if ("READY_UP".equals(type)) {
            String playerId = session.getId();
            String lobbyCode = getLobbyCode(session);
            if (lobbyCode != null) {
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                if (player != null) {
                    player.setReady(!player.isReady());
                    broadcastPlayersUpdate(lobbyCode);
                    System.out.println("Player " + player.getDisplayName() + " is ready in lobby " + lobbyCode);
                }
            }
        }
        if ("ADD_SHIP".equals(type)){
            String playerId = session.getId();
            String lobbyCode = getLobbyCode(session);
            if (lobbyCode != null) {
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                if (player != null) {
                    player.getGameBoard().AddShip(jsonNode.get("y").asInt(), jsonNode.get("x").asInt(), jsonNode.get("length").asInt(), jsonNode.get("orientation").asText(), jsonNode.get("shipId").asInt());
                    broadcastPlayersUpdate(lobbyCode);
                    System.out.println("Player" + player.getDisplayName() + "placed a ship in lobby" + lobbyCode);
                }
            }
        }
        if ("HIT_CELL".equals(type)) {
            String playerId = session.getId();
            String lobbyCode = getLobbyCode(session);
            TurnSystem turnSystem = turnSystems.get(lobbyCode);

            if (lobbyCode != null) {
                Player playerWhoShot = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(jsonNode.get("serverPlayer").asText());

                if (player != null && playerWhoShot != null) {
                    String selectedPowerUp = playerWhoShot.getGameBoard().getSelectedPowerUp();

                    if (!"Default".equals(selectedPowerUp)) {
                        // Call the respective power-up function based on the selected power-upc
                        switch (selectedPowerUp) {
                            case "Bomb":
                                player.getGameBoard().crossShotPowerUp(jsonNode.get("row").asInt(), jsonNode.get("col").asInt(), jsonNode.get("color").asText());
                                playerWhoShot.getGameBoard().removePowerup("Bomb");
                                playerWhoShot.getGameBoard().setSelectedPowerUp("Default");
                                break;
                            case "Nuke":
                                player.getGameBoard().nukePowerUp(jsonNode.get("color").asText());
                                playerWhoShot.getGameBoard().removePowerup("Nuke");
                                playerWhoShot.getGameBoard().setSelectedPowerUp("Default");
                                break;
                            default:
                                System.out.println("Unknown power-up selected: " + selectedPowerUp);
                        }
                        // Update player life status after power-up hit
                        System.out.println("Is the player alive? " + player.getGameBoard().getPlayerLife());
                        broadcastPlayersUpdate(lobbyCode);

                        isPlayerLastAlive(lobbyCode, turnSystem, player);
                    } else {
                        // Proceed with the hit cell action if no power-up is selected
                        if (player.getGameBoard().getPlayerBoard()[jsonNode.get("row").asInt()][jsonNode.get("col").asInt()].contains("M") ||
                                player.getGameBoard().getPlayerBoard()[jsonNode.get("row").asInt()][jsonNode.get("col").asInt()].contains("P_HIT") ||
                                player.getGameBoard().getPlayerBoard()[jsonNode.get("row").asInt()][jsonNode.get("col").asInt()].contains("B_H")) {
                            System.out.println("Cell has already been hit. Turn will not change.");
                        } else {
                            player.getGameBoard().hit(jsonNode.get("row").asInt(), jsonNode.get("col").asInt(), jsonNode.get("color").asText());
                            System.out.println("Is the player alive? " + player.getGameBoard().getPlayerLife());
                            broadcastPlayersUpdate(lobbyCode);

                            if (player.getGameBoard().getPlayerBoard()[jsonNode.get("row").asInt()][jsonNode.get("col").asInt()].contains("B")) {
                                System.out.println(player.getDisplayName() + " has just been struck!");
                                playerWhoShot.getGameBoard().incrementPoints();
                                broadcastPlayersUpdate(lobbyCode);
                            } else if (player.getGameBoard().getPlayerBoard()[jsonNode.get("row").asInt()][jsonNode.get("col").asInt()].contains("P")) {
                                System.out.println(player.getDisplayName() + " hit a point cell!");
                                playerWhoShot.getGameBoard().hitPointCell();
                                broadcastPlayersUpdate(lobbyCode);
                            }
                            isPlayerLastAlive(lobbyCode, turnSystem, player);
                        }
                    }
                }
            }
        }
        if ("SEND_MESSAGE".equals(type)) {
            String playerId = session.getId();
            String lobbyCode = getLobbyCode(session);
            if (lobbyCode != null) {
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                if (player != null) {
                    String color = jsonNode.get("color").asText();
                    String chat = jsonNode.get("message").asText();
                    System.out.println("Player " + player.getDisplayName() + " sent a message in lobby " + lobbyCode);
                    System.out.println("Message: " + chat);

                    // Create a JSON message to broadcast
                    String broadcastMessage = new ObjectMapper().writeValueAsString(Map.of(
                            "type", "RECEIVE_MESSAGE",
                            "displayName", player.getDisplayName(),
                            "displayNameColor", color,
                            "message", chat
                    ));

                    // Broadcast the message to all clients in the lobby
                    for (WebSocketSession wsSession : lobbySessions.getOrDefault(lobbyCode, Collections.emptySet())) {
                        if (!wsSession.getId().equals(session.getId())) {
                            wsSession.sendMessage(new TextMessage(broadcastMessage));
                        }
                    }
                }
            }
        }

        if ("PLACE_POWERUPS".equals(type)) {
            String lobbyCode = getLobbyCode(session);
            String playerId = session.getId();
            if (lobbyCode != null) {
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                if (player != null) {
                    player.getGameBoard().hidePowerUps();
                    broadcastPlayersUpdate(lobbyCode);
                    System.out.println("Player " + player.getDisplayName() + " has power ups added to their stuff now" + lobbyCode);
                }
            }
        }

        if ("PURCHASE_POWERUP".equals(type)) {
            String lobbyCode = getLobbyCode(session);
            String playerId = session.getId();
            if (lobbyCode != null) {
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                if (player != null) {
                    player.getGameBoard().purchasePowerup(jsonNode.get("item_name").asText(), jsonNode.get("item_price").asInt());
                    broadcastPlayersUpdate(lobbyCode);
                    System.out.println("Player " + player.getDisplayName() + " has purchased a power up in lobby " + lobbyCode);
                }
            }
        }

        if ("SELECT_POWER_UP".equals(type)) {
            String lobbyCode = getLobbyCode(session);
            String playerId = session.getId();
            if (lobbyCode != null) {
                Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(playerId);
                if (player != null) {
                    player.getGameBoard().setSelectedPowerUp(jsonNode.get("powerUp").asText());
                    broadcastPlayersUpdate(lobbyCode);
                    System.out.println("Player " + player.getDisplayName() + " has selected a power up in lobby " + lobbyCode);
                }
            }
        }
    }

    private void isPlayerLastAlive(String lobbyCode, TurnSystem turnSystem, Player player) throws IOException {
        if (!player.getGameBoard().getPlayerLife()) {
            String broadcastMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "PLAYER_DEATH",
                    "playerId", player.getId(),
                    "playerName", player.getDisplayName()));

            for (WebSocketSession wsSession : lobbySessions.getOrDefault(lobbyCode, Collections.emptySet())) {
                wsSession.sendMessage(new TextMessage(broadcastMessage));
            }
            int alivePlayersCount = lobbyPlayers.get(lobbyCode).values().stream().filter(p -> p.getGameBoard().getPlayerLife()).toArray().length;
            System.out.println("Alive Players Count: " + alivePlayersCount);

            Optional<Player> winnerOptional = lobbyPlayers.get(lobbyCode).values().stream()
                    .filter(p -> p.getGameBoard().getPlayerLife())
                    .findFirst();

            if (alivePlayersCount == 1) {
                Player winner = winnerOptional.get();
                System.out.println("Winner is: " + winner.getDisplayName());
                String gameOverMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "GAME_OVER",
                        "winnerId", winner.getId(),
                        "winnerName", winner.getDisplayName()
                ));

                for (WebSocketSession wsSession : lobbySessions.getOrDefault(lobbyCode, Collections.emptySet())) {
                    String result = wsSession.getId().equals(winner.getId()) ? "You won!" : "You lost!";
                    wsSession.sendMessage(new TextMessage(gameOverMessage));
                    wsSession.sendMessage(new TextMessage("{\"type\": \"RESULT\", \"message\": \"" + result + "\"}"));
                }
            }
        }

        if (turnSystem != null) {
            turnSystem.playerAction();
        }
    }

    public void updateAreShipsPlaced(String lobbyCode, String sessionId, boolean areShipsPlaced) {
        Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(sessionId);
        if (player != null) {
            player.setAreShipsPlaced(areShipsPlaced);
            System.out.println("Player " + player.getDisplayName() + " has placed their ships in lobby " + lobbyCode);
        }
    }

    // Currently does nothing because the playerBoard is already occupied from the sent message
    public void populatePlayerBoard(WebSocketSession session, String lobbyCode, String[][] playerBoard) throws IOException {
        Lobby lobby = lobbies.get(lobbyCode);
        if (lobby != null) {
            Player player = lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap()).get(session.getId());
            if (player != null) {
            // lobby.setPlayerBoard(playerBoard, player);
                // I guess it broadcasts stuff, but that's pretty much nothing
                broadcastPlayersUpdate(lobbyCode);
            }
        }
    }

    private String getLobbyCode(WebSocketSession session) {
        System.out.println(session.getUri());
        return Objects.requireNonNull(session.getUri()).getPath().split("/")[2];
    }

    public int getNumberOfConnectedClients(String lobbyCode) {
        return lobbySessions.getOrDefault(lobbyCode, Collections.emptySet()).size();
    }

    public void addPlayerToLobby(String lobbyCode, Player player) throws IOException {
        lobbies.get(lobbyCode).addPlayer(player);
        lobbyPlayers.computeIfAbsent(lobbyCode, k -> Collections.synchronizedMap(new LinkedHashMap<>())).put(player.getId(), player);
        System.out.println("Player " + player.getDisplayName() + " added to lobby " + lobbyCode);
        System.out.println("Player's PlayerID is:" + player.getId());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    broadcastPlayersUpdate(lobbyCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    public void removePlayerFromLobby(String lobbyCode, Player player) throws IOException {
        Map<String, Player> players = lobbyPlayers.get(lobbyCode);
        if (players != null) {
            players.remove(player.getId());
            System.out.println("Player " + player.getDisplayName() + " removed from lobby " + lobbyCode);
        }
        broadcastPlayersUpdate(lobbyCode);
    }

    public Map<String, Player> getPlayersInLobby(String lobbyCode) {
        return lobbyPlayers.getOrDefault(lobbyCode, Collections.emptyMap());
    }

    // This method is used to broadcast the updated list of players to all clients in the lobby
    private synchronized void broadcastPlayersUpdate(String lobbyCode) throws IOException {
        Set<WebSocketSession> sessions = lobbySessions.get(lobbyCode);
        if (sessions != null) {
            Map<String, Player> players = getPlayersInLobby(lobbyCode);
            String playersJson = new ObjectMapper().writeValueAsString(players.values());
            TextMessage message = new TextMessage(playersJson);

            for (WebSocketSession session : sessions) {
                synchronized (session) { // Lock individual session for sending messages
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    }
                }
            }
        }
    }

    public void closeSession(WebSocketSession session) throws Exception {
        String lobbyCode = getLobbyCode(session);
        session.close(CloseStatus.NORMAL);
        Set<WebSocketSession> sessions = lobbySessions.get(lobbyCode);
        if (sessions != null) {
            sessions.remove(session);
            System.out.println("Session closed in lobby " + lobbyCode + ". Total clients: " + sessions.size());
        }
    }

    public Set<WebSocketSession> getSessions(String lobbyCode) {
        return lobbySessions.getOrDefault(lobbyCode, Collections.emptySet());
    }
}