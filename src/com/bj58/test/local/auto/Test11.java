package com.bj58.test.local.auto;

public class Test11 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BirthDate a = new BirthDate(1999, 2, 28);
		a.ShowBirthDate();
		a.getYear();
	}

}

class BirthDate {
	int year;
	int month;
	int day;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public BirthDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public void ShowBirthDate() {
		System.out.println(year + "年" + month + "月" + day + "日");

	}
}
