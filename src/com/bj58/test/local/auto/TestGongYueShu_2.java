package com.bj58.test.local.auto;

import java.util.Scanner;

public class TestGongYueShu_2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入两个正整数： ");
		Scanner scanner_1 = new Scanner(System.in);
		Scanner scanner_2 = new Scanner(System.in);
		int a = scanner_1.nextInt();
		int b = scanner_2.nextInt();
		MaxProductMutiple aMaxProduct = new MaxProductMutiple();
		System.out.println("最大公约数为： " + aMaxProduct.showMaxduct(a, b));
		System.out.println("最小公倍数为： " + aMaxProduct.showMutiple(a, b));
	}
}

class MaxProductMutiple {
	int i = 2;
	int j = 2;
	int product = 1;
	int mutiple = 1;

	int showMaxduct(int a, int b) {
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
		// System.out.println("最大公约数: " + product);
		return product;
	}

	int showMutiple(int c, int d) {
		if (c < d) {
			int temp = c;
			c = d;
			d = temp;
		}
		if ((c * j) % d == 0) {
			mutiple = c * j;
		} else {
			j++;
		}
		return mutiple;
	}
}