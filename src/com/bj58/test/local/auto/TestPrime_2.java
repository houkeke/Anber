package com.bj58.test.local.auto;

public abstract class TestPrime_2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean flag = true;
		for (int i = 101; i <= 200; i++) {
			for (int j = 2; j < i; j++) {
				if (i % j == 0) { // 如果i%j==0的话，说明可以被整除，不是素数！
					flag = false; // 就将布尔值改为false；
					break; // 就跳出这次的循环，执行i自增1的操作！
				} else {
					flag = true;
				}
			}
			if (flag)// 一个循环结束，如果flag为真，则说明没有被整除，是素数，
				System.out.println(i + "是素数！");
			else // 反之不是
				System.out.println(i + "不是素数");
		}

	}

}
