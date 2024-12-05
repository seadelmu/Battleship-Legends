package group6cs442.backend.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @BeforeEach
    void setUp() {
        Player player = new Player();
    }

    @Test
    void getId() {
        Player player = new Player();
        assertEquals("", player.getId());
    }

    @Test
    void setId() {
        Player player = new Player();
        player.setId("1");
        assertEquals("1", player.getId());
    }

    @Test
    void getDisplayName() {
        Player player = new Player();
        assertEquals("", player.getDisplayName());
    }

    @Test
    void setDisplayName() {
        Player player = new Player();
        player.setDisplayName("John");
        assertEquals("John", player.getDisplayName());
    }

    @Test
    void isReady() {
        Player player = new Player();
        assertFalse(player.isReady());
    }

    @Test
    void setReady() {
        Player player = new Player();
        player.setReady(true);
        assertTrue(player.isReady());
    }

    @Test
    void getGameBoard() {
        Player player = new Player();
        assertNotNull(player.getGameBoard());
    }

}