package group6cs442.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class BackEndApplicationTests {

	static GameBoard g;
	static GameBoard outOfBounds;
	static GameBoard edgeCases;
	static GameBoard edgeCases2;
	static GameBoard edgeCases3;
	
	@BeforeAll
	static void setup() {
		g = new GameBoard();
		outOfBounds = new GameBoard();
		edgeCases = new GameBoard();
		edgeCases2 = new GameBoard();
		edgeCases3 = new GameBoard();
	}

	//
	// Testing Add functions
	//

	@Test
	@Order(1)
	void addTest1() {
		int err = g.AddShip(0, 0, 5, "south", 1);
		int x = g.allShips[0].locations[0][0];
		int y = g.allShips[0].locations[0][1];

		assertEquals(1, err, "addShip not successful");
		assertEquals(0, x, "ship X-coord incorrect");
		assertEquals(0, y, "ship Y-coord incorrect");

	}

	@Test
	@Order(1)
	void addTest2() {
		int err = g.AddShip(0, 1, 5, "south", 2);

		assertEquals(1, err, "addShip not successful");


	}
	
	@Test
	@Order(1)
	void addTest3() {
		int err = g.AddShip(5, 4, 5, "west", 3);
		
		assertEquals(1, err, "addShip not successful");


	}


	//
	// Testing collisions in add function
	//

	@Test
	@Order(2)
	void collisionTest1() {
		int err = g.AddShip(0, 2, 2, "west", 999);

		assertEquals(-2, err, "ship collision not detected");

	}


	// Testing Out Of Bounds in add functions

	@Test
	@Order(3)
	void outOfBoundsTest1() {
		int err = 0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 4; j++) {
				
				err = outOfBounds.AddShip(i, j, 5, "west", i+1);
				assertEquals(-1, err, "out of bounds not detected");
			}
		}
	}

	@Test
	@Order(4)
	void edgeCaseTest1() {
		int err = 0;
		for (int i = 0; i < 5; i++) {
			err = edgeCases.AddShip(i, 4, 5, "west", i+1);
			System.out.println(err);
			edgeCases.printBoard();
			assertEquals(1, err, "edge case failure");

		}
		
	}
	@Test
	@Order(4)
	void edgeCaseTest2() {
		int err = 0;
		for (int i = 5; i < 10; i++) {
			err = edgeCases2.AddShip(i, 4, 5, "west", i-4);
			System.out.println(err);
			edgeCases2.printBoard();
			assertEquals(1, err, "edge case failure");

		}
		
	}
	
	@Test
	@Order(4)
	void edgeCaseTest3() {
		int err = 0;
		//for (int i = 5; i < 10; i++) {
			err = edgeCases3.AddShip(0, 0, 3, "east", 1);
			edgeCases3.AddShip(0, 3, 3, "east", 2);
			edgeCases3.AddShip(0, 6, 3, "east", 3);
			System.out.println(err);
			edgeCases3.printBoard();
			assertEquals(1, err, "edge case failure");

		//}
		
	}
	
}
