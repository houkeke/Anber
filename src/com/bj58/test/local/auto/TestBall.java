package com.bj58.test.local.auto;

public class TestBall {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StaticDistance distance = new StaticDistance();
		distance.showDistance(10, 100);
	}

}

class StaticDistance {
	int intialHeight;
	int times;

	public void showDistance(int times, int intialHeight) {
		double sum = intialHeight;
		double height = intialHeight;
		for (int i = 1; i < times; i++) {
			sum += height;
			height /= 2;
		}
		System.out.println("第" + intialHeight + "次落地的时候的共经过的路程： " + sum);
	}
}