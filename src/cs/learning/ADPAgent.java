/*
 * Created by Roy
 *
 */
package cs.learning;

import cs.decision.*;

/**
 * A passive adaptive dynamic programming (ADP) agent.
 * 
 */
public class ADPAgent {
	
	MarkovDecisionProcess mdp;
	PolicyEvaluator pe;
	
	int mdpSize;
	int numActions;
	
	double[][] Nsa;
	double[][][] Nsasp;
	
	State prevState;
	Action prevAction;
	
	public ADPAgent(MarkovDecisionProcess mdp){
		
		// This is an empty mdp, without reward info
		this.mdp = mdp;
		pe = new PolicyEvaluator(mdp);
		
		prevState = null;
		prevAction = null;
		
		mdpSize = mdp.getReachableSize();
		numActions = mdp.getNumberActions();
		
		Nsa = new double[mdpSize][numActions];
		Nsasp = new double[mdpSize][numActions][numActions+1];
	}
	
	public Action go(Percept p){
		
		// Precondition:
		// There is already a policy in mdp
		
		//System.out.print("from state "+prevState);
		
		State currentState = p.state;
		double reward = p.reward;
		
		if(!currentState.isVisited()){
			mdp.setReward(currentState, reward);
			mdp.setUtility(currentState, reward);
			currentState.setVisited(true);
			
		}
		
		if(prevState != null) {
			incrementNsa(prevState, prevAction);
			incrementNsasp(prevState, prevAction, currentState);
			updateTransitionModel(prevState, prevAction);
			
		}
		
		// Update utility
		try {
			pe.solve();
		}catch (ArithmeticException e){
			//System.out.println(e);
			//prevState = null;
			//prevAction = null;
			//return null;
		}
		
		if(currentState.isTerminate()){
			prevState = null;
			prevAction = null;
		}else {
			prevState = currentState;
			prevAction = mdp.getAction(currentState);
		}
		//System.out.println(" to state "+prevState);
		return prevAction;
	}
	
	private void incrementNsa(State s, Action a){
		Nsa[s.getIndex()][a.getIndex()] ++;
	}
	
	private void incrementNsasp(State s, Action a, State sp) {
		int relPosition = getRelativePosition(s, sp);
		Nsasp[s.getIndex()][a.getIndex()][relPosition] ++;
	}
	
	private void updateTransitionModel(State s, Action a) {
		int sIndex = s.getIndex();
		int aIndex = a.getIndex();
		double p;
		for(int i=0; i<=numActions; ++i){
			if(Nsasp[sIndex][aIndex][i] != 0) {
				
				State nextState = getAbsolutePosition(s, i);
				p = (double)Nsasp[sIndex][aIndex][i]/(double)Nsa[sIndex][aIndex];
				mdp.setTransitionProbability(s, a, nextState, p);
			}
		}
		
	}
	
	private State getAbsolutePosition(State s, int relative) {
		int[][] rel2absolute = {{1,0}, {0,1}, {-1,0},{0,-1}, {0, 0}};
		
		int dr = rel2absolute[relative][0];
		int dc = rel2absolute[relative][1];
		
		int r = s.getRow() + dr;
		int c = s.getCol() + dc;
		
		r += 1;
		c += 1;
		
		return mdp.getState(r, c);
		
	}
	
	private int getRelativePosition(State s, State sp){
		int dr = sp.getRow() - s.getRow();
		int dc = sp.getCol() - s.getCol();
		
		dr += 1;
		dc += 1;
		
		// See cs.decision.MarkovDecisionProcess
		int[][] abs2relative = {{-1,2,-1},{3,4,1}, {-1,0, 1}};
		
		return abs2relative[dr][dc];
	}
}

