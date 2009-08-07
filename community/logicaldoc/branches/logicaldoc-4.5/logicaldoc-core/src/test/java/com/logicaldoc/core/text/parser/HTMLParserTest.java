package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.SuffixFileFilter;

public class HTMLParserTest extends DefaultParserTest  {

	
	public void testParseFolder() throws IOException {
		
		File dir = new File("C:/tmp/htmls");
		
		String[] files = dir.list(new SuffixFileFilter(".html"));
		for (int i = 0; i < files.length; i++) {
			parseFile(dir, files[i]);
		}
		
		files = dir.list(new SuffixFileFilter(".htm"));
		for (int i = 0; i < files.length; i++) {
			parseFile(dir, files[i]);
		}

		System.err.println("Finished");
	}
	
	public void parseFile(File dir, String fileName) throws IOException {
		System.out.println(fileName);

		File file = new File(dir, fileName);

		HTMLParser parser = new HTMLParser();
		parser.parse(file);
		
		System.out.println("content: \n" + parser.getContent());

		myContent = parser.getContent();
		
		saveTxtFile(file.getAbsolutePath(), "UTF-8");
		
	}


}
