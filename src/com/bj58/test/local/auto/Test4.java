package com.bj58.test.local.auto;

public class Test4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * int sum; int result=0; for(int i=1;i<=10;i++) { sum=1; for(int
		 * j=1;j<=i;j++) { sum*=j;
		 * 
		 * } result+=sum; } System.out.println("结果是："+result);
		 */
		long result = 0;
		long sum = 1;
		for (int i = 1; i <= 10; i++) {
			sum *= i;
			result += sum;
		}
		System.out.println("result= " + result);

	}

}
