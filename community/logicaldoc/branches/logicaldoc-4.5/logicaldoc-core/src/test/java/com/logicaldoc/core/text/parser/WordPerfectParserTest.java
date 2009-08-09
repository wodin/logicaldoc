package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.logicaldoc.core.text.parser.wordperfect.WordPerfectParser;

public class WordPerfectParserTest extends DefaultParserTest {

	public void testParseFolder() throws IOException {

		File dir = new File("C:/tmp/wordperfect");
		
		String[] files = dir.list(new SuffixFileFilter(".wpd"));
		for (int i = 0; i < files.length; i++) {
			parseFile(dir, files[i]);
		}

		System.err.println("Finished");
	}
	
	
	public void parseFile(File dir, String fileName) throws IOException {
		System.err.println(fileName);

		File file = new File(dir, fileName);

		AbstractParser parser = new WordPerfectParser();
		parser.parse(file);
		
		//System.out.println("content: \n" + parser.getContent());

		myContent = parser.getContent();
		
		saveTxtFile(file.getAbsolutePath(), "UTF-8");
	}
}
