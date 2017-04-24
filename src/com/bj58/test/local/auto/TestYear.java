package com.bj58.test.local.auto;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestYear {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入年份：");
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader bReader = new BufferedReader(is);
		try {
			String name = bReader.readLine();
			int year = Integer.parseInt(name);
			System.out.println("通过BufferedReader输入的年份为： " + year);
			if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
				System.out.println(year + "年是闰年");
			} else {
				System.out.println(year + "年是平年");
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
