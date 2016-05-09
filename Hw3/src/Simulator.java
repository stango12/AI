/* Author: Mingcheng Chen 
 * Editor: Felix Wang
 * */

import java.util.Random;
import java.util.ArrayList;
import java.io.PrintWriter;

public class Simulator {
  public static void main(String[] args) {
    if (args.length != 6) {
      System.out.println("Usage: java Simulator <world> <agent> <steps> <episodes> <policy_output> <episode_output>");
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

    Agent agent;

    try {
      Class c = Class.forName(args[1]);
      agent = (Agent)c.newInstance();
    } catch (Exception e) {
      System.out.println("Error: invalid agent class");
      return;
    }

    int steps;

    try {
      steps = Integer.parseInt(args[2]);
    } catch (Exception e) {
      System.out.println("Error: invalid steps");
      return;
    }

    int episodes;

    try {
      episodes = Integer.parseInt(args[3]);
    } catch (Exception e) {
      System.out.println("Error: invalid episodes");
      return;
    }

    String policyOutput = args[4];
    String episodeOutput = args[5];

    (new Simulator(world, agent, steps, episodes, policyOutput, episodeOutput)).simulate();
  }

  public Simulator(World world, Agent agent, int steps, int episodes, String policyOutput, String episodeOutput) {
    this.world = world;
    this.agent = agent;
    this.steps = steps;
    this.episodes = episodes;
    this.policyOutput = policyOutput;
    this.episodeOutput = episodeOutput;
    this.rand = new Random(System.currentTimeMillis());
  }

  public void simulate() {
    this.agent.initialize(this.world.getNumberOfStates(), numOfActions);

    ArrayList<Double> episodeList = new ArrayList<Double>();

    for (int episode = 0; episode < this.episodes; episode++) {
      this.world.initialize();

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

      for (int step = 0; step < this.steps; step++) {
        int action = this.agent.chooseAction(currState);

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

          if (!hasP1 && !hasP2) {  // successful delivery
            reward += rewardByStacking;
          }
        }

        totalReward += reward;
        int newState = this.world.hasForklift() ? this.world.getState(robotRow, robotCol, forkliftCol, hasP1, hasP2) :
                                                 this.world.getState(robotRow, robotCol, hasP1, hasP2);

        this.agent.updatePolicy(reward, action, currState, newState);

        currState = newState;
      }

      System.out.println("Episode " + (episode + 1) + ": reward = " + totalReward);
      episodeList.add(totalReward);
    }

    this.agent.getPolicy().save(this.policyOutput);

    try {
      PrintWriter writer = new PrintWriter(this.episodeOutput);
      for (int i = 0; i < episodeList.size(); i++) {
        writer.println(episodeList.get(i));
      }
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static final int numOfActions = 5;
  private static final int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {0, 0}};  // NSEW
  private static final double lossByLockup = 1.0;
  private static final double rewardByStacking = 1.0;

  private World world;
  private Agent agent;
  private int steps;
  private int episodes;
  private String policyOutput;
  private String episodeOutput;

  private Random rand;
}
