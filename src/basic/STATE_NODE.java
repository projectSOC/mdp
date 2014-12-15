package basic;

import java.util.ArrayList;
import java.util.List;

import Test.WorkflowMDP;

public class STATE_NODE {

	Mat mat;
	double cost;
	double timeCost;
	List <Integer>  childrenList; //save the state of children state
	List <int []>  childrenDifList;
	
	
	int NextStateNodeNum; //number of all possible next states
	int NextStateNodeNow; //current children state that have been traversed
	public boolean terminal;
	
	int trans[] = new int[2];
	public void setTrans(int trans_in[])
	{
		trans[0] = trans_in[0];
		trans[1] = trans_in[1];
	}
	
	STATE_NODE(int n,int m)
	{
		mat= new Mat(n,m,new int[m]);
		mat.MatClear();
		cost = WorkflowMDP.INF;
		timeCost = WorkflowMDP.INF;
		
		childrenList = new ArrayList();
		childrenDifList = new ArrayList();
		NextStateNodeNum = 0;
		NextStateNodeNow = 0;
		terminal = false;
	}
	
	public void setCost(double cost_in)
	{
	
		cost =cost_in;
	
	}
	
	public double getCost()
	{
		
		return cost;
	}
	
	public void setTimeCost(double timeCost)
	{
		this.timeCost = timeCost;
	}
	
	public double getTimeCost()
	{
		return timeCost;
	}
	
	void addNestStateNodeById(int id,int[] dif)
	{
		//dif0 component dif1 vm action action是将任务dif[0]分配到VM dif[1]
		childrenList.add(id);
		childrenDifList.add(dif);
		
		NextStateNodeNum ++;
	}
	
	public int getStartChildNodeId()
	{
	
	  NextStateNodeNow = 0;
		
	  if(NextStateNodeNum > 0) 
		  return (int)childrenList.get(0);
	  else
		  return -1;
		
	}
	
	public int[] getStartChildDif()
	{
		  return childrenDifList.get(NextStateNodeNow);
	}
	
	public int getNextChildNodeId()
	{
		
	  if(++NextStateNodeNow < NextStateNodeNum)
		  
		  return (int)childrenList.get(NextStateNodeNow);
	  else
		  return -1;
		
	}
	
}
