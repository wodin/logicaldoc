package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TXTParserTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testParse() throws UnsupportedEncodingException, FileNotFoundException {
		String inputFile = "target/test-classes/AnalyzeFileTest_enc.txt";
		File file = new File(inputFile);
		String filename = file.getPath();
		Parser parser = ParserFactory.getParser(filename);
		TXTParser p = (TXTParser) parser;
		p.parse(file);
		Assert.assertTrue(p.getContent().contains("scalpo"));
		p.parse(new FileInputStream(inputFile));
		Assert.assertTrue(p.getContent().contains("scalpo"));
	}
}