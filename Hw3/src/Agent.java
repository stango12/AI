/* Author: Mingcheng Chen */

public interface Agent {
  public void initialize(int numOfStates, int numOfActions);
  public int chooseAction(int state);
  public void updatePolicy(double reward, int action,
                           int oldState, int newState);
  public Policy getPolicy();
}
