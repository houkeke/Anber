package com.bj58.test.local.auto;

import java.util.ArrayList;
import java.util.Scanner;

public class TestGongyueshu {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入两个正整数： ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		int a = scanner.nextInt();
		int b = scanner.nextInt();
		System.out.println("两个正整数为： " + a + "," + b);
		ArrayList arrayList_1 = new ArrayList();
		ArrayList arrayList_2 = new ArrayList();
		ArrayList<Integer> list_1 = new ArrayList<Integer>();
		ArrayList<Integer> list_2 = new ArrayList<Integer>();

		int i = 2;
		int j = 2;
		int m = a;
		int n = b;
		int product = 1;
		int multiple = 1;
		if (a < 2 || b < 2) {
			product = 1;
			// System.out.println("最大的公约数为1");
		} else if (a == 2 || b == 2) {
			product = 2;
			// System.out.println("最大的公约数为" + product);
		} else {
			while ((a > i) && (b > i)) {
				if ((a % i == 0) && (b % i == 0)) {
					a = a / i;
					b = b / i;
					product *= i;
				} else {
					i++;
				}
			}
		}
		System.out.println("最大公约数: " + product);

		if ((m >= n)) {
			if (m % n == 0)
				multiple = a;
			else {
				if ((m * j) % n == 0) {
					multiple = m * j;
				} else {
					j++;
				}
			}
		} else if ((m < n)) {
			if (n % m == 0)
				multiple = n;
			else {
				if ((n * j) % m == 0) {
					multiple = n * j;
				} else {
					j++;
				}
			}
		}
		System.out.println("最小公倍数: " + multiple);
	}

}
