/*
 * Created by Roy
 *
 */
package cs.decision;
import java.util.*;

/**
 * A model Markov Decision Process.
 * 
 */
public class MarkovDecisionProcess {

	int rows, cols;
	State[][] grid;
	Vector reachableStates;
	int numReachableStates = 0;
	
	static final int ACTION_UP = 0;
	static final int ACTION_RIGHT = 1;
	static final int ACTION_DOWN = 2;
	static final int ACTION_LEFT = 3;
	static final int ACTION_STAY = 4; //stay is not an action per se.
	static final int numActions = 4;
	
	/**
	 * For each action, there is a resulting state.  Plus 1 because it's
	 * possible that the action is not allowed at boundaries and the resulting
	 * state is the originating state itself.
	 */
	static final int numResultingStates = numActions+1;
	
	/**
	 * The actions array contains all valid movements in the environment and
	 * provides a level of abstraction, so the client
	 * of this class doesn't have to worry about specific kinds of actions.
	 */
	Action[] actions;
	
	int[][] abs2relative = {{-1,2,-1},{3,4,1}, {-1,0, 1}};
	
	int[][] rel2absolute = {{1,0}, {0,1}, {-1,0},{0,-1}, {0, 0}};
	
	/**
	 * The transition function is a big (4*N^2) sparse matrix.  Reduce the
	 * last dimension to 5, number of actions+1 (stay)
	 */
	double[][][] transitionModel;
	Vector transitions;
	
	/**
	 * Variables for the iterator methods: getNextAction() and
	 * getNextState().
	 */
	int currentStateIndex;
	int currentAction;
	
	/**
	 * discount factor
	 */
	double gamma=1.0;
	
	public MarkovDecisionProcess(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		
		grid = new State[rows][cols];
		for(int i=0; i<rows; ++i)
			for(int j=0; j<cols; ++j){
				grid[i][j] = new State(i,j, 0.0, 0.0);
			}
	
		actions = new Action[4];
		actions[0] = new Action(ACTION_UP);
		actions[1] = new Action(ACTION_RIGHT);
		actions[2] = new Action(ACTION_DOWN);
		actions[3] = new Action(ACTION_LEFT);
	
		reachableStates = new Vector(rows*cols);
	}
	
	/**
	 * Set specified grid point null, meaning unreachable states
	 * 
	 * @param row Row number, starting from 1, of the hole.
	 * @param col Column number, starting from 1, of the hole.
	 */
	public void punchHole(int row, int col){
		grid[row-1][col-1] = null;
	}
	
	/**
	 * For testing only.
	 * @param r
	 * @param c
	 * @return
	 */
	public State getState(int r, int c){
		return grid[r-1][c-1];
	}
	
	/** 
	 * the following three methods ({@link #countReableSize} and {@link #getNextReachableState}
	 * perform a scanline order travesal of the environment.  The order of
	 * the traversal can be arbitrary.
	 * 
	 * @return Start state.
	 */
	public State getStartState() {
		currentStateIndex = 0;
		return (State)reachableStates.get(0);
	}
	
	/**
	 * The function is actually part of construction of this object.  Better
	 * to put it explicitly in the constructor, instead of wishing the user
	 * to call this voluntarily.
	 * It serializes all the states (in a 2D array) into a vector and initializing
	 * the transition model (a 3D array).
	 * 
	 */
	public void compileStates() {
		State s;
		int index = 0;

		reachableStates.clear();
		
		for(int i=0; i<rows; ++i)
			for(int j=0; j<cols; ++j) {
		
				s = grid[i][j];
				
				// A hole
				if(s == null)
					continue;
		
				s.index = index;
				index ++;
				reachableStates.add(s);
		}
	

		transitionModel = new double[index][numActions][numResultingStates];
		
		for(int i=0; i<index; ++i)
			for(int j=0; j<numActions; ++j)
				for(int k=0; k<numResultingStates; ++k)
					transitionModel[i][j][k] = 0.;
		
		transitions = new Vector();
		
		this.numReachableStates = index;
	}
	
	public int getReachableSize() {
		return numReachableStates;
	}
	
	/**
	 * Get next reachable state following a scanline travesal.  Any state
	 * other than a hole is reachable.
	 * 
	 * @return Next reachable state, <tt>null</tt> if no more state to visit.
	 */
	public State getNextState() {
		currentStateIndex ++;
		if(currentStateIndex == numReachableStates)
			return null;
		else
			return (State)reachableStates.get(currentStateIndex);

	}
	
