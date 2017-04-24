package com.bj58.test.local.auto;

import java.util.Scanner;

/**
 * 
 * @author 58
 *
 */

public class TestAz_2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入字符串： ");
		Scanner scanner = new Scanner(System.in);
		String string = scanner.nextLine();
		StaticLetterBlankTwo staticLetterBlank = new StaticLetterBlankTwo();
		staticLetterBlank.staticLetter(string);
	}

}

class StaticLetterBlankTwo {
	int staticLetterNum = 0;
	int staticBlank = 0;
	int staticChinese = 0;
	int staticOther = 0;

	void staticLetter(String str) {
		char[] a = str.toCharArray();
		for (int i = 0; i < a.length; i++) {
			if (Character.isLetter(a[i]))
				staticLetterNum++;
			else if (Character.isSpaceChar(a[i]))
				staticBlank++;
			else
				staticOther++;
		}
		System.out.println("字符中包含字母的个数： " + staticLetterNum);
		System.out.println("字符中包含空格的个数： " + staticBlank);
		System.out.println("字符中包含其他的个数： " + staticOther);
	}

}