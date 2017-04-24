package com.bj58.test.local.auto;

public class Test6 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
        
		for(int i=100;i<=1000;i++)
		{	int sum=0;
		    int n=0;
		    for(int j=i;j>0;j/=10)
			   {
				 n=j%10;
				// sum+=n*n*n;
				 sum+=Math.pow(n, 3);	   
			   }
		    if(i==sum)
		    {
		    	System.out.println("i= "+i);
		    }
			
		 }
		
	}

}
