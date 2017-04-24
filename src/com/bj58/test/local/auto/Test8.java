package com.bj58.test.local.auto;

public class Test8 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i = 1;
		int m = 0;
		while (i <= 100) {
			if (i % 3 == 0) {
				m++;
				System.out.println(i);
			}
			if (m > 5)
				break;
			i++;
		}

	}

}
