/*
 * Created by Roy
 *
 */

package Test;

import basic.Component;
import basic.Mat;
import basic.STATE;
import basic.STATE_NODE;
import cs.decision.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



public class WorkflowMDP {

	public static final double INF = 1e30;
	
    MarkovDecisionProcess mdp;
    static int n; //number of components
    static int u; //number of units
    static int m; //number of virtual machine
    static int deadline;
    static int dollarCost[];
    static int time[];

    static int MODE;

    static double reliability[]; //Reliability of Virtual Machine, stands for p(s|a , s')
    static ArrayList<Component> components = new ArrayList<Component>(); //Dependency of components
    static ArrayList<String> featureList = new ArrayList<String>();

    /**
     * Constructor for PolicyIterationTest.
     * @param name
     */
    public static void main(String[] args) {
    	readfile("./src/Test/example_MDP.txt");
    	runWorkflowApproximate();
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
                    u = Integer.parseInt(nStr);
                	for(int i=0; i<n;i++){
                		Component componenti = new Component(String.valueOf(i+1));
                		components.add(componenti);
                	}
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
                
                //components information
                else if(tempString.contains("componentId")){
                    int tempVMId = Integer.parseInt(tempString.split("=")[1].trim());
                    tempString = reader.readLine();
                    if(tempString == null || tempString.length() <= 1){
                        System.out.println("error input file");
                        System.exit(-1);
                    }else{
                        int tempComputeUnit = Integer.parseInt(tempString.split("=")[1].trim());
                        components.get(tempVMId - 1).setUnit(tempComputeUnit);
                    }
                }
                
                else if(tempString.contains("componentDependency")){
                	String[] temp = tempString.trim().split(":");
                	String[] dependencies = temp[1].trim().split(",");
                	for(int i=0;i<dependencies.length;i++){
                		String[] depend = dependencies[i].trim().split("-");
                		int former = Integer.parseInt(depend[0]);
                		components.get(former-1).addChildren(depend[1]);
                	}
                }
                else if(tempString.contains("feature")){
                	String[] temp = tempString.trim().split("=");
                	featureList.add(temp[1].trim());
                	
                }
                else if(tempString.contains("mode"))
                {
                	String[] temp = tempString.trim().split("=");
                	MODE = Integer.parseInt(temp[1].trim());
                	System.out.println("MODE = "+MODE);
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

    	Mat TimeCost = new Mat(n,m,time);//Execution time 	
    	Mat COST = new Mat(n,m,dollarCost);//Dollar Cost 把哪个component分配到VM
    	
    	 	

    	boolean Task[] = new boolean[n];
    	
    	STATE  st = new STATE(u,m);

    	
    	STATE_NODE sta_node;
    	
    
    	final double CostErr = 1e-5;

    	int cnt = 0;
    	while(true)
    	{
    		cnt++;
    		double CostErrTmp1 = 0;
    		double CostErrTmp2 = 0;
    		double TimeCostErrTmp1 = 0;
    		double TimeCostErrTmp2 = 0;
    	
	    	for(sta_node = st.getStartStateNode(); sta_node != null;sta_node = st.getNextStateNode())
	    	{
	    		if(sta_node.terminal == true) continue;
	    		
	    		double minCost = sta_node.getCost();

	    		double minTimeCost = sta_node.getTimeCost();
	    		//System.out.println("c+t: " + minCost + " , " + minTimeCost);
	    		double minCostTmp = 0;
	    		double minTimeCostTmp = 0;

	    		
	    		int[] minCost_child_dif = {-1,-1};
	    		
	    		
	    		for(int childId = sta_node.getStartChildNodeId() ; childId != -1 ; childId = sta_node.getNextChildNodeId())

	    		{
	    			 int dif[] = sta_node.getStartChildDif();
	    			
	    			 minCostTmp =  (COST.get(dif[0], dif[1])*components.get(dif[1]).getUnit() +st.getStateNodeById(childId).getCost())*reliability[dif[1]];
	    			 
	    			 minCostTmp += (1 - reliability[dif[1]] )*(minCost+ reward);
	    			 
	    			 minTimeCostTmp = (TimeCost.get(dif[0], dif[1]) + st.getStateNodeById(childId).getTimeCost())*reliability[dif[1]];
	    			 //System.out.println("@@@"+minCostTmp+"###"+minTimeCostTmp);
	    			 minTimeCostTmp += (1 - reliability[dif[1]] )*(minTimeCost+reward);
	    			 //System.out.println("@@"+minCostTmp+"##"+minTimeCostTmp);
	    			 if(minCostTmp + minTimeCostTmp < minCost + minTimeCost)
	    			 {
	    				 CostErrTmp1 = Math.abs(minCost - minCostTmp);
	    				 minCost = minCostTmp;
	    				 
	    				 TimeCostErrTmp1 = Math.abs(minTimeCost - minTimeCostTmp);
	    				 minTimeCost = minTimeCostTmp;
	    				 
	    				 minCost_child_dif = dif;
	    				 
	    				 sta_node.setCost(minCost);

	    				 sta_node.setTimeCost(minTimeCost);
	    				 sta_node.setTrans(minCost_child_dif);
	    				 
	    				 if(CostErrTmp1 > CostErrTmp2) CostErrTmp2 = CostErrTmp1; 
	    				 if(TimeCostErrTmp1 > TimeCostErrTmp2)
	    					 TimeCostErrTmp2 = TimeCostErrTmp1;
	    			 }
	    		}
	    	}
	    	
	    	if(MODE == 1)
	    	{
	    		if(st.getStartStateNode().getTimeCost() < deadline && CostErrTmp2 < CostErr)
	    			break;
	    	}
	    	if(MODE == 2)
	    	{
	    		if(CostErrTmp2 < CostErr && TimeCostErrTmp2 < CostErr)
	    		{
	    			System.out.println("!!!!!"+cnt);
	    			System.out.println("@@@@"+CostErrTmp2);
	    			System.out.println("###"+TimeCostErrTmp2);
	    			break;
	    		}
	    			
	    	}
	    	if(MODE == 3)
	    	{
	    		if(TimeCostErrTmp2 < CostErr)
	    			break;
	    	}
    	}

    	st.showResult();
    
    }
    
    public static void runWorkflowApproximate(){
    	double reward = 1;
        
    	Mat A = new Mat(n,m,new int[m]);//Action
    	Mat TimeCost = new Mat(n,m,time);//Execution time 
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
	    		double minTimeCost = INF;
	    		
	    		double minCostTmp = 0;
	    		double minTimecostTmp = 0;
	    		double timeRest = deadline;
	    		
	    		int[] minCost_child_dif = {-1,-1};
	    		
	    		
	    		for(int childId = sta_node.getStartChildNodeId() ; childId != -1 ; childId = sta_node.getNextChildNodeId())
	    			
	    		{
	    			 int dif[] = sta_node.getStartChildDif();
	    			
	    			 minCostTmp =  COST.get(dif[0], dif[1])*components.get(dif[0]).getUnit()*reliability[dif[1]];
	    			 minTimecostTmp = TimeCost.get(dif[0], dif[1]);
	    			 timeRest = timeRest - minTimecostTmp;
	    			 minCostTmp = minCostTmp * (deadline / timeRest);
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



