package com.bj58.test.local.auto;

import java.util.Arrays;

public class TestArrayAlist {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] strings = { "类别", "服务", "范围" };
		String text = "别";
		if (Arrays.asList(strings).contains(text)) {
			System.out.println("存在该类别");
		} else {
			System.out.println("不存在该类别");
		}

	}

}
