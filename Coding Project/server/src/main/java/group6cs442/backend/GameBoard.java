package group6cs442.backend;


import java.util.*;

// GameBoard Class
// ShipBoard has all the players ships and information of the game
// ShipBoard uses string: ". " = Empty
//						  "B#" = Ship where # is the number
//						  "B#H" = Ship Hit
//						  "M" = Miss
//
// ServerBoard gives information of boards to other players
// ServerBoard uses strings: "." = Empty
//							 "H" = Hit
//						 	 "M" = Miss

//@JsonSerialize(using = GameBoardSerializer.class)
public class GameBoard {

	class ShipInfo {
		String shipName = "";
		String status = "live";
		int size = 0;
		int[][] locations = new int[5][2];
	}


	String[][]PlayerBoard;
	String[][]ServerBoard;
	int LiveShips = 0;
	ShipInfo[] allShips = new ShipInfo[5];
	public boolean playerLife = true;
	public int points = 1000;
	// Give the user an inventory of powerups
	List<Map<String, Integer>> powerUpInventory = new ArrayList<>();
	public String selectedPowerUp = "Default";


	public void purchasePowerup(String powerup, int cost) {
		if (losePoints(cost)) {
			boolean found = false;
			for (Map<String, Integer> map : powerUpInventory) {
				if (map.containsKey(powerup)) {
					map.put(powerup, map.get(powerup) + 1);
					found = true;
					break;
				}
			}
			if (!found) {
				Map<String, Integer> powerupMap = new HashMap<>();
				powerupMap.put(powerup, 1);
				powerUpInventory.add(powerupMap);
			}
		}
	}

	public void removePowerup(String powerup) {
		for (Map<String, Integer> map : powerUpInventory) {
			if (map.containsKey(powerup)) {
				int quantity = map.get(powerup);
				if (quantity > 1) {
					map.put(powerup, quantity - 1);
				} else {
					powerUpInventory.remove(map);
				}
				break;
			}
		}
	}

	public void usePowerup(String powerup, int row, int col) {
		if (powerup.equals("crossShot")) {
			crossShotPowerUp(row, col, "green");
		} else if (powerup.equals("sonar")) {
			sonarPowerUp(row, col);
		} else if (powerup.equals("nuke")) {
			nukePowerUp("green");
		}
	}

	public void setSelectedPowerUp(String powerUp) {
				selectedPowerUp = powerUp;
	}

	public String getSelectedPowerUp() {
		return selectedPowerUp;
	}

	public List<Map<String, Integer>> getPowerUpInventory() {
		return powerUpInventory;
	}


	public String[][] getPlayerBoard() {
		return PlayerBoard;
	}


