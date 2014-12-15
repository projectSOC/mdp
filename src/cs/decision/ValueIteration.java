/*
 * Created by Roy
 *
 */
package cs.decision;
import java.util.*;

/**
 * Value iteration algorithm to solve a markov decision process.
 *  
 */
public class ValueIteration {

	MarkovDecisionProcess mdp;
	
	/**
	 * The error threshold to stop the iteration 
	 */
	double epsilon = 1e-4;
	
	int numIterations;
	
	public ValueIteration(MarkovDecisionProcess mdp){
		this.mdp = mdp;
	}
	
	/**
	 * Set the stop threhold.
	 * @param epsilon The error threshold.
	 */
	public void setError(double epsilon){
		this.epsilon = epsilon;
	}
	
	public int getNumberIterations() {
		return numIterations;
	}
	
	public int solve() {
		
		double gamma = mdp.getGamma();
		double threshold;
		if(gamma == 1)
			threshold = epsilon;
		else
			threshold = epsilon*(1.-gamma)/gamma;
		
		boolean finished = false;
		
		numIterations = 0;
		while(!finished) {
			double maxError = -1.;
			
			for(State state=mdp.getStartState();state!=null;state=mdp.getNextState()) {
			
				double utility = mdp.getUtility(state);
				double reward = mdp.getReward(state);

				double maxCurrentUtil = -1e30;
				Action maxAction = null;
				
				///System.out.println("utility:" + utility);
				
				///System.out.println("reward:" + reward);
				
				// The following while loop computes \max_a\sum T(s,a,s')U(s')
				for(Action action=mdp.getStartAction(); action!=null; 
						action=mdp.getNextAction()){

					Vector T = mdp.getTransition(state, action);
					int s = T.size();
					double nextUtil = 0;
					for(int i=0; i<s; ++i) {
						Transition t=(Transition)T.get(i);
						double prob=t.probability;
						State sPrime=t.nextState;
						nextUtil += (prob * mdp.getUtility(sPrime));
					}

					if(nextUtil > maxCurrentUtil){
						maxCurrentUtil = nextUtil;
						maxAction = action;
					}
					
				}
				
				maxCurrentUtil=reward+gamma*maxCurrentUtil;
				///maxCurrentUtil=-0.04+gamma*maxCurrentUtil;
				mdp.setUtility(state, maxCurrentUtil);
				mdp.setAction(state, maxAction);
				
				double currentError=Math.abs(maxCurrentUtil-utility);
				if(currentError>maxError)
					maxError = currentError;
							
			}
			
			numIterations ++;
			if(maxError < threshold)
				finished = true;

			//System.out.println("Iteration: "+numIterations + " error:"+maxError);
			
		}
		
		return numIterations;
	}
}
