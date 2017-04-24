package com.bj58.test.local.auto;

public class Test16 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    Dot a=new Dot(1, 2.1, 2);
    System.out.println(a.showDistance());
	}
}

class Dot{
	double x;
	double y;
	double z;
	public Dot(double x,double y,double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public double showDistance(){
		return Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2);
		
	}
}