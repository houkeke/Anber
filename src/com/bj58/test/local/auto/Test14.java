package com.bj58.test.local.auto;

import org.omg.CORBA.PUBLIC_MEMBER;

public class Test14 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test14 a=new Test14();
		System.out.println(a.fibonac(6));
		}
		
	public long fibonac(int n){
		if(n==1||n==2)
		    return 1;
		else
			return fibonac(n-1)+fibonac(n-2);

	}
	

}
