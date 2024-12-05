package group6cs442.backend.TurnSystem;

import group6cs442.backend.Player.Player;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TurnSystem {
    private final List<Player> players;
    private final Set<WebSocketSession> sessions;
    private int currentPlayerIndex = 0;
    private Timer timer;

    public TurnSystem(List<Player> players, Set<WebSocketSession> sessions) {
        this.players = players;
        this.sessions = sessions;
    }

    public void startTurns() {
        reset();
        if (players.isEmpty()) {
            System.out.println("No players available to start turns.");
            return;
        }
        startTurn(players.get(currentPlayerIndex));
    }

    private void startTurn(Player player) {
        if (!player.getGameBoard().getPlayerLife()) {
            advanceToNextPlayer();
            return;
        }

        System.out.println("It's " + player.getDisplayName() + "'s turn.");
        sendTurnUpdate(player.getId());
        timer = new Timer();
        int turnDuration = 20000; // 30 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endTurn();
            }
        }, turnDuration);
    }

    private void advanceToNextPlayer() {
        if (players.isEmpty()) {
            System.out.println("No players available to take a turn.");
            stopTurns();
            return;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        startTurn(players.get(currentPlayerIndex));
    }

    public void endTurn() {
        if (timer != null) {
            timer.cancel();
        }
        if (players.isEmpty()) {
            System.out.println("No players remaining. Ending turns.");
            stopTurns();
            return;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        startTurn(players.get(currentPlayerIndex));
    }

    public void playerAction() {
        if (timer != null) {
            timer.cancel();
        }
        endTurn();
    }

    private void stopTurns() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void playerLeave(Player player) {
        if (timer != null) {
            timer.cancel();
        }
        //TODO: Does this move the turn to the next player?
        players.remove(player);
        if (players.size() <= 1) {
            System.out.println("Only one or no players left in the game.");
            stopTurns();
        }
    }

    private void sendTurnUpdate(String playerId) {
        String message = String.format("{\"type\": \"TURN_UPDATE\", \"playerId\": \"%s\"}", playerId);
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reset() {
        currentPlayerIndex = 0;
    }
}