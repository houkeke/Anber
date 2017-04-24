package com.bj58.test.local.auto;

public class TestMaxMin_2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ShowMaxMin f = new ShowMaxMin();
		int m = f.ShowMax(6, 8);
		int n = f.ShowMin(6, 8);
		System.out.println("���还好" + m + " ��С" + n);

	}

}

class ShowMaxMin {
	public int ShowMax(int a, int b) {
		return a > b ? a : b;
	}

	public int ShowMin(int c, int d) {
		return c < d ? c : d;
	}
}