package group6cs442.backend.Lobby;

import group6cs442.backend.Player.Player;
import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private List<Player> players;
    private String lobbyCode;
    private String lobbyPassword;
    private int maxPlayers;

    public Lobby(String lobbyCode, String lobbyPassword, int maxPlayers) {
        this.lobbyCode = lobbyCode;
        this.lobbyPassword = lobbyPassword;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>(); // Initialize the players list
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setPlayerBoard(String[][] playerBoard, Player player) {
        // parse through the board and call addShip() for each ship

        player.setPlayerBoard(playerBoard);
    }

    public String getLobbyPassword() {
        return lobbyPassword;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}