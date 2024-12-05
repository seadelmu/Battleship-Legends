package group6cs442.backend.Lobby;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Lobbies {
    private final List<Lobby> lobbies;

    public Lobbies() {
        this.lobbies = new ArrayList<>();
    }

    public boolean exists(String lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFull(String lobbyCode) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyCode().equals(lobbyCode)) {
                return (lobby.getMaxPlayers() == lobby.getPlayers().size());
            }
        }
        return false;
    }

    public void addLobby(Lobby lobby) {
        lobbies.add(lobby);
    }
}