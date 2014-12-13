package basic;

public class Mat {

	int mat[][];
	int col;
	int row;
	int i , j , k;
	
	public Mat(int row_in,int col_in,int default_val[])
	{
		
		col = col_in;
		row = row_in;
		mat = new int[col][row];
		
		for(i = 0; i < col; i++)
			for(j = 0 ; j < row ; j++)
			{
				mat[i][j] = default_val[i];
			}
		//MatClear();
	}
	
	void MatClear()//clear the matrix
	{
		
		for(i = 0; i < col; i++)
			for(j = 0 ; j < row ; j++)
			{
				mat[i][j] = 0;
			}
	}
	
	void MatShow()//show the matrix
	{
		for(i = 0; i < col; i++)
		{
			System.out.println("");
			for(j = 0 ; j < row ; j++)
			{
				System.out.print(mat[i][j]+ " ");
			}
			
		}
		
	}
	
	public int get(int row_in,int col_in)
	{
		////System.out.println("col "+col+"row:"+row);
		return mat[col_in][row_in];
		
	
	}
	
	
	void set(int row_in,int col_in,int val)
	{
		
		mat[col_in][row_in] = val;
		
	}
	void MatShowDescribe()//describe the matrix
	{
		for(i = 0; i < col; i++)
		{
			for(j = 0 ; j < row ; j++)
			{
				if(mat[i][j]==1){
					System.out.println("component "+j+" -> Virtual Machine "+i);
				}
				
			}
			
		}
		
	}


}
