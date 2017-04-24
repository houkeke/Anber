package com.bj58.test.local.auto;

//import java.sql.Date;
import java.util.Date;

public class TestArrayDate {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a1 = new int[3];
		Date[] d = new Date[3];
		a1[0] = 100;
		a1[2] = 200;
		d[0] = new Date();
		d[2] = new Date();
		int len = a1.length;
		System.out.println("a1的长度：" + len);
		for (int i = 0; i < a1.length; i++)
			System.out.println(a1[i]);
		for (int i = 0; i < d.length; i++)
			System.out.println("d" + "[" + i + "]=" + d[i]);
	}

}
