package com.logicaldoc.core.text.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
		assertTrue(content.length() == 27179);
		System.out.println("content : " + content);
	}
	
	@Test
	public void testParseArabic() throws UnsupportedEncodingException {
		
		// This is a pdf document with two (2) columns, one english and one Arabic on the right
		// The text in the left column is left aligned, while the text in the right goes from right to left (Arabic)
		// The documentation of PDFBox 1.4.0 states that this requires ICU4J 3.8
		String inputFile = "target/test-classes/Arabic/imaging14.pdf";
		String outputFile = "C:/tmp/testdocs/imaging14_icu4j.txt";
		File file = new File(inputFile);		
		String filename = file.getPath();
		
		Parser parser = ParserFactory.getParser(filename);
		PDFParser pdfp = (PDFParser) parser;
		pdfp.parse(file);
		
		String title = pdfp.getTitle();
		System.out.println("title: " + title);
		assertTrue(StringUtils.isNotEmpty(title));
		assertEquals("Microsoft Word - Systems Ltd.doc", title);
		
		String author = pdfp.getAuthor();
		System.out.println("author: " + author);
		assertTrue(StringUtils.isNotEmpty(author));
		assertEquals("adham", author);
		
		// Testing for SourceDate
		String sourceDate = pdfp.getSourceDate();
		System.out.println("sourceDate: " + sourceDate);
		assertTrue(StringUtils.isNotEmpty(sourceDate));
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.MONTH, 5);
		calendar.set(Calendar.YEAR, 2006);
		Date date = calendar.getTime();
		String testDate = DateFormat.getDateInstance().format(date);
		assertEquals(testDate, sourceDate);
	
		String content = pdfp.getContent();
		assertNotNull(content);
		assertTrue(StringUtils.isNotEmpty(content));

		System.err.println("content.length(): " + content.length());
		assertTrue(content.length() == 3001);
		System.out.println("content : " + content);
		
//		try {
//			FileOutputStream out = new FileOutputStream(outputFile);
//			BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//			BW.write(content);			
//			BW.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}	
	
	@Test
	public void testParseArabic2() throws UnsupportedEncodingException {
		
		// This is an Arabic pdf document 
		// The text goes from right to left (Arabic)
		// The documentation of PDFBox 1.4.0 states that this requires ICU4J 3.8
		String inputFile = "target/test-classes/Arabic/SharePoint.pdf";
		String outputFile = "C:/tmp/testdocs/SharePoint_icu4j.txt";
		File file = new File(inputFile);		
		String filename = file.getPath();
		
		Parser parser = ParserFactory.getParser(filename);
		PDFParser pdfp = (PDFParser) parser;
		pdfp.parse(file);
		
		String author = pdfp.getAuthor();
		System.out.println("author: " + author);
		assertTrue(StringUtils.isNotEmpty(author));
		assertEquals("wael", author);
	
		String content = pdfp.getContent();
		assertNotNull(content);
		assertTrue(StringUtils.isNotEmpty(content));

		System.err.println("content.length(): " + content.length());
		assertTrue(content.length() == 8598);
		System.out.println("content : " + content);
		
//		try {
//			FileOutputStream out = new FileOutputStream(outputFile);
//			BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//			BW.write(content);			
//			BW.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}		

}
