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
		
		int no_zero_num; //鐢ㄤ簬璁板綍璇ョ姸鎬佷腑锛屽凡琚垎閰嶈繃鐨勪换鍔＄殑鏁伴噺锛岃鏁伴噺 == n鏃讹紝灏辨槸鎵�湁浠诲姟宸茬粡琚垎閰嶏紝浠ｈ〃浜嗙粓姝㈢姸鎬併�
		
		for(int i = 0; i < S_total_num ; i++)
		{
									
			mod = i;
			st_node = new STATE_NODE(n,m);
			
			no_zero_num = 0;
			for(int j = n -1 ; j >= 0; j--)
			{
				
				mod_val = mod % (m + 1);//鏈塵涓猇M
				
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
			
			
			
		}
	
		
	}
	
	void showCost()
	{
		for(STATE_NODE sta_node = getStartStateNode(); sta_node != null;sta_node = getNextStateNode())
		{
		   
			System.out.println(sta_node.getCost());
		
		}
	}
	
	public void showResult()
	{
		int i = 1;
		for(STATE_NODE sta_node = getStartStateNode(); sta_node != null;sta_node = getNextStateNode())
		{
			System.out.print("State :" + i);
			i++;
			sta_node.mat.MatShow();
			
			System.out.println("\nCost :"+sta_node.getCost());
			
			if(sta_node.terminal)
			{
				System.out.println("all finished\n");
			}
			else
			{
				System.out.println("Trans :"+"compnent "+sta_node.trans[0]+" -> Vitural Machine "+sta_node.trans[1]+"\n");
			}
		}
	}
	
	public void showState(int m, int[] bst_data)
	{
		for(int i = 0; i < m+1 ; i++){
			STATE_NODE sta_node = getStateNodeById(bst_data[i]);
			System.out.printf("State "+(bst_data[i]+1));
			sta_node.mat.MatShow();
			System.out.printf("\n");
    		
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
