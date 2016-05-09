/* Author: Mingcheng Chen 
 * Editor: Felix Wang
 * */

public interface World {
  public void initialize();
  public boolean hasForklift();
  public boolean atLoading(int row, int col);
  public int atStacking(int row, int col);  // 0: no stack, 1: stack 1, 2: stack 2
  public int getForkliftCol();
  public int getNumberOfRows();
  public int getNumberOfCols();
  public int getNumberOfStates();
  public int getInitialState();
  public int getState(int robotRow, int robotCol, boolean hasP1, boolean hasP2);
  public int getState(int robotRow, int robotCol, int forkliftCol, boolean hasP1, boolean hasP2);
  public void evolve();
  public double collideObstacle(int row, int col);
  public boolean collideForklift(int row, int col);
}
