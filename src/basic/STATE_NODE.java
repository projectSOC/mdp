package basic;

import java.util.ArrayList;
import java.util.List;

import Test.WorkflowMDP;

public class STATE_NODE {

	Mat mat;
	double cost;
	List <Integer>  childrenList; //瀛樻斁浜嗗瓙鐘舵�鐨勭紪鍙�	
	List <int []>  childrenDifList;//瀛愮姸鎬佺紪鍙�	
	
	int NextStateNodeNum; //鎵�湁鍙兘鐨勪笅涓�鐨勪釜鏁�possible鐨勫瓙鐘舵�
	int NextStateNodeNow; //褰撳勾閬嶅巻鍒扮殑瀛愮姸鎬佺殑缂栧彿
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
	
	void addNestStateNodeById(int id,int[] dif)
	{
		//dif0 component dif1 vm action action鏄皢浠诲姟dif[0]鍒嗛厤鍒癡M dif[1]
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
