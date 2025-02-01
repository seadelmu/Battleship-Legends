package group6cs442.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// GameBoard2 Class
//	Data structure and class methods written by Seann Tyler Del Mundo.
//
// A second version of the GameBoard Class written in retrospective
//
// After completing this project for class, I felt I wasn't utilizing the full potential of Java as an object-orientated language.
// Therefore, this is written and tested as a concept of what would've been done.
//
// NOTE: None of the methods that connect this class to the front end are implemented. This is simply a rewrite
//       	or reconstruction of how data is encapsulated and used.
//
// 		Code that my groupmates did to this file will be commented out, and specifically the code I did when developing
//			this class file will remain functional

//@JsonSerialize(using = GameBoardSerializer.class)
public class GameBoard2 {

	// A struct to store a single ship's information
	class ShipInfo {
		String shipName = "";
		String status = "live";
		int size = 0;
		PlayerTile locations[] = new PlayerTile[5];
	}

	// Data of Tiles in PlayerBoard
	class PlayerTile {
		String type = "None";
		String ID = "None";
		boolean hit = false;
		String playerBoardPrintable = ". ";

		public PlayerTile() {}

		public PlayerTile(String tileType, String id) {
			type = tileType;
			ID = id;
			playerBoardPrintable = id;
		}
	}

	// Data of Tile in ServerBoard
	class ServerTile {
		boolean hit = false;
		String serverBoardHitMarker = ". ";
		boolean sonarCheck = false;
	}

	private PlayerTile[][] PlayerBoard;
	private ServerTile[][] ServerBoard;

	int LiveShips = 0;
	ShipInfo[] allShips = new ShipInfo[5];
	private boolean playerLife = true;
	public int points = 0;
	// Give the user an inventory of powerups
	List<Map<String, Integer>> powerUpInventory = new ArrayList<>();
	public String selectedPowerUp = "Default";
	

	// public void purchasePowerup(String powerup, int cost) {
	// 	if (losePoints(cost)) {
	// 		boolean found = false;
	// 		for (Map<String, Integer> map : powerUpInventory) {
	// 			if (map.containsKey(powerup)) {
	// 				map.put(powerup, map.get(powerup) + 1);
	// 				found = true;
	// 				break;
	// 			}
	// 		}
	// 		if (!found) {
	// 			Map<String, Integer> powerupMap = new HashMap<>();
	// 			powerupMap.put(powerup, 1);
	// 			powerUpInventory.add(powerupMap);
	// 		}
	// 	}
	// }

	// public void removePowerup(String powerup) {
	// 	for (Map<String, Integer> map : powerUpInventory) {
	// 		if (map.containsKey(powerup)) {
	// 			int quantity = map.get(powerup);
	// 			if (quantity > 1) {
	// 				map.put(powerup, quantity - 1);
	// 			} else {
	// 				powerUpInventory.remove(map);
	// 			}
	// 			break;
	// 		}
	// 	}
	// }

	// public void usePowerup(String powerup, int row, int col) {
	// 	if (powerup.equals("crossShot")) {
	// 		crossShotPowerUp(row, col, "green");
	// 	} else if (powerup.equals("sonar")) {
	// 		sonarPowerUp(row, col);
	// 	} else if (powerup.equals("nuke")) {
	// 		nukePowerUp("green");
	// 	}
	// }

	public void setSelectedPowerUp(String powerUp) {
		selectedPowerUp = powerUp;
	}

	public String getSelectedPowerUp() {
		return selectedPowerUp;
	}

	public List<Map<String, Integer>> getPowerUpInventory() {
		return powerUpInventory;
	}

	// public String[][] getPlayerBoard() {
	// 	return PlayerBoard;
	// }


