package com.logicaldoc.web;

public class Main {

	public static void main(String[] args) {
		StringBuffer sb=new StringBuffer("/");
		for (int i = 0; i < 200; i++) {
			sb.append("folder");
			sb.append(Integer.toString(i+1));
			sb.append("/");
		}

		System.out.println(sb.toString());
	}

}
