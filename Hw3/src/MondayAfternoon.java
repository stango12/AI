/* Author: Felix Wang */
/*
The world is below.

S . . O S
. . O . .
F<->F<->F
O O . . .
. . L . .

S is where pallets should be stacked.
L is the loading dock.
O is an obstacle.
F (middle row) is where a forklift travels.
*/

import java.util.Random;

public class MondayAfternoon implements World {
  public MondayAfternoon() {
    this.rand = new Random();
  }

  public void initialize() {
    this.forkliftCol = rand.nextInt(numOfCols);
  }

  public boolean hasForklift() {
    return true;
  }

  public boolean atLoading(int row, int col) {
    return row == this.getNumberOfRows() - 1 && col == 2;
  }

  public int atStacking(int row, int col) {
    if (row == 0 && col == 0) {
      return 1;
    }
    if (row == 0 && col == this.getNumberOfCols() - 1) {
      return 2;
    }
    return 0;
  }

  public int getForkliftCol() {
    return this.forkliftCol;
  }

  public int getNumberOfRows() {
    return numOfRows;
  }

  public int getNumberOfCols() {
    return numOfCols;
  }

  public int getNumberOfStates() {
    return numOfRows * numOfCols * numOfCols * 2 * 2;
  }

  public int getInitialState() {  // called immediately after initialize()
    return this.getState(this.getNumberOfRows() - 1, 2, this.forkliftCol, true, true);
  }

  public int getState(int robotRow, int robotCol, boolean hasP1, boolean hasP2) {
    System.out.println("Error: This world has a forklift.");
    System.exit(0);

    return -1;
  }

  public int getState(int robotRow, int robotCol, int thiefRow, boolean hasP1, boolean hasP2) {
    int state = (robotRow * this.getNumberOfCols() + robotCol) * this.getNumberOfRows() + thiefRow;

    if (hasP1) {
      state = state * 2 + 1;
    } else {
      state = state * 2;
    }

    if (hasP2) {
      state = state * 2 + 1;
    } else {
      state = state * 2;
    }

    return state;
  }

  public void evolve() {
    int forkliftMove = (this.rand.nextInt(7));

    if (forkliftCol > 0 && forkliftCol + 1 < numOfCols) {
    	if (forkliftMove < 3) {
    		forkliftCol--;
    	} else if (forkliftMove > 3) {
    		forkliftCol++;
    	}
    } else if (forkliftCol == 0) {
    	if (forkliftMove > 2) {
    		forkliftCol++;
    	}
    } else if (forkliftCol + 1 == numOfCols) {
    	if (forkliftMove > 2) {
    		forkliftCol--;
    	}
    }
  }

  public double collideObstacle(int row, int col) {
    if ((row == 0 && col == 3) ||
    	(row == 1 && col == 2)) {
      return largeObstacle;
    }

    if ((row == 3 && col == 1) ||
    	(row == 3 && col == 0)) {
      return smallObstacle;
    }

    return 0.0;
  }

  public boolean collideForklift(int row, int col) {
    return row == forkliftRow && col == forkliftCol;
  }

  public static void main(String[] args) {
    if (args.length != 5) {
      System.out.println("Usage: java MondayAfternoon <robot_row> <robot_col> <forklift_col> <has_pallet_1> <has_pallet_2>");
      return;
    }

    int robotRow, robotCol;

    try {
      robotRow = Integer.parseInt(args[0]);

      if (robotRow < 0 || robotRow >= numOfRows) {
        System.out.println("Error: robot_row is out of range");
        return;
      }
    } catch (Exception e) {
      System.out.println("Error: invalid robot_row");
      return;
    }

    try {
      robotCol = Integer.parseInt(args[1]);

      if (robotCol < 0 || robotCol >= numOfCols) {
        System.out.println("Error: robot_col is out of range");
        return;
      }
    } catch (Exception e) {
      System.out.println("Error: invalid robot_col");
      return;
    }

    int forkliftCol = -1;
	try {
		forkliftCol = Integer.parseInt(args[2]);
	
		if (forkliftCol < 0 || forkliftCol >= numOfRows) {
			System.out.println("Error: forklift_col is out of range");
			return;
		}
	} catch (Exception e) {
		System.out.println("Error: invalid forklift_col");
		return;
	}
	  
    boolean hasP1, hasP2;

    if (args[3].equals("y")) {
      hasP1 = true;
    } else if (args[3].equals("n")) {
      hasP1 = false;
    } else {
      System.out.println("Error: has_pallet_1 should be y or n");
      return;
    }

    if (args[4].equals("y")) {
      hasP2 = true;
    } else if (args[4].equals("n")) {
      hasP2 = false;
    } else {
      System.out.println("Error: has_pallet_2 should be y or n");
      return;
    }

    System.out.println("The state ID with is " + (new MondayAfternoon()).getState(robotRow, robotCol, forkliftCol, hasP1, hasP2));
  }

  private Random rand;
  private int forkliftCol;

  private static final int forkliftRow = 2;
  private static final int numOfRows = 5;
  private static final int numOfCols = 5;
  private static final double largeObstacle = 0.5;
  private static final double smallObstacle = 0.1;
}
