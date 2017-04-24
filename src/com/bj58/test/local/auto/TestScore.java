package com.bj58.test.local.auto;

import java.util.Scanner;

public class TestScore {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入一个正整数:");
		Scanner scanner = new Scanner(System.in);
		int score = scanner.nextInt();
		if (score >= 90) {
			System.out.println("score= " + "A");
		} else if ((score > 60) && (score <= 89)) {
			System.out.println("score= " + "B");
		} else {
			System.out.println("score= " + "C");
		}

	}

}
