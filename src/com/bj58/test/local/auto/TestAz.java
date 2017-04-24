package com.bj58.test.local.auto;

import java.util.Scanner;

public class TestAz {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("请输入字符串： ");
		Scanner scanner = new Scanner(System.in);
		String string = scanner.nextLine();
		StaticLetterBlank staticLetterBlank = new StaticLetterBlank();
		staticLetterBlank.staticLetter(string);
	}

}

class StaticLetterBlank {
	int staticLetterNum = 0;
	int staticBlank = 0;
	int staticChinese = 0;
	int staticOther = 0;

	void staticLetter(String str) {
		String regexLetter = "[a-zA-Z]";
		// String regexChinese = "[u4e00-u9fa5]";
		String regexChinese = "[\u4E00-\u9FA5]";
		String regexBlank = "[\\s]";
		System.out.println("字符串的长度： " + str.length());
		for (int i = 0; i < str.length(); i++) {
			if (String.valueOf(str.charAt(i)).matches(regexLetter))
				staticLetterNum++;
			else if (String.valueOf(str.charAt(i)).matches(regexChinese))
				staticChinese++;
			else if (String.valueOf(str.charAt(i)).matches(regexBlank))
				staticBlank++;
			else {
				staticOther++;
			}
		}
		System.out.println("字符串中包含字符的个数： " + staticLetterNum);
		System.out.println("字符串中包含汉字的个数： " + staticChinese);
		System.out.println("字符串中包含空格的个数： " + staticBlank);
		System.out.println("字符串中包含其他字符的个数： " + staticOther);
	}

}