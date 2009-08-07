package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.SuffixFileFilter;

public class DOCParserTest  extends DefaultParserTest  {

	
	public void testParseFolder() throws IOException {

		if (true)
			return;
		
		File dir = new File("C:/tmp/msoffice");
		
		String[] files = dir.list(new SuffixFileFilter(".doc"));
		for (int i = 0; i < files.length; i++) {
			parseFile(dir, files[i]);
		}

		System.err.println("Finished");
	}
	
	
	public void testParseFile() throws IOException {

		File dir = new File("C:/tmp/msoffice");


		parseFile(dir, "huikao3.doc");
		System.err.println("Finished");
	}
	
	
	public void parseFile(File dir, String fileName) throws IOException {
		System.out.println(fileName);

		File file = new File(dir, fileName);

		AbstractParser parser = new DOCParser();
		parser.parse(file);
		
		System.out.println("content: \n" + parser.getContent());

		myContent = parser.getContent();
		
		saveTxtFile(file.getAbsolutePath(), "UTF-8");
	}

}
