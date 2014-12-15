/*
 * Created by Roy
 *
 */
package cs.decision;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDPFileParser {
	
	static final int ACTION_UP = 0;
	static final int ACTION_RIGHT = 1;
	static final int ACTION_DOWN = 2;
	static final int ACTION_LEFT = 3;
	int[][] transitionAction = {
			{ACTION_LEFT, ACTION_UP, ACTION_RIGHT, ACTION_DOWN},
			{ACTION_RIGHT, ACTION_DOWN, ACTION_LEFT, ACTION_UP}
	};
	
	IniParser mdpFile;
	MarkovDecisionProcess mdp=null;
	int rows, cols;
	
	public MDPFileParser(String filename) throws FileNotFoundException {
		mdpFile = new IniParser(filename);
	}

	public MarkovDecisionProcess parse() {
				
		mdpFile.parseIniFile();
		
		IniSection section = mdpFile.getSection("Size");
		if(section == null){
			System.out.println("No size info, check your configuration file.");
			return null;
		}
		
		rows = Integer.parseInt(section.getValue("rows"));
		
		cols = Integer.parseInt(section.getValue("cols"));
		
		///System.out.println("rows:"+rows+",cols:"+cols);
	
		mdp = new MarkovDecisionProcess(rows, cols);

		section = mdpFile.getSection("Holes");
		if(section != null)
			parseHoles(section);
		
		section = mdpFile.getSection("Rewards");
		if(section != null)
			parseRewards(section);
		
		section = mdpFile.getSection("Terminate states");
		if(section != null)
			parseTerminateStates(section);

		mdp.compileStates();
		
		section = mdpFile.getSection("Transition model");
		if(section != null)
			parseTransitionModel(section);

		return mdp;
	}
	
	public static String stripWhiteSpace(String s){
		return s.replaceAll("\\s*", "");
	}
	
	private void extractRowCol(String value, String[] s) {
		//value is if form "(3,4)"
		// could use RegEx, but why overkill
		s[0]=value.substring(value.indexOf('(')+1, 
				value.indexOf(','));
		s[1]=value.substring(value.indexOf(',')+1, 
				value.lastIndexOf(')'));
	}

	private void parseHoles(IniSection section){
		String value;
		String[] lr = new String[2];
		
		section.vectorize();
		
		int numVals = section.size();
		for(int i=0; i<numVals; ++i){
			value = section.getValue(i);

			value = stripWhiteSpace(value);
			
			extractRowCol(value, lr);
			int row = Integer.parseInt(lr[0]);
			int col = Integer.parseInt(lr[1]);
			
			//System.out.println("Hole at "+row+" "+col);
			
			mdp.punchHole(row, col);
			
		}
	}
	
	private void parseRewards(IniSection section) {
		int numVals = section.size();
		String[] lr = new String[2];
		
		section.vectorize();
		for(int n=0; n<numVals; ++n){
			
			// after vectorize, key(n) and value(n) are guarantted
			// to be pairing.
			String posString = section.getKey(n);
			String rewardString = section.getValue(n);
			double reward = Double.parseDouble(rewardString);
			
			posString = stripWhiteSpace(posString);
			
			extractRowCol(posString, lr);
			
			if(lr[0].equals("*") && lr[1].equals("*")){
				for(int i=1; i<=rows; ++i)
					for(int j=1; j<=cols; ++j)
						mdp.setReward(i,j, reward);
			} else if(lr[0].equals("*")){
				int col = Integer.parseInt(lr[1]);
				for(int i=1; i<=rows; ++i)
					mdp.setReward(i, col, reward);
			} else if(lr[1].equals("*")) {
				int row = Integer.parseInt(lr[0]);
				for(int j=1; j<=cols; ++j)
					mdp.setReward(row, j, reward);
			}else{
				try {
					int row = Integer.parseInt(lr[0]);
					int col = Integer.parseInt(lr[1]);
				
					mdp.setReward(row, col, reward);
				} catch (NumberFormatException e) {
					System.out.println("Invalid numbers: "+lr[0]+" "+lr[1]);
				}
			}
		}
	}
	
	private void parseTerminateStates(IniSection section) {
		String value;
		String[] lr = new String[2];
		
		section.vectorize();
		
		int numVals = section.size();
		for(int i=0; i<numVals; ++i){
			value = section.getValue(i);

			//value = stripWhiteSpace(value);
			
			extractRowCol(value, lr);
			int row = Integer.parseInt(lr[0]);
			int col = Integer.parseInt(lr[1]);
			
			//System.out.println("Terminate states "+row+" "+col);
			
			mdp.setTerminateState(row, col);
			
		}
		
	}
	
	private void parseTransitionModel(IniSection section) {
		int numVals = section.size();
		
		///System.out.println("numVals :"+numVals);
		
		String[] lr = new String[2];
		
		section.vectorize();
		
		// This section can be quite ugly.  Expecting
		// (*, FOWARD|LEFT|RIGHT|(BACK) = num
		// or
		// ((3,4), F|L|R|B) = num
		for(int i=0; i<numVals; ++i){
			
			// after vectorize, key(i) and value(i) are guarantted
			// to be pairing.
			String posString = section.getKey(i);
			String probString = section.getValue(i);
			
			posString = stripWhiteSpace(posString);

			if(posString.indexOf('*') != -1){
				parseAgentCentric(posString, probString);
			}else{
				parseRegular(posString, probString);
			}
		}
		
	}
	
	private void parseAgentCentric(String position, String prob){
		double p = Double.parseDouble(prob);
		
		// position is of form "(*,*),FORWARD|LEFT|RIGHT"
		if(position.matches(".*FORWARD.*")){
            
			for(State s = mdp.getStartState(); s!=null; s = mdp.getNextState()){
			
				////System.out.println("");
				for(Action a=mdp.getStartAction(); a!=null; a=mdp.getNextAction()){
					State nextState = mdp.move(s, a);
					///System.out.println("************ Setting:"+s+" "+a+" "+nextState+" "+p);
					
					mdp.accumTransitionProbability(s, a, nextState, p);
				}
			}
		}else if(position.matches(".*LEFT.*")) {

			for(State s = mdp.getStartState(); s!=null; s = mdp.getNextState()){
				// a cycles through forward movement, the table maps what is the "LEFT"
				// side step.
				///System.out.println("");
				for(Action a=mdp.getStartAction(); a!=null; a=mdp.getNextAction()){
					State nextState = mdp.move(s, transitionAction[0][a.action]);
					///System.out.println("************ Setting:"+s+" "+a+" "+nextState+" "+p);
					mdp.accumTransitionProbability(s, a, nextState, p);
				}
			}
			
		}else if(position.matches(".*RIGHT.*")) {
			for(State s = mdp.getStartState(); s!=null; s = mdp.getNextState()){
				for(Action a=mdp.getStartAction(); a!=null; a=mdp.getNextAction()){
					State nextState = mdp.move(s, transitionAction[1][a.action]);
					///System.out.println("************ Setting:"+s+" "+a+" "+nextState+" "+p);
					mdp.accumTransitionProbability(s, a, nextState, p);
				}
			}
		}else {
			System.out.println("Invalid transition entry:"+position);
		}
	}
	
	Pattern transPat = Pattern.compile("\"\\((\\d+),(\\d+)\\),"+
			"(UP|RIGHT|DOWN|LEFT),\\((\\d+),(\\d+)\\)\"");
	
	private void parseRegular(String position, String prob){
		
		// A typical position looks like "(1,1),UP,(2,1)"
		// and prob is a floating point number.

		// Too awkward to use extractRowCol.  Plug in RE
		Matcher transMatcher = transPat.matcher(position);
		if(transMatcher.matches()) {
			int r = Integer.parseInt(transMatcher.group(1));
			int c = Integer.parseInt(transMatcher.group(2));
			String a = transMatcher.group(3);
			int rp = Integer.parseInt(transMatcher.group(4));
			int cp = Integer.parseInt(transMatcher.group(5));
			
			double p = Double.parseDouble(prob);
			mdp.setTransitionProbability(r,c, new Action(a), rp, cp, p);
		
		}else {
			throw (new IllegalArgumentException("Check configuration file:"+position));
		}
	}
}
