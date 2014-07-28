package com.logicaldoc.util;

import java.io.File;
import java.io.IOException;

import com.logicaldoc.util.config.ContextProperties;

public class Dummy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		ContextProperties test=new ContextProperties(new File("C:\\Users\\marco\\workspace-logicaldoc\\community\\logicaldoc\\logicaldoc-util\\context.properties"));
		System.out.println(test.getProperty("conf.dbdir"));
		test.setProperty("test", "C:\\pollo\\collo\\ciccio");
		
		test.write();
	}

}
