package com.logicaldoc.web;

public class Main {

	public static void main(String[] args) {
		String[] names="pippo".split("\\,");
		for (String name : names) {
			System.out.println(name);
		}
	}

}
