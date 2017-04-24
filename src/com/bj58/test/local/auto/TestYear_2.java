package com.bj58.test.local.auto;

import java.util.Scanner;

public class TestYear_2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] year = new int[3];
		int i = 0;
		System.out.println("请输入一串年份，并用逗号隔开： ");
		Scanner scanner = new Scanner(System.in);
		String stringYear = scanner.next().toString();
		String[] stringArray = stringYear.split(",");

		for (String tempYaer : stringArray) {
			year[i] = Integer.parseInt(tempYaer);
			System.out.println("输入的年份为： " + year[i]);
			i++;
		}

		for (int infactYear : year) {
			if (((infactYear % 4 == 0) && (infactYear % 100 != 0)) || (infactYear % 400 == 0)) {
				System.out.println(infactYear + "年是闰年");
			} else {
				System.out.println(infactYear + "年是平年");
			}
		}
	}

}
