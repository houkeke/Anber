package com.bj58.test.local.auto;

import java.io.File;

public class TestFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f1 = new File("D:\\学习资料", "1.txt");
		File f2 = new File("D:\\学习资料\\2.txt");
		try {
			f2.createNewFile();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
