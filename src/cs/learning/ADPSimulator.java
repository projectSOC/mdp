/*
 * Created by Roy
 *
 */
package cs.learning;
import java.io.FileNotFoundException;

import cs.decision.*;

/**
 * Simulator for testing an ADP agent.
 * 
 */
public class ADPSimulator {

	MarkovDecisionProcess modelMDP;
	MarkovDecisionProcess learnedMDP;
	ADPAgent agent;
	
	public static void main(String[] args) throws FileNotFoundException {
		ADPSimulator simulator;
		
		if(args.length >= 1)
			simulator = new ADPSimulator(args[0]);
		else
			simulator = new ADPSimulator("textbook.txt");
		
		simulator.demo();
	}
	
	public ADPSimulator(String filename) throws FileNotFoundException{
		
		MDPFileParser parse = new MDPFileParser(filename);
		modelMDP = parse.parse();
		
		// Create the learned MDP
		learnedMDP = modelMDP.copyLayout();
		
		// Generate a policy for the learned MDP
		agent = new ADPAgent(learnedMDP);

	}
	
	public void demo() {
		
		//learnedMDP.generateProperPolicy();
		learnedMDP.setAction(1,1, 0);
		learnedMDP.setAction(2,1, 0);
		learnedMDP.setAction(3,1, 1);
		learnedMDP.setAction(1,2, 3);
		learnedMDP.setAction(3,2, 1);
		learnedMDP.setAction(1,3, 3);
		learnedMDP.setAction(2,3, 0);
		learnedMDP.setAction(3,3, 1);
		learnedMDP.setAction(1,4, 3);
		
		//Mark every state new
		for(State s=learnedMDP.getStartState(); s!=null; s=learnedMDP.getNextState())
			s.setVisited(false);
		
		learnedMDP.dumpHTML(true);
		run(100);
		learnedMDP.dumpTransitionModel();
	}
	
	public void run(int numTrials) {
		Percept percept = new Percept(null, 0.0);

		// Now we have two models here and the states
		// are different, though refer to the same position (state as meant
		// by the textbook), we have resort to the (row, col) cooridinates
		// Can be an arbitrary state.
		State s = learnedMDP.getStartState();
		State modelState;
		for(int trials=0; trials < numTrials; trials++) {

			System.out.println("<p>"+trials+"</p>");
			learnedMDP.dumpHTML(false);
			
			percept.state = s;
			modelState = modelMDP.getCoincideState(s);
			percept.reward = modelMDP.getReward(modelState);
			
			// The agent decides what to do next
			Action a = agent.go(percept);
			//System.out.println("Trial: "+trials+" go "+a);
			
			if(a == null) {
				s = learnedMDP.getRandomReachableState();
			}else {
				// Given the state and the action, the simulator
				// determines the next state using the transition model
				// There should be modelAction and learnedAction for
				// actions from the 2 different MDPs, too.  But the way
				// actions are used guarantees no confusion.
				modelState = modelMDP.transit(modelState, a);
				s = learnedMDP.getCoincideState(modelState);
			}
		}
	}
}
