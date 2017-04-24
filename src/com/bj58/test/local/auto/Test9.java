package com.bj58.test.local.auto;

public class Test9 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       
		for(int i=101;i<200;i++)
		{    boolean a=true;
			for(int j=2;j<i;j++)
			{
				if(i%j==0)
					a=false;
					break;
			}
		   if(a)
		   {
			  System.out.println(i); 
		   }
		}
	}

}
