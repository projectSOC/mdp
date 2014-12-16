package basic;

import java.util.ArrayList;
import java.util.List;

public class STATE {

	List <STATE_NODE> list = new ArrayList();
	STATE_NODE st_node;
	int S_total_num ;
	int state_node_now;
	int n,m;
	
	public STATE(int n_in,int m_in){   
		
		n = n_in;
		m = m_in;
		
		S_total_num = (int) Math.pow(m+1, n);
		
		state_node_now = 0;
		
		int mod = 0; //
		
		int mod_val = 0;
		
		int no_zero_num; //用于记录该状态中，已被分配过的任务的数量，该数量 == n时，就是所有任务已经被分配，代表了终止状态。
		
		for(int i = 0; i < S_total_num ; i++)
		{
									
			mod = i;
			st_node = new STATE_NODE(n,m);
			
			no_zero_num = 0;
			for(int j = n-1 ; j >= 0; j--)
			{
				
				mod_val = mod % (m + 1);//有m个VM
				
				if(mod_val != 0)
				{	
					no_zero_num ++;
					st_node.mat.set(j ,m - mod_val, 1);
				}
				
				mod /= (m+1);
			}
			
			if(no_zero_num == n)
			{
				st_node.setCost(0);

				st_node.setTimeCost(0);

				st_node.terminal = true;
			}
			
			list.add(st_node);
		
		}
		
		   GetTheirNextStateNode();
	}
	
	int[] isNextState(Mat parent, Mat child)
	{
		int Abs_Err_Sum = 0;
		int Err_Sum = 0;
		int ret[] = {-1,-1};
		
		///System.out.println("m :"+m+" "+"n:"+n);
		for(int i = 0; i < m ; i ++)
			for(int j = 0; j < n; j++)
			{
				Abs_Err_Sum += Math.abs(parent.get(j, i)-child.get(j, i));
				if(Abs_Err_Sum > 1) return ret;
			}
		
		   if(Abs_Err_Sum != 1)
			   return ret;
		   
		   for(int i = 0; i < m ; i ++)
				for(int j = 0; j < n; j++)
				{
					Err_Sum = child.get(j, i) - parent.get(j, i);
					if(Err_Sum == 1) 
						{   
						    ret[0] = j;
						    ret[1] = i;
							return ret;
					
						}
				}
		   
		   return ret;
	}
	
	void GetTheirNextStateNode()
	{
		for(int i = 0; i < S_total_num ; i++)
		{
								
			st_node = list.get(i);
			Mat parent = st_node.mat;
			
			
			for(int j = 0; j < S_total_num; j++)
			{   
			
				
				if(i == j) continue;
				Mat child = list.get(j).mat;
				
				int ret[] = isNextState(parent,child);
				
				if(ret[1] != -1)
				{
					st_node.addNestStateNodeById(j,ret);
					
				}
				
			}
			
			
		
		}
		
		
	}
	
	void show()
	{
		
		for(int i = 0; i < S_total_num; i++)
		{
			System.out.println("\nid : "+(i+1));
			list.get(i).mat.MatShow();
			System.out.println("\ncost: "+list.get(i).cost);

			System.out.println("\ntime cost: "+list.get(i).getTimeCost());		
			
		}
	
		
	}
	
	void showCost()
	{
		for(STATE_NODE sta_node = getStartStateNode(); sta_node != null;sta_node = getNextStateNode())
		{
		   
			System.out.println(sta_node.getCost());

			System.out.println(sta_node.getTimeCost());
		}
	}
	
	public void showResult()
	{
		int i = 1;
//		for(STATE_NODE sta_node = getStartStateNode(); sta_node != null;sta_node = getNextStateNode())
//		{
//			System.out.print("State :" + i);
//			i++;
//			sta_node.mat.MatShow();
//			
//			System.out.println("\nCost :"+sta_node.getCost());
//			
//			if(sta_node.terminal)
//			{
//				System.out.println("all finished\n");
//			}
//			else
//			{
//				System.out.println("Trans :"+"compnent "+sta_node.trans[0]+" -> Vitural Machine "+sta_node.trans[1]+"\n");
//			}
//		}
		
		STATE_NODE sta_node_start = getStartStateNode();
		double total_cost = sta_node_start.getCost();

		double totalTimeCost = sta_node_start.getTimeCost();
		for(STATE_NODE sta_node = getStartStateNode(); sta_node != null;sta_node = getNextStateNode())
		{
			//System.out.print("State :" + i);
			//i++;
			//sta_node.mat.MatShow();
			
			//System.out.println("\nCost :"+sta_node.getCost());
			
			if(sta_node.terminal)
			{
				System.out.println("Cost :"+total_cost);

				System.out.println("Time : "+totalTimeCost);

				sta_node.mat.MatShow();
				sta_node.mat.MatShowDescribe();
				System.out.println("\n");
			}
//			else
//			{
//				System.out.println("Trans :"+"compnent "+sta_node.trans[0]+" -> Vitural Machine "+sta_node.trans[1]+"\n");
//			}
		}
	}
	
	public STATE_NODE getStartStateNode()
	{
		state_node_now = 0;
		
		return list.get(state_node_now);
		
	}
	
	public STATE_NODE getNextStateNode()
	{
		
		state_node_now ++;
		
		if(state_node_now < S_total_num)
		{
			return list.get(state_node_now);
		}
		else
		{
			return null;
			
		}
	}
	
	
	public STATE_NODE getStateNodeById(int sta_node_id)
	{
		
		if(sta_node_id < S_total_num)
		{
			return list.get(sta_node_id);
		}
		else
		{
			return null;
		}
	}
	
	
	
}
