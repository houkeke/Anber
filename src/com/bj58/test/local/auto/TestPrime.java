package com.bj58.test.local.auto;

public class TestPrime {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean a = false;
		int sum = 0;
		for (int i = 101; i <= 200; i++) {
			for (int j = 2; j < i; j++) {
				if (i % j != 0) {
					a = true;
					continue;
				} else {
					a = false;
					break;
				}
			}
			if (a) {
				sum++;
				System.out.println(i + " 是素数");
			} else {

			}
		}
		System.out.println("素数的个数为： " + sum);
	}

}
