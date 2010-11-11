package com.logicaldoc.core.text.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class PDFParserTest {
	
	@Test
	public void testParse() throws UnsupportedEncodingException {
		
		// This is a pdf document that PDFBox version 0.7.3 in not able to read
		// The new version of PDFBox 1.3.1 is able to open such document
		// This pdf has been created with Adobe PDF Library 9.0
		File file = new File(URLDecoder.decode(getClass().getClassLoader().getResource("Digital_Day.pdf").getPath(), "UTF-8"));		
		String filename = file.getPath();
		
		Parser parser = ParserFactory.getParser(filename);
		PDFParser pdfp = (PDFParser) parser;
		pdfp.parse(file);
		
		String title = pdfp.getTitle();
		System.out.println("title: " + title);
		assertTrue(StringUtils.isNotEmpty(title));
		assertEquals("Folie 1", title);
		
		String author = pdfp.getAuthor();
		System.out.println("author: " + author);
		assertTrue(StringUtils.isNotEmpty(author));
		assertEquals("Marcus Joost", author);
	
		String content = pdfp.getContent();
		assertNotNull(content);
		assertTrue(StringUtils.isNotEmpty(content));

		System.out.println("content.length(): " + content.length());
		assertTrue(content.length() == 27269);
		System.out.println("content : " + content);
	}

}
