package com.bj58.test.local.auto;

public class TestStatic {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * Flower.name="�ٺ�"; Flower.ShowInfo();
		 */
		Flower a = new Flower("rose", 1);
		a.ShowInfo();
	}

}

class Flower {
	String name;
	int age;

	public Flower(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public void ShowInfo() {
		System.out.println("名字是" + name);
	}
}

/*
 * class Flower{ static String name; static int age; public Flower(String
 * name,int age){ this.name=name; this.age=age; } public static void ShowInfo(){
 * System.out.println("�����ǣ� "+name); } }
 */