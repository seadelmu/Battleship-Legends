package group6cs442.backend.Player;
import group6cs442.backend.GameBoard;

public class Player {
    private String id;
    private String displayName;
    private boolean isReady;
    private GameBoard gameBoard;
    private boolean areShipsPlaced;

    public Player() {
        this.id = "";
        this.displayName = "";
        this.isReady = false;
        this.areShipsPlaced = false;
        this.gameBoard = new GameBoard(); // Initialize gameBoard
    }

    public Player(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.isReady = false;
        this.areShipsPlaced = false;
        this.gameBoard = new GameBoard(); // Initialize gameBoard
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setPlayerBoard(String[][] playerBoard) {
        // parse through the board and call addShip() for each ship

        this.gameBoard.setPlayerBoard(playerBoard);
    }

    public boolean getAreShipsPlaced() {
        return areShipsPlaced;
    }

    public void setAreShipsPlaced(boolean areShipsPlaced) {
        this.areShipsPlaced = areShipsPlaced;
    }

}