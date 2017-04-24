package com.bj58.test.local.auto;

import java.util.ArrayList;

public class TestWanNumeber {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StaticWanNumber staticWanNumber = new StaticWanNumber();
		staticWanNumber.staticNumber(1000);
	}
}

class StaticWanNumber {

	public void staticNumber(int m) {

		for (int i = 2; i <= m; i++) {
			int j = 2;
			int n = i;
			ArrayList<Integer> list_1 = new ArrayList<Integer>();
			{
				System.out.print(i + "=1");
				while (n >= j) {
					if (n % j == 0) {
						System.out.print("*" + j);
						list_1.add(j);
						n = n / j;
					} else {
						j++;
					}
				}
				System.out.println();
				int sum = 1;
				for (int q = 0; q < list_1.size(); q++) {
					sum += list_1.get(q);
				}
				if (sum == i)
					System.out.println(i + "是完数");
			}
		}
	}
}