	/**
	 * Because only (in theory) this class know the details of Action, this
	 * method belongs inside the MDP class.
	 * 
	 * @return A random valid action.
	 */
	public Action getRandomAction() {
		int a = (int)Math.round(Math.random()*numActions-0.5);
		return actions[a];
	}
	
	/**
	 * Generate a proper policy, which of course isn't necessarily optimal.
	 * A multi-path maze tracing problem.
	 */
	public void generateProperPolicy(){
		
		Stack stack = new Stack();
		
		for(State s=getStartState(); s!=null; s=getNextState()){
			
			if(s.action != null)
				continue;
		
			stack.push(s);
			
			while(!stack.empty()) {
			
				State currentState = (State)stack.peek();
				currentState.visited = true;
				
				int a;
				for(a=0; a<numActions; ++a){
					
					if(currentState.actionTaken[a])
						continue;

					currentState.actionTaken[a] = true;
					
					State nextState = move(currentState, a);
					
					if(nextState == currentState)
						continue;
					else if(/*nextState.action != null||*/nextState.terminate){
							currentState.action = actions[a];
							stack.pop();
							break;
					}else  if(nextState.visited)
						continue;
					else {
						currentState.action = actions[a];
						stack.push(nextState);
						break;
					}
				}
				if(a == 4){
					currentState.visited = false;
					for(int i=0; i<4; ++i)
						currentState.actionTaken[i]=false;
					stack.pop();
				}
			}
			
		}
		
	}
	
	/**
	 * This function is entirely for debugging purpose.  It requires the caller
	 * to know about the internal (action coding) of this class.
	 * 
	 * @param row The row number of the state.
	 * @param col The col number of the state.
	 * @param a Action coding.
	 */
	public void setAction(int row, int col, int a){
		grid[row-1][col-1].action = actions[a];
	}
	
	/**
	 * The function is for setting the action of the state
	 * @param s The state.
	 * @param a The action.
	 */
	public void setAction(State s, Action a) {
		if(s.terminate)
			return;
		
		s.action = a;
	}
	
	/**
	 * 
	 * @param s The state.
	 * @return The action of state s.
	 */
	public Action getAction(State s) {
		return s.action;
	}
	
	/**
	 * Iterator.
	 * @return The first valid action.
	 */
	public Action getStartAction() {
		currentAction = 0;
		return actions[0];
	}

	/**
	 * Go through all possible actions.
	 * @return Next applicable actions, <tt>null</tt> if no more action's possible.
	 */
	public Action getNextAction() {
		currentAction ++;
		if(currentAction == numActions)
			return null;
		else
			return actions[currentAction];
	}
	
	/**
	 * Set a state to terminate state.
	 * @param row Row number of the state.
	 * @param col Column number of the state.
	 */
	public void setTerminateState(int row, int col) {
		grid[row-1][col-1].setTerminate();
		grid[row-1][col-1].utility = grid[row-1][col-1].reward;
	}
	
	/**
	 * This function is only intended for setting up the process.  An 
	 * algorithm (user of this class) should use the next one.
	 * @param row Row number, starting from 1, not 0.
	 * @param col Column number, starting from 1.
	 * @param r Reward.
	 */
	public void setReward(int row, int col, double r) {
		State s = grid[row-1][col-1];
		if(s != null){
			s.reward = r;

			if(s.terminate)
				s.utility = r;
		}
	}
	
	/**
	 * Set reward value.
	 * @param s The state to be set value to.
	 * @param r The reward value.
	 */
	public void setReward(State s, double r) {
		s.reward = r;
	}
	
	/**
	 * 
	 * @param s The state.
	 * @return The reward value of the state.
	 */
	public double getReward(State s) {
		return s.reward;
	}
	
	/**
	 * Set utility of a state.  If the state is a terminate state, its
	 * utility value keeps unchanged.
	 * @param s The state.
	 * @param u The utility.
	 */
	public void setUtility(State s, double u){
		if(s.terminate){
			//TODO the assignment is redundant
			s.utility = s.reward;
		}else
			s.utility = u;
	}
	
	/**
	 * Used (solely) by policy evaluation.  Setting the utility value of a state.
	 * @param index The index of the state.
	 * @param u The utility value.
	 */
	public void setUtility(int index, double u) {
		((State)reachableStates.get(index)).utility = u;
	}
	
	/**
	 * @param s The state.
	 * @return The utility value of the state.
	 */
	public double getUtility(State s) {
		return s.utility;
	}
	
