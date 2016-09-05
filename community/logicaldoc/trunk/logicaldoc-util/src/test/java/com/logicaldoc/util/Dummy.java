package com.logicaldoc.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class Dummy {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Map<String, Charset> charsets = Charset.availableCharsets();
		for (String name : charsets.keySet()) {
			System.out.println(name + " " + charsets.get(name).displayName());
		}
	}

}
