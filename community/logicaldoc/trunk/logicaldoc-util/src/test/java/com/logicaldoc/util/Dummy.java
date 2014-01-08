package com.logicaldoc.util;

import java.nio.charset.CharacterCodingException;

public class Dummy {

	/**
	 * @param args
	 * @throws CharacterCodingException 
	 */
	public static void main(String[] args) throws CharacterCodingException {
		String src="guide � (but, yeah, it’s okay to share it with ‘em).";
		System.out.println(StringUtil.removeNonUtf8Chars(src));
	}

}
