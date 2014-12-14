/*
 * Created by Roy
 *
 */

package Test;

import basic.Mat;
import basic.STATE;
import basic.STATE_NODE;
import cs.decision.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;




public class MDPSolutionMain {

	public static final double INF = 1e30;
	
    MarkovDecisionProcess mdp;
    static int n; //number of components
    static int m; //number of virtual machine
    static int deadline;
    static int dollarCost[];
    static int time[];
    static double reliability[]; //Reliability of Virtual Machine, stands for p(s|a , s')

    /**
     * Constructor for PolicyIterationTest.
     * @param name
     */
    public static void main(String[] args) {
    	readfile("./src/Test/example_MDP.txt");
    	runWorkflow();
    }
    
    public static void readfile(String filename){
    	File file = new File(filename);
    	BufferedReader reader = null;
    	try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                if(tempString.contains("componentNumber")){
                	String nStr = tempString.substring(tempString.indexOf("=")+1, tempString.length()).trim();
                	n = Integer.parseInt(nStr);
                }
                else if(tempString.contains("virtualMachineNumber")){
                	String mStr = tempString.substring(tempString.indexOf("=")+1, tempString.length()).trim();
                	m = Integer.parseInt(mStr);
                	dollarCost = new int[m];
                	time = new int[m];
                	reliability = new double[m];
                }
                else if(tempString.contains("deadline")){
                	String deadlineStr = tempString.substring(tempString.indexOf("=")+1, tempString.length()).trim();
                	deadline = Integer.parseInt(deadlineStr);
                }
                else if(tempString.contains("dollarCost")){
                	String dollarCostStr = tempString.substring(tempString.indexOf("=")+1, tempString.length()).trim();
                	for(int i=0; i<m; i++){
                		if(dollarCost[i]==0){
                			dollarCost[i] = Integer.parseInt(dollarCostStr);
                			break;
                		}
                	}
                }
                else if(tempString.contains("time")){
                	String timeStr = tempString.substring(tempString.indexOf("=")+1, tempString.length()).trim();
                	for(int i=0; i<m; i++){
                		if(time[i]==0){
                			time[i] = Integer.parseInt(timeStr);
                			break;
                		}
                	}
                }
                else if(tempString.contains("reliability")){
                	String reliabilityStr = tempString.substring(tempString.indexOf("=")+1, tempString.length()).trim();
                	for(int i=0; i<m; i++){
                		if(reliability[i]==0){
                			reliability[i] = Double.parseDouble(reliabilityStr);
                			break;
                		}
                	}
                }
                else{
                	
                }
                
                
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
    public static void runWorkflow(){
    	double reward = 1;
        
    	Mat A = new Mat(n,m,new int[m]);//Action
    	Mat T = new Mat(n,m,time);//Execution time 	
    	Mat COST = new Mat(n,m,dollarCost);//Dollar Cost 把哪个component分配到VM
    	
    	String printThings = "State Permutation: ";
 	
    	boolean Task[] = new boolean[n];
    	
    	STATE  st = new STATE(n,m);

    	
    	STATE_NODE sta_node;
    	
    
    	final double CostErr = 1e-5;
    	double min = 0;

    		double CostErrTmp1 = 0;
    		double CostErrTmp2 = 0;
    		int bst_childid = 0;
    		int k = 0;
    		int count = m;
    		int bst_childid_data[] =new int[m+1];
	    	for(sta_node = st.getStartStateNode(); count >= 0;sta_node = st.getStateNodeById(bst_childid))
	    	{
	    		if(sta_node.terminal == true) continue;
	    		
	    		//double minCost = sta_node.getCost();
	    		double minCost = INF;
	    		
	    		
	    		double minCostTmp = 0;
	    		
	    		int[] minCost_child_dif = {-1,-1};
	    		
	    		
	    		for(int childId = sta_node.getStartChildNodeId() ; childId != -1 ; childId = sta_node.getNextChildNodeId())
	    			
	    		{
	    			 int dif[] = sta_node.getStartChildDif();
	    			
	    			 minCostTmp =  COST.get(dif[0], dif[1])*reliability[dif[1]];
	    			 
	    			// minCostTmp += (1 - reliability[dif[1]] )*(minCost+ reward);
	    				    			
	    			 if(minCostTmp <= minCost)
	    			 {
	    				// CostErrTmp1 = Math.abs(minCost - minCostTmp);
	    				 
	    				 minCost = minCostTmp;
	    				 
	    				 minCost_child_dif = dif;	    				 
	    				
	    				 sta_node.setCost(minCost);
	    				 sta_node.setTrans(minCost_child_dif);
	    				
	    				 bst_childid = childId;
	    				// if(CostErrTmp1 > CostErrTmp2) CostErrTmp2 = CostErrTmp1; 

	    			 }
	    			 
	    			 System.out.printf("0"+childId+"\t");		
	    			
	    		}
	    		min += minCost;
    			
	    		System.out.printf("/////"+bst_childid+"/////"+"\n");	
	    		printThings += bst_childid+1 + " ->" ;
	    		bst_childid_data[m-count] = bst_childid;
	    		count --;
	    	}
	    
	    	printThings += " Finished";
    	
    	//st.showResult();
    	System.out.printf(printThings+"\n");
    	
    	st.showState(m, bst_childid_data);
    	System.out.printf("Minimal Total Cost : "+min);
    
    }

}


