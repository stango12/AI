/* Author: Felix Wang */
/*
The world is below.

. . O O S
. O . . .
S . O . O
O O O . .
. . L . .

S is where pallets should be stacked.
L is the loading dock.
O is an obstacle.
*/

public class MondayMorning implements World {
  public void initialize() {
  }

  public boolean hasForklift() {
    return false;
  }

  public boolean atLoading(int row, int col) {
    return row == 4 && col == 2;
  }

  public int atStacking(int row, int col) {
    if (row == 2 && col == 0) {
      return 1;
    }
    if (row == 0 && col == 4) {
      return 2;
    }
    return 0;
  }

  public int getForkliftCol() {
    System.out.println("Error: This world does not have forklift.");
    System.exit(0);

    return -1;
  }

  public int getNumberOfRows() {
    return numOfRows;
  }

  public int getNumberOfCols() {
    return numOfCols;
  }

  public int getNumberOfStates() {
    return numOfRows * numOfCols * 2 * 2;
  }

  public int getInitialState() {
    return this.getState(4, 2, true, true);
  }

  public int getState(int robotRow, int robotCol, boolean hasP1, boolean hasP2) {
    int state = robotRow * this.getNumberOfCols() + robotCol;

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

  public int getState(int robotRow, int robotCol, int forkliftCol, boolean hasP1, boolean hasP2) {
    System.out.println("Error: This world does not have a forklift.");
    System.exit(0);

    return -1;
  }

  public void evolve() {
  }

  public double collideObstacle(int row, int col) {
    if ((row == 0 && col == 3) ||
    	(row == 1 && col == 1) ||
    	(row == 2 && col == 2) ||
    	(row == 3 && col == 2)) {
      return largeObstacle;
    }

    if ((row == 3 && col == 0) ||
    	(row == 3 && col == 1) ||
    	(row == 0 && col == 2) ||
    	(row == 2 && col == 4)) {
      return smallObstacle;
    }

    return 0.0;
  }

  public boolean collideForklift(int row, int col) {
    return false;
  }

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Usage: java MondayMorning <robot_row> <robot_col> <has_pallet_1> <has_pallet_2>");
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

    boolean hasP1, hasP2;

    if (args[2].equals("y")) {
      hasP1 = true;
    } else if (args[2].equals("n")) {
      hasP1 = false;
    } else {
      System.out.println("Error: has_pallet_1 should be y or n");
      return;
    }

    if (args[3].equals("y")) {
      hasP2 = true;
    } else if (args[3].equals("n")) {
      hasP2 = false;
    } else {
      System.out.println("Error: has_pallet_2 should be y or n");
      return;
    }

    System.out.println("The state ID is " + (new MondayMorning()).getState(robotRow, robotCol, hasP1, hasP2));
  }

  private static final int numOfRows = 5;
  private static final int numOfCols = 5;
  private static final double largeObstacle = 0.5;
  private static final double smallObstacle = 0.1;
}
