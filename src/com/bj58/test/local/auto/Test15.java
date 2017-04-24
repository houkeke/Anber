package com.bj58.test.local.auto;

public class Test15 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str1="abcdef";
		String str2="ÄãºÃ";
		System.out.println(str1.length());
		System.out.println(str2.length());
		System.out.println(str1.charAt(2));
		for(int i=0;i<str1.length();i++)
			System.out.println(str1.charAt(i));
		String str3="abcdefghi";
		String str4="l";
		System.out.println(str3.indexOf(str4));
		System.out.println(str3.substring(2));
		System.out.println(str3.substring(2, 4));
		System.out.println(str3.subSequence(2, 4));

	}
	

	
}
