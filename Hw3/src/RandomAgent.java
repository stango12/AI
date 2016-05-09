/* Author: Mingcheng Chen
 * Editor: Felix Wang
 * */

import java.util.Random;

public class RandomAgent implements Agent {
  public RandomAgent() {
    this.rand = new Random();
  }

  public void initialize(int numOfStates, int numOfActions) {
    this.numOfStates = numOfStates;
    this.numOfActions = numOfActions;
  }

  public int chooseAction(int state) {
    // Do not know what to do. Let's toss a coin.
    return rand.nextInt(this.numOfActions);
  }

  public void updatePolicy(double reward, int action, int oldState, int newState) {
    // What is reward?
    // What is action?
    // What is oldState?
    // What is newState?
    return;
  }

  public Policy getPolicy() {
    int[] actions = new int[this.numOfStates];
    // Let's toss lots of coins.
    for (int i = 0; i < this.numOfStates; i++) {
      actions[i] = rand.nextInt(this.numOfActions);
    }

    return new Policy(actions);
  }

  private Random rand;
  private int numOfStates;
  private int numOfActions;
}