	// GameBoard2()
	//
	// Default constructor. Initializes PlayerBoard and ServerBoard
	// to 10x10 2D array with "." strings to make an empty board.
	public GameBoard2() {
		PlayerBoard = new PlayerTile[10][10];
		ServerBoard = new ServerTile[10][10];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				PlayerBoard[i][j] = new PlayerTile();
				ServerBoard[i][j] = new ServerTile();
			}
		}
	}

	// public void setPlayerBoard(String[][] playerBoard) {
	// 	PlayerBoard = playerBoard;
	// }

	// public String[][] getServerBoard() {
	// 	return ServerBoard;
	// }

	// public void incrementPoints() {
	// 	points = points + 1;
	// }

	// public void incrementPointsBy(int points) {
	// 	this.points += points;
	// }

	// public void hitPointCell() {
	// 	points = points + 10;
	// }

	// public boolean losePoints(int cost) {

	// 	if (points >= cost) {
	// 		points = points - cost;
	// 		return true;
	// 	} else {
	// 		System.out.println("Player does not have enough points to make this purchase.");
	// 		return false;
	// 	}
	// }

	// // call this after all ships are placed via rest api?
	public void hidePowerUps() {
		int powerUpCount = 0;
		int row = 0;
		int col = 0;
		while (powerUpCount != 5) {
			row = (int) (Math.random() * 10);
			col = (int) (Math.random() * 10);
			
			PlayerTile NewPowerUpTile = PlayerBoard[row][col];

			if (NewPowerUpTile.type == "None") {
				NewPowerUpTile.type = "PowerUp";
				NewPowerUpTile.playerBoardPrintable = "P ";
				powerUpCount++;
			}
		}
	}


	// AddShip()
	//
	// Method to add ships to the board, given an row,col coordinate,
	// the size of the ship, orientation of the ship, and a number identifier.
	public int AddShip(int row, int col, int shipSize, String orientation, int number) {
		int err = checkBounds(row, col, shipSize, orientation);
		String shipIdentifier = "B" + number;

		if (err == 1) {
			ShipInfo newShip = new ShipInfo();
			newShip.size = shipSize;

			if (Objects.equals(orientation, "north")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerTile newShipTile = new PlayerTile("Ship", shipIdentifier);
					PlayerBoard[row - i][col] = newShipTile;
					newShip.locations[i] = newShipTile;

				}
			} else if (Objects.equals(orientation, "south")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerTile newShipTile = new PlayerTile("Ship", shipIdentifier);
					PlayerBoard[row + i][col] = newShipTile;
					newShip.locations[i] = newShipTile;

				}
			} else if (Objects.equals(orientation, "east")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerTile newShipTile = new PlayerTile("Ship", shipIdentifier);
					PlayerBoard[row][col + i] = newShipTile;
					newShip.locations[i] = newShipTile;

				}
			} else if (Objects.equals(orientation, "west")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerTile newShipTile = new PlayerTile("Ship", shipIdentifier);
					PlayerBoard[row][col - i] = newShipTile;
					newShip.locations[i]= newShipTile;

				}
			} else {
				System.out.println("ERROR: No Orientation given");
				return -3;
			}

			allShips[number - 1] = newShip;
			LiveShips++;
			return 1;

		}

		return err;

	}

	// hit()
	//
	// Method marks hits or misses on the PlayerBoard and ServerBoard
	// Requires a row, col coordinate
	//
	public void hit(int row, int col, String color) {

		if (row >= 0 && row < 10 && col >= 0 && col < 10) {

			PlayerTile P_Tile = PlayerBoard[row][col];
			ServerTile S_Tile = ServerBoard[row][col];

			// cannot hit the same spot twice by anyone else or the same player
			if (P_Tile.hit == false) {

				if (P_Tile.type == "Ship") {

					String hitMarker = P_Tile.playerBoardPrintable + "H_" + color;

					P_Tile.playerBoardPrintable = hitMarker;
					P_Tile.hit = true;

					S_Tile.serverBoardHitMarker = "H_" + color;
					S_Tile.hit = true;

					checkSunkenShip();

				} else if (P_Tile.type == "PowerUp") {

					P_Tile.playerBoardPrintable = "P_HIT_" + color;
					S_Tile.serverBoardHitMarker = "P_HIT_" + color;

				} else if (P_Tile.type == "Decoy") {

					P_Tile.playerBoardPrintable = "DH_" + color;
					S_Tile.serverBoardHitMarker = "DH_" + color;

				} else {

					P_Tile.playerBoardPrintable = "M_" + color;
					P_Tile.hit = true;
					S_Tile.serverBoardHitMarker = "M_" + color;
					S_Tile.hit = true;

				}
			}
		}
	}

	// //
	// //
	// // PowerUp/Special Ability Methods
	// //
	// //

	// crossShotPowerUp()
	//
	// Method performs calls to the hit method, 5 times, making a cross shape
	public void crossShotPowerUp(int row, int col, String color) {

		if (row >= 0 && row < 10 && col >= 0 && col < 10) {
			// note that since the hit function already check bounds of the coordinate,
			// some hit calls won't modify the board if they are found to be out of bounds.
			hit(row, col, color);
			hit(row + 1, col, color);
			hit(row - 1, col, color);
			hit(row, col + 1, color);
			hit(row, col - 1, color);

		}
	}

	// // sonarPowerUp()
	// //
	// // Method functions similarly to the cross shot, but reveals any ships in the
	// // selected area
	void sonarPowerUp(int row, int col) {

		sonarCheck(row + 1, col);
		sonarCheck(row - 1, col);
		sonarCheck(row, col + 1);
		sonarCheck(row, col - 1);
		sonarCheck(row, col);

	}

	// // sonarCheck
	// //
	// // helper function for sonarPowerUp() method
	void sonarCheck(int row, int col) {

		if (row >= 0 && row < 10 && col >= 0 && col < 10) {

			PlayerTile P_Tile = PlayerBoard[row][col];
			ServerTile S_Tile = ServerBoard[row][col];

			if (P_Tile.type != "None" && P_Tile.hit != true) {

				if (S_Tile.sonarCheck != true) {

					S_Tile.serverBoardHitMarker = "S ";
					S_Tile.sonarCheck = true;

				}
			}
		}
	}

	// // sonarClear()
	// //
	// // Clears the board of any revealed squares by sonar.
	// // Intended to be called after a certain amount of turns
	void sonarClear() {

		for (int i = 0; i < 10; i++) {

			for (int j = 0; j < 10; j++) {
				ServerTile S_Tile = ServerBoard[i][j];

				if (S_Tile.sonarCheck == true) {

					S_Tile.sonarCheck = false;
					S_Tile.serverBoardHitMarker = ". ";
				}
			}
		}
	}

	// nukePowerUp()
	//
	// Calls the hit function on every square of the board.
	public void nukePowerUp(String color) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				hit(i, j, color);
			}
		}
	}


	// hideDecoy()
	//
	// Hides a decoy on the board that can be hit like any other ship
	void hideDecoy(int row, int col) {

		if (row >= 0 && row < 10 && col >= 0 && col < 10) {

			PlayerTile DecoyTile = PlayerBoard[row][col];

			if (DecoyTile.type == "None") {

				DecoyTile.type = "Decoy";
				DecoyTile.playerBoardPrintable = "D ";
			}
		}
	}


	// //
	// //
	// // Other Methods
	// //
	// //

	// // checkSunkenShip()
	// //
	// // checks every shipInfo object in allShips for if an entire ship is sunk
	public void checkSunkenShip() {
		for (ShipInfo ship : allShips) {
			if (ship != null) {
				int hitCounter = 0;
				for (PlayerTile loc : ship.locations) {

					if (loc != null) {
						if (loc.hit == true) {
							hitCounter++;
						}
					}


					// if entire ship is hit, mark status as dead
					if (hitCounter == ship.size) {
						ship.status = "dead";
					}
				}
			}
		}
		checkPlayerLife();
	}

	// // checkPlayerLife()
	// //
	// // Function checks if a player is still alive based on how many ships are live
	public void checkPlayerLife() {
		int liveShipCount = 0;
		for (ShipInfo s : allShips) {
			if (s != null) {
				if (s.status.equals("live")) {
					liveShipCount++;
				}
			}
		}

		if (liveShipCount > 0) {
			return;
			// 		// Still alive
		}

		setPlayerLife(false);
		System.out.println("DEBUG: Player Dead\n");
		// 	// No more ships remain, Player is dead
	}

	// // getPlayerLife()
	// //
	// // returns the playerLife boolean
	public boolean getPlayerLife() {
		return playerLife;
	}

	// // setPlayerLife()
	// //
	// // sets playerLife to new boolean value
	public void setPlayerLife(boolean playerLife) {
		this.playerLife = playerLife;
	}


	// checkBounds
	//
	// Method checks if the coordinate position exists on the board, and
	// if the entire ship fits within the board considering its orientation
	// Method will return 1 if ship is in bounds, -1 if out of bounds,
	// and -2 if the ship will collide with another ship
	public int checkBounds(int row, int col, int size, String orientation) {

		if (Objects.equals(orientation, "north")) {
			for (int i = 0; i < size; i++) {
				if ((row - i) < 0) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if (PlayerBoard[row - i][col].type.equals("Ship")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		} else if (Objects.equals(orientation, "south")) {
			for (int i = 0; i < size; i++) {
				if ((row + i) >= 10) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if (PlayerBoard[row + i][col].type.equals("Ship")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		} else if (Objects.equals(orientation, "east")) {
			for (int i = 0; i < size; i++) {
				if ((col + i) >= 10) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if (PlayerBoard[row][col + i].type.equals("Ship")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		} else if (Objects.equals(orientation, "west")) {
			for (int i = 0; i < size; i++) {
				if ((col - i) < 0) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if (PlayerBoard[row][col - i].type.equals("Ship")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		}

		return 1;
	}

	// printBoard()
	//
	// Method prints the current state of PlayerBoard and ServerBoard
	void printBoard() {
		System.out.println("PlayerBoard:");
		for (PlayerTile[] row : PlayerBoard) {
			for (PlayerTile column : row) {
				System.out.print(column.playerBoardPrintable + " ");
			}
			System.out.println();
		}

		System.out.println();

		System.out.println("ServerBoard:");
		for (ServerTile[] row : ServerBoard) {
			for (ServerTile column : row) {
				System.out.print(column.serverBoardHitMarker + " ");
			}
			System.out.println();
		}

		System.out.println();
	}


	static void debugPrintTile(PlayerTile t) {
		System.out.println("Debug PlayerTile");
		System.out.println("Type: " + t.type);
		System.out.println("ID: " + t.ID);
		System.out.println("Hit: " + t.hit);
		System.out.println("PlayerBoardMarker: " + t.playerBoardPrintable);
		System.out.println();
	}

	static void debugPrintTile2(ServerTile t) {
		System.out.println("Debug ServerTile");
		System.out.println("Hit: " + t.hit);
		System.out.println("HitMarker: " + t.serverBoardHitMarker);
		System.out.println("SonarCheck: " + t.sonarCheck);
		System.out.println();
	}

	//
	// main function for testing
	//
	public static void main(String[] args) {

		GameBoard2 x = new GameBoard2();

		x.printBoard();
		x.AddShip(0, 0, 5, "south", 1);
		x.printBoard();
		debugPrintTile(x.PlayerBoard[0][0]);
		debugPrintTile(x.allShips[0].locations[0]);

		x.hit(0, 0, "Green");
		x.printBoard();
		debugPrintTile(x.PlayerBoard[0][0]);
		debugPrintTile2(x.ServerBoard[0][0]);
		debugPrintTile(x.allShips[0].locations[0]);
		debugPrintTile(x.allShips[0].locations[1]);

		System.out.println("Ship Status: " + x.allShips[0].status + "\n");

		x.hit(1, 0, "Green");
		x.hit(2, 0, "Green");
		x.hit(3, 0, "Green");
		x.hit(4, 0, "Green");


		x.hit(4, 4, "Green");
		x.printBoard();
		debugPrintTile(x.PlayerBoard[4][4]);
		debugPrintTile2(x.ServerBoard[4][4]);
		System.out.println("Ship Status: " + x.allShips[0].status + "\n");

		x.hideDecoy(8, 8);
		x.printBoard();
		debugPrintTile(x.PlayerBoard[8][8]);

		x.sonarPowerUp(7, 8);
		x.printBoard();
		debugPrintTile2(x.ServerBoard[0][1]);
		debugPrintTile2(x.ServerBoard[0][0]);
		x.sonarClear();
		x.printBoard();

		x.nukePowerUp("Red");
		x.printBoard();

		x = new GameBoard2();
		System.out.println("\nNEW BOARD\n");

		x.AddShip(0, 0, 5, "south", 1);
		x.printBoard();
		x.AddShip(3, 1, 3, "west", 2);
		x.printBoard();
		x.AddShip(5, 4, 4, "east", 2);
		x.printBoard();
		x.AddShip(7, 8, 4, "west", 3);
		x.printBoard();
		x.AddShip(9, 9, 2, "north", 4);
		x.printBoard();
		x.AddShip(0, 9, 3, "south", 5);
		x.printBoard();
		
		x.hidePowerUps();
		x.printBoard();

		x.crossShotPowerUp(8,8, "Green");
		x.printBoard();

		x.sonarPowerUp(7, 7);
		x.printBoard();
		x.sonarClear();
		x.printBoard();

		x.hideDecoy(9, 2);
		x.printBoard();

		x.hit(9, 2, "Blue");
		x.printBoard();

		x.nukePowerUp("Blue");
		x.printBoard();

	}
}
