package com.bj58.test.local.auto;

import java.util.Scanner;

public class TestPrime_3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入一个正整数:");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		int a = scanner.nextInt();
		int i = 2;

		if (a <= 2) {
			System.out.println(a + "=" + a + "*" + "1");
		} else {
			System.out.print(a + "=" + "1");
			while (a >= i) {
				if (a % i == 0) {
					System.out.print("*" + i);
					a = a / i;
				} else {
					i++;
				}
			}
		}

	}

}
