package com.bj58.test.local.auto;

public class TestBubbleSort {
	public  void bubbleSort(int[] b){
		for(int i=0;i<10;i++)
		{
			for(int j=i+1;j<10;j++)
				{
				if(b[i]>b[j])
				{   int temp=b[i];
					b[i]=b[j];
					b[j]=temp;
				}
				}
		}
		for(int i=0;i<10;i++)
			System.out.println(b[i]);
		 
 	}
	public void Test1 (int a[]) {
		bubbleSort(a);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a;
		int []is= new int[7];
		System.out.println(is[5]+"*********");
		a=new int[]{1,3,5,7,9,2,4,6,8,0};
		TestBubbleSort testBubbleSort = new TestBubbleSort();
		testBubbleSort.bubbleSort(a);
		
	}


}
