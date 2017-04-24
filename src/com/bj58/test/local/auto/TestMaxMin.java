package com.bj58.test.local.auto;

public class TestMaxMin {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ShowMaxMinum f = new ShowMaxMinum();
		int m = f.ShowMax(6, 8);
		int n = f.ShowMin(6, 8);
		System.out.println("���" + m + " ��С" + n);

	}

}

class ShowMaxMinum {
	public int ShowMax(int a, int b) {
		return a > b ? a : b;
	}

	public int ShowMin(int c, int d) {
		return c < d ? c : d;
	}
}