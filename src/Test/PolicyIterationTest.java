/*
 * Created by Roy
 *
 */

package Test;

import cs.decision.*;

import java.io.FileNotFoundException;

public class PolicyIterationTest {

    MarkovDecisionProcess mdp;

    /**
     * Constructor for PolicyIterationTest.
     * @param name
     */
    public static void main(String[] args) {
	try {
	    MDPFileParser parser = new MDPFileParser("example.txt");
	    MarkovDecisionProcess mdp = parser.parse();

	    PolicyIteration pi = new PolicyIteration(mdp);

	    pi.solve();
	    ////mdp.dumpHTML(false);
	    ///mdp.dumpTable(false);
	    mdp.dump();
	    //mdp.dumpTransitionModel();
	} catch (FileNotFoundException e){
	    System.out.println(e);
	}
    }



}
