/*
 * Created by Roy
 *
 */
package cs.learning;

import cs.decision.*;

public class Percept {
	State state;
	double reward;
	
	public Percept(State s, double r) {
		state = s;
		reward = r;
	}
}
