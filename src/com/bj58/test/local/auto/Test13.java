package com.bj58.test.local.auto;

public class Test13 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    Test13 a=new Test13();
	int result=a.method(5);
	System.out.println(result);
	}

	public int method(int n){
	    if(n==1)
		return 1;
	    else
	    	return n*method(n-1);
	}
	
}


