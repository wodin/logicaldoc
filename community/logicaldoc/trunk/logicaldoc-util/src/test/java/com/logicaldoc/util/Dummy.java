package com.logicaldoc.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;

import com.logicaldoc.util.config.ContextProperties;

public class Dummy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String src="guide � (but, yeah, it’s okay to share it with ‘em).";
		System.out.println(StringUtil.removeNonUtf8Chars(src));
		
		ContextProperties test=new ContextProperties(new File("context.properties"));
		System.out.println(test.getProperty("conf.dbdir"));
		test.setProperty("test", "C:\\pollo\\collo\\ciccio");
		
		test.write();
	}

}
