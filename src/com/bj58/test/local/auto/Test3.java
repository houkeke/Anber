package com.bj58.test.local.auto;

public class Test3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double x=3;
		double y=4;
		double x0=0;
		double y0=1;
		double r=5;
		double Subtract=(x-x0)*(x-x0)+(y-y0)*(y-y0);
		if(Subtract>r*r)
		{
			System.out.println("点在圆外");
		}
		else {
			System.out.println("点在圆内");
		}
	}

}
