package com.logicaldoc.core;

public class Test {
	public static void main(String[] args) {
		int rights = 123;
		String map = Integer.toBinaryString(rights);
		System.out.println(map);

		
		int a = Integer.parseInt("111111111011",2);
		System.out.println(a);
		int mask= Integer.parseInt("0000010",2);
		System.out.println((a&mask) +" - "+Integer.toString(a&mask, 2));
	}
}
