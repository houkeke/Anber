package com.bj58.test.local.auto;

public class Test19 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Person a=new Person(18,"С��");
		// System.out.println(a.getAge()+a.getName());
		Person a = new Person();
		a.setAge(18);
		a.setName("你好");
		int b = a.getAge();//
		String c = a.getName();
		System.out.println(c + "***" + b);
	}
}

class Person {
	int age;
	String name;

	/*
	 * public Person(int age,String name){ this.age=age; this.name=name;
	 * 
	 * }
	 */
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}