package com.bj58.test.local.auto;

import java.util.Scanner;

public class TestAaa {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入一个一位的正整数： ");
		System.out.println("请输入一个正整数： ");
		Scanner scanner_1 = new Scanner(System.in);
		Scanner scanner_2 = new Scanner(System.in);
		int a = scanner_1.nextInt();
		int n = scanner_2.nextInt();
		StatictisSum statictisSum = new StatictisSum();
		statictisSum.showSum(a, n);
	}

}

class StatictisSum {
	int m;
	int k;
	int sum;
	int temp;

	void showSum(int m, int k) {
		sum = m;
		temp = m;
		for (int i = 1; i < k; i++) {
			temp = temp * 10 + m;
			sum += temp;
		}
		System.out.println("sum= " + sum);
	}
}