	/**
	 * @return Returns the gamma.
	 */
	public double getGamma() {
		return gamma;
	}
	
	/**
	 * @param gamma The gamma to set.
	 */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	
	
	public void accumTransitionProbability(int r, int c, Action a, int rp, int cp, double prob) {
		State s = grid[r-1][c-1];
		if(s.terminate)
			return;
		
		int nextStateIndex = abs2relative[rp-r+1][cp-c+1];
		transitionModel[s.index][a.action][nextStateIndex] += prob;
	}
	
	public void accumTransitionProbability(State s, Action a, State sp, double p) {

		accumTransitionProbability(s.row+1, s.col+1, a, sp.row+1, sp.col+1, p);
	}
	
	/**
	 * This function is for setting up the transition model.
	 * @param r Row number (starts from 1) of current state.
	 * @param c Column number (starts from 1) of current state.
	 * @param a Action to be taken.
	 * @param rp Row number of next state, after the action is performed.
	 * @param cp Column number of next state.
	 * @param prob The probability of this chain of action: T(s,a,s').
	 */
	public void setTransitionProbability(int r, int c, Action a, int rp, int cp, double prob) {

		State s = grid[r-1][c-1];
		if(s.terminate)
			return;
		
		int nextStateIndex = abs2relative[rp-r+1][cp-c+1];
		transitionModel[s.index][a.action][nextStateIndex] = prob;
	}
	
	/**
	 * Set T(s,a,s').
	 * @param s The source state.
	 * @param a The action to be taken.
	 * @param sp The destination state.
	 * @param p The probability.
	 */
	public void setTransitionProbability(State s, Action a, State sp, double p) {

		setTransitionProbability(s.row+1, s.col+1, a, sp.row+1, sp.col+1, p);
	}
	
	/**
	 * This is the transition function, T(s,a,s') in the textbook.
	 * 
	 * @param s The current state.
	 * @param a Action to be taken.
	 * @return A list of (probability,next-state) pairs.
	 */
	public Vector getTransition(State s, Action a) {
		transitions.clear();

		if(s.terminate) 
			return transitions;
		
		double p;
		State nextState;
		
		///System.out.println("transtion");
		
		for(int i=0; i<numResultingStates; ++i) {
			
			p = transitionModel[s.index][a.action][i];

			nextState = move(s, i);
			
			transitions.add(new Transition(p, nextState));
			
			////System.out.println(" P" + p);
		}
		
		return transitions;
	}
	
	/**
	 * Display the MDP in simple text format.
	 *
	 */
	public void dump(){
		for(int i=rows-1; i>=0; --i){
			for(int j=0; j<cols; ++j){
				if(grid[i][j] != null){
					if(grid[i][j].terminate)
						System.out.print(grid[i][j].reward+"/"+grid[i][j].utility+"* ");
					else
						System.out.print(grid[i][j].reward+"/"+grid[i][j].utility + " ");
				}
				else
					System.out.print("  N  ");
			}
			System.out.println();
		}		
	}
	
	
	/**
	 * Display the MDP in an table.
	 * @param showHeader Whether to print out HTML headers.
	 *
	 */
	public void dumpTable(boolean showHeader) {
			
		for(int i=rows-1; i>=0; --i){
			System.out.println("");
			for(int j=0; j<cols; ++j){
				
				if(grid[i][j] != null){
					if(grid[i][j].terminate)
						System.out.print(grid[i][j].utility);
					else
						System.out.print(grid[i][j].utility+":"+grid[i][j].action);
				}
				else
					System.out.print(" N ");
				System.out.print(" | ");
			}
		}		
		
	}
	
	/**
	 * Display the MDP in an HTML table.
	 * @param showHeader Whether to print out HTML headers.
	 *
	 */
	public void dumpHTML(boolean showHeader) {
		if(showHeader)
			System.out.println("<html><head></head><body>");
		
		System.out.println("<table border=1 cellspacing=0 cellpadding=3>");
		
		for(int i=rows-1; i>=0; --i){
			System.out.println("<tr>");
			for(int j=0; j<cols; ++j){
				System.out.print("<td>");
				if(grid[i][j] != null){
					if(grid[i][j].terminate)
						System.out.print(grid[i][j].utility);
					else
						System.out.print(grid[i][j].utility+":"+grid[i][j].action);
				}
				else
					System.out.print(" N ");
				System.out.println("</td>");
			}
			System.out.println("</tr>");
		}		
		System.out.println("</table>");
		
		if(showHeader)
			System.out.println("</body></html>");
	}
	
