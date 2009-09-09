package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

public abstract class DefaultParserTest extends TestCase  {
	
	protected String myContent;

	public abstract void testParseFolder() throws IOException;
	
	public abstract void parseFile(File dir, String fileName) throws IOException;

	protected File processFileName(String filename) {
		// Do no overwrite existing file
		File file = new File(filename);

		String fname = file.getName();
		fname = fname.substring(0, fname.lastIndexOf("."));
		fname += ".txt";
		file = new File(file.getParent(), fname);

		return file;
	}

	protected void saveTxtFile(String filename, String encoding) throws IOException {

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