	// GameBoard()
	//
	// Default constructor. Initializes PlayerBoard and ServerBoard
	//		to 10x10 2D array with "." strings to make an empty board.
	public GameBoard() {
		PlayerBoard = new String[10][10];
		ServerBoard = new String[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				PlayerBoard[i][j] = ". ";
				ServerBoard[i][j] = ". ";
			}
		}
	}

	public void setPlayerBoard(String[][] playerBoard) {
		PlayerBoard = playerBoard;
	}

	public String[][] getServerBoard() {
		return ServerBoard;
	}

	public void incrementPoints() {
		points = points + 1;
	}

	public void hitPointCell() {
		points = points + 10;
	}

	public boolean losePoints(int cost){

		if( points > cost){
			points = points - cost;
			return true;
		}
		else{
			System.out.println("Player does not have enough points to make this purchase.");
			return false;
		}
	}
	
	// call this after all ships are placed via rest api?
	public void hidePowerUps() {
		int powerUpCount = 0;
		int row = 0;
		int col = 0;
		while (powerUpCount != 5) {
			row = (int)(Math.random() * 10);
			col = (int)(Math.random() * 10);
			
			if (PlayerBoard[row][col] == ". ") {
				PlayerBoard[row][col] = "P";
				powerUpCount++;
			}
		}
	}
	


	// AddShip()
	//
	// Method to add ships to the board, given an row,col coordinate,
	// 		the size of the ship, orientation of the ship, and a number identifier.
	//		Method also calls the checkBounds method to see if the entire ship fits
	//		in the bounds of the board.
	public int AddShip(int row, int col, int shipSize, String orientation, int number) {
		int err = checkBounds(row, col, shipSize, orientation);
		String shipIdentifier = "B" + number;

		if (err == 1) {

			ShipInfo newShip = new ShipInfo();
			newShip.shipName = shipIdentifier;
			newShip.size = shipSize;

			if (Objects.equals(orientation, "north")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerBoard[row-i][col] = shipIdentifier;
					newShip.locations[i][0] = row-i;
					newShip.locations[i][1] = col;

				}
			} else if (Objects.equals(orientation, "south")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerBoard[row+i][col] = shipIdentifier;
					newShip.locations[i][0] = row+i;
					newShip.locations[i][1] = col;

				}
			} else if (Objects.equals(orientation, "east")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerBoard[row][col+i] = shipIdentifier;
					newShip.locations[i][0] = row;
					newShip.locations[i][1] = col+i;

				}
			} else if (Objects.equals(orientation, "west")) {

				for (int i = 0; i < shipSize; i++) {
					PlayerBoard[row][col-i] = shipIdentifier;
					newShip.locations[i][0] = row;
					newShip.locations[i][1] = col-i;

				}
			} else {
				System.out.println("ERROR: No Orientation given");
				return -3;
			}

			allShips[number-1] = newShip;
			LiveShips++;
			return 1;

		}

		return err;

	}


	// hit()
	//
	//
	// Method marks hits or misses on the PlayerBoard and ServerBoard
	//	Requires a row, col coordinate
	//	Method also makes calls to the checkSunkenShup() method
	//		to check for any sunken ships
	//
	public void hit(int row, int col, String color) {
		if (row >= 0 && row < 10 && col >= 0 && col < 10) {
			String cell = PlayerBoard[row][col];
			// cannot hit the same spot twice by anyone else or the same player
			if (!cell.contains("H_") && !cell.contains("M_")) {
				if (cell.contains("B")) {
					String hitMarker = cell + "H_" + color;
					PlayerBoard[row][col] = hitMarker;
					ServerBoard[row][col] = "H_" + color;
					checkSunkenShip();
				} else if (cell.contains("P")) {
					PlayerBoard[row][col] = "P_HIT_" + color;
					ServerBoard[row][col] = "P_HIT_" + color;
				} else if (cell.contains("D")) {
					PlayerBoard[row][col] = "DH_" + color;
					ServerBoard[row][col] = "DH_" + color;
				}
				else {
					PlayerBoard[row][col] = "M_" + color;
					ServerBoard[row][col] = "M_" + color;
				}
			}
		}
	}
	
	
	//
	//
	// PowerUp/Special Ability Methods
	//
	//

	
	// crossShotPowerUp()
	//
	// Method performs calls to the hit method, 5 times, making a cross shape
	public void crossShotPowerUp(int row, int col, String color) {
		if (row >= 0 && row < 10 && col >= 0 && col < 10) {
			// note that since the hit function already check bounds of the coordinate,
			//  some hit calls won't modify the board if they are found to be out of bounds.
			hit(row, col, color);
			hit(row+1, col, color);
			hit(row-1, col, color);
			hit(row, col+1, color);
			hit(row, col-1, color);
		}
	}
	
	// sonarPowerUp()
	//
	// Method functions similarly to the cross shot, but reveals any ships in the selected area
	void sonarPowerUp(int row, int col) {
		sonarCheck(row+1, col);
		sonarCheck(row-1, col);
		sonarCheck(row, col+1);
		sonarCheck(row, col-1);
		sonarCheck(row, col);
		
	}
	
	// sonarCheck
	//
	// helper function for sonarPowerUp() method
	void sonarCheck(int row, int col) {
		if (row >= 0 && row < 10 && col >= 0 && col < 10) {
			if (PlayerBoard[row][col] != ". ") {
				if (ServerBoard[row][col].equals(". ")) {
					ServerBoard[row][col] = "S ";
				}
				
			}
		}
	}
	
	// sonarClear()
	//
	// Clears the board of any revealed squares by sonar.
	// Intended to be called after a certain amount of turns
	void sonarClear() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (ServerBoard[i][j].equals("S ")) {
					ServerBoard[i][j] = ". ";
				}
			}
		}
	}
	
	// nukePowerUp()
	//
	// Calls the hit function on every square of the board.
	public void nukePowerUp(String color) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j< 10; j++) {
				hit(i, j, color);
			}
		}
	}
	
	// hideDecoy()
	//
	// Hides a decoy on the board that can be hit like any other ship
	void hideDecoy(int row, int col) {
		if (row >= 0 && row < 10 && col >= 0 && col < 10) {
			if (PlayerBoard[row][col] == ". ") {
				PlayerBoard[row][col] = "D ";
			}
			
		}
	}
	
	
	//
	//
	// Other Methods 
	//
	//
	

	// checkSunkenShip()
	//
	// checks every shipInfo object in allShips for if an entire ship is sunk
	public void checkSunkenShip() {
		for (ShipInfo ship : allShips) {
			if (ship != null) {
				int hitCounter = 0;
				for (int i = 0; i < ship.size; i++) {
					if (ship.locations[i] != null) {
						String shipMarker = PlayerBoard[ship.locations[i][0]]
								[ship.locations[i][1]];
						// count if hit is marked
						if (!shipMarker.equals(ship.shipName)) {
							hitCounter++;
						}

						// if entire ship is hit, mark status as dead
						if (hitCounter == ship.size) {
							ship.status = "dead";
						}
					} 
				}
			}
		}
		checkPlayerLife();
	}


	// checkPlayerLife()
	//
	// Function checks if a player is still alive based on how many ships are live
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
			// Still alive
		}

		setPlayerLife(false);
		// No more ships remain, Player is dead
	}

	// getPlayerLife()
	//
	// returns the playerLife boolean
	public boolean getPlayerLife() {
		return playerLife;
	}


	// setPlayerLife()
	//
	// sets playerLife to new boolean value
	public void setPlayerLife(boolean playerLife) {
		this.playerLife = playerLife;
	}
	

	// checkBounds
	//
	// Method checks if the coordinate position exists on the board, and
	//		if the entire ship fits within the board considering its orientation
	//		Method will return 1 if ship is in bounds, -1 if out of bounds,
	//		and -2 if the ship will collide with another ship
	public int checkBounds(int row, int col, int size, String orientation) {

		if (Objects.equals(orientation, "north")) {
			for(int i = 0; i < size; i ++) {
				if ((row-i) < 0) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if(!PlayerBoard[row-i][col].equals(". ")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		} else if (Objects.equals(orientation, "south")) {
			for(int i = 0; i < size; i ++) {
				if ((row+i) >= 10) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if(!PlayerBoard[row+i][col].equals(". ")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		} else if (Objects.equals(orientation, "east")) {
			for(int i = 0; i < size; i ++) {
				if ((col+i) >= 10) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if(!PlayerBoard[row][col+i].equals(". ")) {
					System.out.println("ERROR: Ship Collision");
					return -2;
				}
			}
		} else if (Objects.equals(orientation, "west")) {
			for(int i = 0; i < size; i ++) {
				if ((col-i) < 0) {
					System.out.println("ERROR: Out of bounds");
					return -1;
				}
				if(!PlayerBoard[row][col-i].equals(". ")) {
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
		for (String[] row : PlayerBoard) {
			for (String column : row) {
				System.out.print(column + " ");
			}
			System.out.println();
		}

		System.out.println();

		System.out.println("ServerBoard:");
		for (String[] row : ServerBoard) {
			for (String column : row) {
				System.out.print(column + " ");
			}
			System.out.println();
		}

		System.out.println();
	}

	
	

	//
	// main function for testing
	//
	public static void main(String[] args) {

				GameBoard x = new GameBoard();
		
				x.printBoard();
		
				x.AddShip(0, 0, 5, "south", 1);
		
				x.printBoard();
		
				int y = x.checkBounds(4, 0, 5, "south");
		
				System.out.println(y);
		
				y = x.checkBounds(5, 0, 2, "east");
		
				System.out.println(y);
		
				y = x.checkBounds(5, 3, 4, "west");
		
				System.out.println(y);
		
				x.AddShip(5, 0, 2, "east", 2);
		
				x.AddShip(8, 9, 4, "west", 3);
		
				x.printBoard();
		//
		//		for (ShipInfo s : x.allShips) {
		//			if (s != null) {
		//				for (int i = 0; i < 5; i++) {
		//					if (s.locations[i] != null) {
		//						System.out.print(s.locations[i][0] + ", ");
		//						System.out.println(s.locations[i][1]);
		//					}
		//				}
		//				System.out.println();
		//			}
		//
		//		}
		//
				
//				x.hidePowerUps();
//				
				x.hit(0, 0, "blue");
//				x.hit(1, 0, "blue");
//				x.hit(2, 0, "blue");
//				x.hit(3, 0, "blue");
//				x.printBoard();
//				System.out.println(x.allShips[0].status);
//
//				x.crossShotPowerUp(7, 7, "blue");
//				x.printBoard();
//				x.crossShotPowerUp(0, 9, "blue");
//				x.printBoard();
//				
//				x.sonarPowerUp(7, 9);
//				x.printBoard();
//				x.sonarClear();
//				x.printBoard();
		//		
//				x.hit(4, 0, "blue");
//				x.printBoard();
//				System.out.println(x.allShips[0].status);

				x.hideDecoy(5, 5);
//				x.nukePowerUp("green");
				x.printBoard();
				x.hit(5, 5, "yellow");
				x.printBoard();
		//
		//		x.hit(8, 9);
		//		x.printBoard();


	}
}


