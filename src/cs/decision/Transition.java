/*
 * Created by Roy
 *
 */
package cs.decision;

public class Transition {
	double probability;
	State nextState;
	
	public Transition(double probability, State state) {
		this.probability = probability;
		this.nextState = state;
	}
}
