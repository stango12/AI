import java.util.Random;


public class QLearningAgent implements Agent
{
	private int numStates, numActions;
	private double [][] q;
	private int[] actions;
	
	private final double DISCOUNT = 0.9;
	private final double RATE = 0.1;
	private final double EXPLORATION = 0.05;
	private Random rand;
	
	public QLearningAgent()
	{
		rand = new Random();
	}
	
	@Override
	public void initialize(int numOfStates, int numOfActions) {
		numStates = numOfStates;
		numActions = numOfActions;
		q = new double[numStates][numActions];
		actions = new int[numStates];
		for(int i = 0; i < numStates; i++)
			for(int j = 0; j < numActions; j++)
				q[i][j] = 0;
		
		for(int i = 0; i < numStates; i++)
			actions[i] = 0;
	}

	@Override
	public int chooseAction(int state) {
		/*
		 * Pick a number
		 * 	less than exploration
		 * 		move randomly
		 * 	else act optimally	
		 */
		if(rand.nextDouble() < EXPLORATION)
		{
			int randAction = rand.nextInt(numActions);
			actions[state] = randAction;
			return randAction;
		}
		else
		{
			double bestQ = q[state][0];
			int bestAction = 0;
			for(int i = 0; i < numActions; i++)
			{
				if(bestQ < q[state][i])
				{
					bestQ = q[state][i];
					bestAction = i;
				}
			}
			actions[state] = bestAction;
			return bestAction;
		}
	}

	@Override
	public void updatePolicy(double reward, int action, int oldState,
			int newState) {
		//Q(a,s) <- Q(a,s) + alpha * (R(s) + gamma(maxA(a', s') - Q(a,s))
		double bestQ = q[newState][0];
		for(int i = 0; i < numActions; i++)
		{
			double currentQ = q[newState][i];
			if(bestQ < currentQ)
			{
				bestQ = currentQ;
				actions[newState] = i;
			}
		}
		q[oldState][action] += RATE * (reward + DISCOUNT * bestQ - q[oldState][action]);
	}

	@Override
	public Policy getPolicy() {
		// TODO Auto-generated method stub
		return new Policy(actions);
	}

}
