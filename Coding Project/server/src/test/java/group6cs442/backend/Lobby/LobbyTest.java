package group6cs442.backend.Lobby;

import group6cs442.backend.Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @BeforeEach
    void setUp() {
        Lobby lobby = new Lobby("1234", "password", 2);

        Player player1 = new Player();
        player1.setId("1");
        player1.setDisplayName("John");

        Player player2 = new Player();
        player2.setId("2");
        player2.setDisplayName("Jane");
    }

    @Test
    void getLobbyCode() {
        Lobby lobby = new Lobby("1234", "password", 2);
        assertEquals("1234", lobby.getLobbyCode());
    }

    @Test
    void setPlayerBoard() {
        Lobby lobby = new Lobby("1234", "password", 2);
        Player player = new Player();
        String[][] playerBoard = new String[10][10];
        lobby.setPlayerBoard(playerBoard, player);
        assertEquals(playerBoard, player.getGameBoard().getPlayerBoard());

    }

}