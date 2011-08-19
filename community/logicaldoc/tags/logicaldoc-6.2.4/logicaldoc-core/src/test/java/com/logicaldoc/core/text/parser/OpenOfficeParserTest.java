package com.logicaldoc.core.text.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OpenOfficeParserTest {

	private long startTime;

	private long mem1;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.startTime = System.currentTimeMillis();
		this.mem1 = Runtime.getRuntime().totalMemory();
		System.out.println("freeMemory: " + Runtime.getRuntime().freeMemory());
		System.out.println("totalMemory: " + Runtime.getRuntime().totalMemory());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		long elapsedMillis = System.currentTimeMillis() - this.startTime;
		System.err.println("elapsedMillis: " + elapsedMillis);
		long mem2 = Runtime.getRuntime().totalMemory();

		System.err.println("freeMemory AFTER: " + Runtime.getRuntime().freeMemory());
		System.err.println("totalMemory AFTER: " + Runtime.getRuntime().totalMemory());

		System.err.println("Difference in memory allocation: " + ((mem2 - mem1) / 1024) + " KB");
		Runtime.getRuntime().gc(); // request garbage collection
	}

	@Test
	public void testParse() throws UnsupportedEncodingException {

		String inputFile = "src/test/resources/logicaldoc-user_manual-en.odt";
		File file = new File(inputFile);
		String filename = file.getPath();

		for (int i = 0; i < 10; i++) {
			Parser parser = ParserFactory.getParser(filename);
			OpenOfficeParser p = (OpenOfficeParser) parser;
			p.parse(file);

			String content = p.getContent();
			assertNotNull(content);
			assertTrue(StringUtils.isNotEmpty(content));

			assertEquals(70125, content.length());
		}
	}
}
