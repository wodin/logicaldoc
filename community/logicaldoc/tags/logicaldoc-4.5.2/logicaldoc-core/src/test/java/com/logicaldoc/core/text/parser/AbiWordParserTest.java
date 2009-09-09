package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.SuffixFileFilter;

public class AbiWordParserTest extends DefaultParserTest  {
	
	public void testParseFolder() throws IOException {

		File dir = new File("C:/tmp/abiword");
		
		String[] files = dir.list(new SuffixFileFilter(".abw"));
		for (int i = 0; i < files.length; i++) {
			parseFile(dir, files[i]);
		}

		System.err.println("Finished");
	}
	
	
	public void parseFile(File dir, String fileName) throws IOException {
		System.out.println(fileName);

		File file = new File(dir, fileName);

		AbstractParser parser = new AbiWordParser();
		parser.parse(file);
		
		System.out.println("content: \n" + parser.getContent());

		myContent = parser.getContent();
		
		saveTxtFile(file.getAbsolutePath(), "UTF-8");
	}

}
