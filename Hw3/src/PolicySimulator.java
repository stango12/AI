/* Author: Mingcheng Chen
 * Editor: Felix Wang
 * */

import java.util.Random;
import java.util.ArrayList;
import java.io.*;
import java.io.PrintWriter;
import java.util.Scanner;

public class PolicySimulator {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Usage: java PolicySimulator <world> <policy_file> <steps>");
      return;
    }

    World world;

    try {
      Class c = Class.forName(args[0]);
      world = (World)c.newInstance();
    } catch (Exception e) {
      System.out.println("Error: invalid world class");
      return;
    }

    String policyFile = args[1];

    int steps;

    try {
      steps = Integer.parseInt(args[2]);
    } catch (Exception e) {
      System.out.println("Error: invalid steps");
      return;
    }

    (new PolicySimulator(world, policyFile, steps)).simulate();
  }

  public PolicySimulator(World world, String policyFile, int steps) {
    this.world = world;
    this.policyFile = policyFile;
    this.steps = steps;
    this.rand = new Random();
  }

  private int[] readPolicy() {
    int[] actions = new int[this.world.getNumberOfStates()];

    try {
      Scanner scanner = new Scanner(new BufferedReader(new FileReader(this.policyFile)));

      ArrayList<String> commands = new ArrayList<String>();
      while (scanner.hasNextLine()) {
        commands.add(scanner.nextLine());
      }

      if (commands.size() != actions.length) {
        System.out.println("Error: The policy file has different number of states.");
        System.exit(0);
      }

      for (int i = 0; i < actions.length; i++) {
        actions[i] = commands.get(i).charAt(0) - '0';
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return actions;
  }

  private void OutputState(int robotRow, int robotCol, boolean hasP1, boolean hasP2, double totalReward) {
    char[][] maze = new char[this.world.getNumberOfRows()][this.world.getNumberOfCols()];

    for (int i = 0; i < this.world.getNumberOfRows(); i++) {
      for (int j = 0; j < this.world.getNumberOfCols(); j++) {
        if (this.world.collideForklift(i, j)) {
          maze[i][j] = 'F';
          continue;
        }
        if (this.world.collideObstacle(i, j) > 0.0) {
          maze[i][j] = 'O';
          continue;
        }
        if (this.world.atStacking(i, j) > 0) {
          maze[i][j] = 'S';//(char)((int)'0' + this.world.hasCustomer(i, j));
          continue;
        }
        maze[i][j] = ' ';
      }
    }

    maze[this.world.getNumberOfRows() - 1][2] = 'L';
    maze[robotRow][robotCol] = 'R';

    for (int i = 0; i < maze.length; i++) {
      System.out.println(maze[i]);
    }
    if (hasP1 && hasP2) {
      System.out.println("Carrying both pallets");
    } else if (hasP1) {
      System.out.println("Only carrying one pallet");
    } else if (hasP2) {
      System.out.println("Only carrying one pallet");
    } else {
      System.out.println("Carrying nothing");
    }

    System.out.println("Total reward is " + totalReward + ".");
  }

  public void simulate() {
    this.world.initialize();

    int[] actions = this.readPolicy();

    int initialState = this.world.getInitialState();

    int robotRow = this.world.getNumberOfRows() - 1;
    int robotCol = 2;

    int forkliftCol = -1;

    if (this.world.hasForklift()) {
    	forkliftCol = this.world.getForkliftCol();
    }

    boolean hasP1 = true, hasP2 = true;

    int currState = initialState;

    double totalReward = 0.0;

    this.OutputState(robotRow, robotCol, hasP1, hasP2, totalReward);

    Scanner scanner = new Scanner(System.in);

    for (int step = 0; step < this.steps; step++) {
      scanner.nextLine();

      int action = actions[currState];

      int nextRobotRow = robotRow + directions[action][0];
      int nextRobotCol = robotCol + directions[action][1];
      
      // Test for wall
      if (nextRobotRow < 0 || nextRobotCol < 0 || nextRobotRow >= this.world.getNumberOfRows() ||
                                                  nextRobotCol >= this.world.getNumberOfCols()) {
        nextRobotRow = robotRow;
        nextRobotCol = robotCol;
      }

      // update position
      robotRow = nextRobotRow;
      robotCol = nextRobotCol;
      
      double reward = 0.0;
      
      // Test for obstacles
      if (this.world.collideObstacle(nextRobotRow, nextRobotCol) > 0.0) {
      	if (rand.nextDouble() <= this.world.collideObstacle(nextRobotRow, nextRobotCol)) {
              reward -= lossByLockup;
          }
      }

      // Move forklift
      this.world.evolve();
      
      // Test for forklift
      if (this.world.hasForklift()) {
    	  forkliftCol = this.world.getForkliftCol();
      }
      if (this.world.collideForklift(robotRow, robotCol)) {
          reward -= lossByLockup;
          System.out.println("Ran into Forklift");
      }

      if (!hasP1 && !hasP2) {  // has nothing (needs to go back to the loading dock)
        if (this.world.atLoading(robotRow, robotCol)) {  // load new pallets
          hasP1 = true;
          hasP2 = true;
        }
      } else {  // has at least one pallet (successfully delivered one)
        if (hasP1 && this.world.atStacking(robotRow, robotCol) == 1) {
          hasP1 = false;
        }
        if (hasP2 && this.world.atStacking(robotRow, robotCol) == 2) {
          hasP2 = false;
        }

        if (!hasP1 && !hasP2) {  // successful stacking
          reward += rewardByStacking;
        }
      }

      totalReward += reward;
      int newState = this.world.hasForklift() ? this.world.getState(robotRow, robotCol, forkliftCol, hasP1, hasP2) :
                                               this.world.getState(robotRow, robotCol, hasP1, hasP2);

      currState = newState;

      this.OutputState(robotRow, robotCol, hasP1, hasP2, totalReward);
    }
  }

  private static final int numOfActions = 5;
  private static final int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {0, 0}};  // NSEW
  private static final double lossByLockup = 0.5;
  private static final double rewardByStacking = 1.0;

  private World world;
  private int steps;
  private String policyFile;

  private Random rand;
}
