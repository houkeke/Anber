package com.bj58.test.local.auto;

public class TestFreesia_2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int b = 0, c = 0, sum;
		for (int i = 100; i < 999; i++) {
			sum = 0;
			for (int j = i; j > 0; j = j / 10) {
				b = j % 10;
				sum += (int) (Math.pow(b, 3));
			}
			if (sum == i) {
				System.out.println("sum= " + sum + "****" + i + " 是水仙花数");
			} else {
				// System.out.println("sum= " + sum + "****" + i + " 不是水仙花数");
			}
		}

	}

}
