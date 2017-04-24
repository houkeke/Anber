package com.bj58.test.local.auto;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestFreesia {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a = 0, b = 0, c = 0, d = 0, f = 0, sum = 0;
		System.out.println("请输入一个三位数： ");
		InputStreamReader iStreamReader = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(iStreamReader);
		try {
			String string = bufferedReader.readLine();
			a = Integer.parseInt(string);
			b = a % 10;
			d = a / 10;
			c = d % 10;
			f = d / 10;
			sum = (int) (Math.pow(b, 3) + Math.pow(c, 3) + Math.pow(f, 3));
			if (sum == a)
				System.out.println(a + " 是水仙花数");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
