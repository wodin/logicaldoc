package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

import org.apache.commons.io.filefilter.SuffixFileFilter;

public class HTMLParserTest extends TestCase {

	private String myContent;
	
	public void testParseHTMLFolder() throws IOException {
		
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

	
	
	private void parseFile(File dir, String fileName) throws IOException {
		System.out.println(fileName);

		File file = new File(dir, fileName);

		HTMLParser parser = new HTMLParser();
		parser.parse(file);
		
		System.out.println("content: \n" + parser.getContent());

		myContent = parser.getContent();
		
		saveTxtFile(file.getAbsolutePath(), "UTF-8");
		
	}



	private File processFileName(String filename) {
		// Do no overwrite existing file
		File file = new File(filename);

		String fname = file.getName();
		fname = fname.substring(0, fname.lastIndexOf("."));
		fname += ".txt";
		file = new File(file.getParent(), fname);

		return file;
	}

	public void saveTxtFile(String filename, String encoding) throws IOException {

		if (myContent == null || myContent.length() == 0)
			return;

		File file = processFileName(filename);

		OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file), encoding);
		BufferedWriter bw = new BufferedWriter(output);

		char[] sss = myContent.toCharArray();

		CharArrayReader car = new CharArrayReader(sss);
		BufferedReader br = new BufferedReader(car);
		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			bw.write(inputLine);
			bw.newLine();
		}

		br.close();
		bw.close();
	}

}