	/**
	 * For testing only.
	 *
	 */
	public void dumpTransitionModel() {
		System.out.println("<html><head></head><body>");
		System.out.println("<table border=1 cellspacing=0 cellpadding=3>");
		State s;
		for(int i=rows-1; i>=0; --i){
			System.out.println("<tr>");
			for(int j=0; j<cols; ++j){
				System.out.print("<td>");
				if((s=grid[i][j]) != null){
					for(int a=0; a<4; ++a) {
						Action act = actions[a];
						Vector t=getTransition(s, act);
						System.out.print(act+":");
						for(int k=0; k<t.size(); k++){
							Transition tr=(Transition)t.get(k);
							System.out.print(tr.probability+"/");
						}
						System.out.println("<br>");
					}

				}
				else
					System.out.print(" N ");
				System.out.println("</td>");
			}
			System.out.println("</tr>");
		}		
		System.out.println("</table></body></html>");
		
	}
	
	public State move(State s, Action a) {
		return move(s, a.action);
	}
	
	/**
	 * Move on the environment.  It'll never return a null (a unreachable
	 * state).
	 * 
	 * @param s Current state.
	 * @param action Type of movement.
	 * @return Next state after the movement.
	 */
	public State move(State s, int action){
		int row = s.row;
		int col = s.col;
		
		if (action == ACTION_STAY)
			return s;
		
		switch (action) {
			case ACTION_UP:
				row += 1;
				if(row > rows-1)
					row = rows-1;
				break;
			case ACTION_RIGHT:
				col += 1;
				if(col > cols-1)
					col = cols-1;
				break;
			case ACTION_DOWN:
				row -= 1;
				if(row < 0)
					row = 0;
				break;
			case ACTION_LEFT:
				col -= 1;
				if(col < 0)
					col = 0;
				break;
		}
		if(grid[row][col] == null)
			return s;
		else
			return grid[row][col];
	}

	/**
	 * @return Total number of valid actions.
	 */
	public int getNumberActions() {
		return numActions;
	}

	/**
	 * This is like move(), only taking the transition model into account.
	 * That is, if you move upwards, you could end up at the cell to the
	 * left.
	 * @param s Current state.
	 * @param a Action to be taken.
	 * @return Next state.
	 */
	public State transit(State s, Action a) {
		double[] dist = new double[numResultingStates];
		int sIndex = s.getIndex();
		int aIndex = a.getIndex();
		dist[0] = transitionModel[sIndex][aIndex][0];
		
		for(int i=1; i<numResultingStates; ++i) 
			dist[i] = dist[i-1] + transitionModel[sIndex][aIndex][i];
		
		double p = Math.random();
		int next=0;
		if(p<dist[0]) {
			next = 0;
		}else {
			for(int i=1; i<numResultingStates; ++i){
				if(dist[i-1]<=p && p<=dist[i]){
					next = i;
					break;
				}
			}
		}
		
		int dr = rel2absolute[next][0];
		int dc = rel2absolute[next][1];
		
		return grid[s.row+dr][s.col+dc];
	}

	/**
	 * 
	 * @return Number of rows of this MDP.
	 */
	public int getRows() {
		return rows;
	}
	
	/**
	 * @return Number of columns of this MDP.
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * Copy the the layout of this MDP, meaning the size, holes and
	 * terminate states.
	 * 
	 * @return A new MDP
	 */
	public MarkovDecisionProcess copyLayout() {
		MarkovDecisionProcess newMDP = new MarkovDecisionProcess(this.rows, this.cols);
		for(int i=0; i<rows; ++i)
			for(int j=0; j<cols; ++j) {
				if(this.grid[i][j] == null) 
					newMDP.grid[i][j] = null;
				else if(this.grid[i][j].terminate) {
					newMDP.grid[i][j].setTerminate();
				}
			}
		
		newMDP.compileStates();
		
		return newMDP;
	}
	
	/**
	 * Given a state belongs to another MDP, find the state of this
	 * MDP at the same position.
	 * @param otherState
	 * @return
	 */
	public State getCoincideState(State otherState) {
		int r = otherState.getRow();
		int c = otherState.getCol();
		return this.grid[r][c];
	}

	/**
	 * @return A random reachable state.
	 */
	public State getRandomReachableState() {
		int i = (int)(Math.random()*numReachableStates);
		return (State)reachableStates.get(i);
	}

}
