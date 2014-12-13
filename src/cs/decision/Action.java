/*
 * Created by Roy
 *
 */
package cs.decision;

/**
 * 
 * A simple wrapper around the type of movements valid in a specific
 * environment.
 * 
 */
public class Action {
	int action;
	
	public Action(int action) {
		this.action = action;
	}
	
	/**
	 * This constructor relies on specific type of action, namely
	 * a 4-neighbor type action.  Shouldn't be used.  It's here only
	 * for convenience.
	 * @param a A string of action type.
	 */
	public Action(String a) {
		if(a.equals("UP"))
			action = 0;
		else if(a.equals("RIGHT"))
			action = 1;
		else if(a.equals("DOWN"))
			action = 2;
		else if(a.equals("LEFT"))
			action =3;
		else
			throw (new IllegalArgumentException("Illegal action:"+a));
	}
	
	public int getIndex() {
		return action;
	}
	
	public boolean equals(Object a) {
		if( (a!=null) && (a instanceof Action)){
			if( ((Action)a).action == this.action)
				return true;
			else
				return false;
		}
		return false;
	}
	
	public String toString() {
		switch (action) {
			case 0: return "UP";
			case 1: return "RIGHT";
			case 2: return "DOWN";
			case 3: return "LEFT";
		}
		return null;
	}
	
	public String toSign() {
		switch (action) {
			case 0: return "^";
			case 1: return ">";
			case 2: return "V";
			case 3: return "<";
		}
		return null;
	}
}
