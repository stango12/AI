/* Author: Mingcheng Chen */

import java.io.PrintWriter;

public class Policy {
  public Policy(int[] actions) {
    this.actions = new int[actions.length];
    for (int i = 0; i < actions.length; i++) {
      this.actions[i] = actions[i];
    }
  }

  public void save(String filename) {
    try {
    PrintWriter writer = new PrintWriter(filename);
    for (int i = 0; i < this.actions.length; i++) {
      writer.println(this.actions[i]);
    }
    writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int[] actions;
}
