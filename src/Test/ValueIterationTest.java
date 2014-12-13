/*
 * Created by Roy
 *
 */

package Test;

import cs.decision.*;
import java.io.FileNotFoundException;

public class ValueIterationTest {

    public static void main(String args[]) {
	try {
	    MDPFileParser parser = new MDPFileParser("example.txt");
	    MarkovDecisionProcess mdp = parser.parse();

	    ValueIteration vi = new ValueIteration(mdp);
	    vi.setError(1e-4);
	    vi.solve();
	    ///mdp.dumpHTML(false);
	    mdp.dumpTable(false);

	} catch (FileNotFoundException e){
	    System.out.println(e);
	}
    }

}
