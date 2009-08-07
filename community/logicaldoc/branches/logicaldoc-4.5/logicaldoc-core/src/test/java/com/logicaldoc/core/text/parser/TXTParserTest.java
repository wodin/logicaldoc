package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.logicaldoc.util.CharsetDetector;

public class TXTParserTest extends TestCase  {
	
	private String myContent;

	public void EEEEEtestDetectCharset() throws IOException {

	StringBuilder sb = new StringBuilder();
	
		File file = new File("C:/tmp/txts/application_zh.txt");
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		LineNumberReader lnr = new LineNumberReader(isr);
		
		String xxx = null;
		while((xxx = lnr.readLine()) != null) {
			sb.append(xxx);
			sb.append("\n");
		}

		fis.close();
		
		String[] encodings = CharsetDetector.detectEncodings(sb.toString());
		for (String enc : encodings) {
			System.out.println("enc: " + enc);
		}
		
		System.err.println("Finished");
	}	
	
	public void testParseTXTFolder() throws IOException {

		File dir = new File("C:/tmp/txts");
		
		String[] files = dir.list(new SuffixFileFilter(".txt"));
		for (int i = 0; i < files.length; i++) {
			parseFile(dir, files[i]);
		}

		System.err.println("Finished");
	}
	
	
	private void parseFile(File dir, String fileName) throws IOException {
		System.out.println(fileName);

		File file = new File(dir, fileName);

		AbstractParser parser = new TXTParser();
		parser.parse(file);
		
		//System.out.println("content: \n" + parser.getContent());

		myContent = parser.getContent();
		
		saveTxtFile(file.getAbsolutePath(), "UTF-8");
	}



	private File processFileName(String filename) {
		// Do no overwrite existing file
		File file = new File(filename);

		String fname = file.getName();
		fname = fname.substring(0, fname.lastIndexOf("."));
		fname += ".parsed";
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
